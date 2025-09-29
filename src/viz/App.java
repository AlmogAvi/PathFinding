package viz;

import grid.*;
import graphs.*;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    // ===== מודל =====
    private final GridMap map;
    private RC start, goal;

    // ===== תצוגה =====
    private final GridPanel panel;

    // ===== בקר =====
    private final JComboBox<Algo> algoBox;
    private final JSlider speed;
    private final JButton runBtn, stepBtn, resetBtn, mazeBtn, rebuildBtn;
    private final JLabel status;

    private JCheckBox diagBox;
    private JCheckBox cornerCutBox;

    // NEW: סליידרים ל-A* ול-Greedy
    private JSlider astarWSlider;       // 100..300  => 1.00..3.00
    private JLabel  astarWLabel;
    private JSlider greedyLambdaSlider;  // 0..300    => 0.00..3.00
    private JLabel  greedyLambdaLabel;

    // ===== ריצה =====
    private final Timer timer;
    private Stepper stepper;
    private int steps = 0;

    public App() {
        super("AlgoViz – Grid Pathfinding");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        map = new GridMap(15, 28);
        start = new RC(6, 2);
        goal  = new RC(6, 25);

        panel = new GridPanel(map, start, goal);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        JPanel top = new JPanel();

        algoBox = new JComboBox<>(Algo.values());
        algoBox.addActionListener(e -> {
            stopRun();
            panel.clearStates();
            buildStepper();
            updateAlgoTuningControls();
        });

        speed     = new JSlider(0, 120, 40);
        runBtn    = new JButton("הרצה");
        stepBtn   = new JButton("צעד");
        resetBtn  = new JButton("איפוס");
        mazeBtn   = new JButton("מבוך אקראי");
        rebuildBtn= new JButton("בנה גרף");

        diagBox = new JCheckBox("Diagonals");
        diagBox.setSelected(false);
        diagBox.addActionListener(e -> { map.setAllowDiagonals(diagBox.isSelected()); rebuildAndReset(); });

        cornerCutBox = new JCheckBox("Avoid corner cut");
        cornerCutBox.setSelected(true);
        cornerCutBox.addActionListener(e -> { map.setAvoidCornerCut(cornerCutBox.isSelected()); rebuildAndReset(); });

        // --- סליידר A* (w) ---
        astarWSlider = new JSlider(100, 300, 100); // 1.00 ברירת מחדל (A* רגיל)
        astarWSlider.setToolTipText("A* weight (w): 1.00–3.00");
        astarWLabel  = new JLabel("A* w: 1.00");
        astarWSlider.addChangeListener(e -> {
            double w = getAStarW();
            astarWLabel.setText(String.format("A* w: %.2f", w));
            if (!astarWSlider.getValueIsAdjusting() && algoBox.getSelectedItem() == Algo.ASTAR) {
                rebuildAndReset();
            }
        });

        // --- סליידר Greedy (λ) ---
        greedyLambdaSlider = new JSlider(0, 300, 100); // 1.00 ברירת מחדל
        greedyLambdaSlider.setToolTipText("Greedy λ: 0.00–3.00 (penalize step cost)");
        greedyLambdaLabel  = new JLabel("Greedy λ: 1.00");
        greedyLambdaSlider.addChangeListener(e -> {
            double lam = getGreedyLambda();
            greedyLambdaLabel.setText(String.format("Greedy λ: %.2f", lam));
            if (!greedyLambdaSlider.getValueIsAdjusting() && algoBox.getSelectedItem() == Algo.GREEDY) {
                rebuildAndReset();
            }
        });

        top.add(new JLabel("אלגוריתם:")); top.add(algoBox);
        top.add(new JLabel("מהירות:"));   top.add(speed);
        top.add(runBtn); top.add(stepBtn); top.add(resetBtn);
        top.add(mazeBtn); top.add(rebuildBtn);
        top.add(diagBox); top.add(cornerCutBox);
        // תוספת טיונינג
        top.add(astarWLabel); top.add(astarWSlider);
        top.add(greedyLambdaLabel); top.add(greedyLambdaSlider);

        add(top, BorderLayout.NORTH);

        status = new JLabel("מוכן");
        add(status, BorderLayout.SOUTH);

        timer = new Timer(80, e -> doStep());
        speed.addChangeListener(e -> timer.setDelay(Math.max(1, 130 - speed.getValue())));
        runBtn.addActionListener(e -> toggleRun());
        stepBtn.addActionListener(e -> doStep());
        resetBtn.addActionListener(e -> { stopRun(); panel.clearStates(); buildStepper(); status.setText("נוקה והוכן להרצה"); });
        rebuildBtn.addActionListener(e -> { stopRun(); panel.clearStates(); buildStepper(); status.setText("נבנה גרף מחדש"); });
        mazeBtn.addActionListener(e -> { stopRun(); randomMaze(); panel.clearStates(); buildStepper(); status.setText("מבוך אקראי"); });

        setupSmallContrastScenario();
        buildStepper();
        updateAlgoTuningControls();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateAlgoTuningControls() {
        Algo a = (Algo) algoBox.getSelectedItem();
        boolean astar = a == Algo.ASTAR;
        boolean greedy = a == Algo.GREEDY;
        astarWSlider.setEnabled(astar);
        astarWLabel.setEnabled(astar);
        greedyLambdaSlider.setEnabled(greedy);
        greedyLambdaLabel.setEnabled(greedy);
    }

    private double getAStarW() {
        return astarWSlider.getValue() / 100.0;
    }
    private double getGreedyLambda() {
        return greedyLambdaSlider.getValue() / 100.0;
    }

    private void rebuildAndReset(){
        buildStepper();
        panel.clearStates();
        steps = 0;
        status.setText("מוכן");
        panel.repaint();
    }

    private void toggleRun() {
        if (timer.isRunning()) { stopRun(); }
        else { if (stepper == null) buildStepper(); timer.start(); runBtn.setText("עצור"); status.setText("רץ…"); }
    }
    private void stopRun() {
        timer.stop(); runBtn.setText("הרצה"); status.setText("נעצר");
    }

    private void doStep() {
        if (stepper == null) buildStepper();
        if (stepper.isFinished()) { stopRun(); status.setText("הסתיים"); return; }
        var ev = stepper.step();
        panel.apply(ev);
        if (ev != null && ev.type == StepEvent.Type.DONE && ev.pathIfDone != null) {
            int steps = Math.max(0, ev.pathIfDone.size()-1);
            double cost = 0.0;
            for (int i=1; i<ev.pathIfDone.size(); i++) {
                RC v = ev.pathIfDone.get(i);
                cost += map.getCost(v.r, v.c);
            }
            status.setText("צעדים: " + steps + " | עלות: " + String.format("%.2f", cost));
        }
    }

    /** בוחר Stepper לפי האלגוריתם ומציב היוריסטיקה + פרמטרים */
    private void buildStepper() {
        WeightedGraph<RC> g = GridGraphBuilder.build(map);
        Algo a = (Algo) algoBox.getSelectedItem();
        switch (a) {
            case BFS      -> stepper = new BfsStepper(g, start, goal);
            case DFS      -> stepper = new DfsStepper(g, start, goal);
            case DIJKSTRA -> stepper = new DijkstraStepper(g, start, goal);
            case ASTAR -> {
                double minCost = map.minWalkableCost();
                Heuristic<RC> h = map.isAllowDiagonals()
                        ? Heuristics.octileScaled(minCost)
                        : Heuristics.manhattanScaled(minCost);
                stepper = new AStarStepper(g, start, goal, h, getAStarW());
            }
            case GREEDY -> {
                double minCost = map.minWalkableCost();
                Heuristic<RC> h = map.isAllowDiagonals()
                        ? Heuristics.octileScaled(minCost)
                        : Heuristics.manhattanScaled(minCost);
                stepper = new GreedyStepper(g, start, goal, h, getGreedyLambda());
            }
            case BIBFS -> stepper = new BidirectionalBfsStepper(g, start, goal);
        }
    }

    private void randomMaze() {
        for (int r=0; r<map.rows(); r++)
            for (int c=0; c<map.cols(); c++) {
                map.setWall(r,c, Math.random() < 0.18);
                map.setCost(r,c, 1.0);
            }
        int highwayRow = map.rows()-1;
        for (int c=0; c<map.cols(); c++)
            if (!map.isWall(highwayRow,c)) map.setCost(highwayRow,c,0.5);
        int mid = map.cols()/2;
        for (int r=0; r<map.rows(); r++)
            if (!map.isWall(r,mid)) map.setCost(r,mid,8.0);
        map.setWall(start.r,start.c,false);
        map.setWall(goal.r,  goal.c, false);
        panel.clearStates(); panel.repaint();
    }

    private void setupSmallContrastScenario() {
        for (int r=0; r<map.rows(); r++)
            for (int c=0; c<map.cols(); c++) {
                map.setWall(r,c,false);
                map.setCost(r,c,1.0);
            }
        for (int r=4; r<=8; r++)
            for (int c=8; c<=18; c++)
                map.setCost(r,c, 50.0);
        int highway = map.rows()-2;
        for (int c=0; c<map.cols(); c++)
            map.setCost(highway, c, 0.2);
        for (int r=0; r<=6; r++)  map.setWall(r, 7,  true);
        for (int r=8; r<=13; r++) map.setWall(r, 19, true);
        map.setWall(start.r,start.c,false);
        map.setWall(goal.r, goal.c, false);
        panel.clearStates(); panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
