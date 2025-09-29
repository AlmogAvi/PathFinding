package viz;

import grid.RC;
import graphs.*;
import java.util.*;

public class AStarStepper implements Stepper {
    private final WeightedGraph<RC> g;
    private final RC start, goal;
    private final Heuristic<RC> h;
    private final double w; // 1.0 = A*, >1.0 = Weighted A*

    private static class Node {
        RC v; double g, f;
        Node(RC v,double g,double f){ this.v=v; this.g=g; this.f=f; }
    }

    private final PriorityQueue<Node> open =
            new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
    private final Map<RC, Double> gScore = new HashMap<>();
    private final Map<RC, RC> parent = new HashMap<>();
    private final Set<RC> closed = new HashSet<>();
    private boolean started=false, finished=false;

    public AStarStepper(WeightedGraph<RC> g, RC start, RC goal, Heuristic<RC> h) {
        this(g, start, goal, h, 1.0);
    }
    public AStarStepper(WeightedGraph<RC> g, RC start, RC goal, Heuristic<RC> h, double w) {
        this.g=g; this.start=start; this.goal=goal; this.h=h; this.w=w;
    }

    @Override public boolean isFinished(){ return finished; }

    @Override public StepEvent step() {
        if (finished) return null;

        if (!started) {
            started = true;
            gScore.put(start, 0.0);
            double f0 = w * h.estimate(start, goal);
            open.add(new Node(start, 0.0, f0));
            parent.put(start, null);
            return StepEvent.queue(start);
        }

        if (open.isEmpty()) { finished=true; return StepEvent.done(Collections.emptyList()); }

        Node cur = open.poll();
        if (closed.contains(cur.v)) return null;
        closed.add(cur.v);

        if (cur.v.equals(goal)) {
            finished=true;
            return StepEvent.done(reconstruct(cur.v));
        }

        StepEvent ev = StepEvent.visit(cur.v);

        for (RC nb : g.neighborsOf(cur.v)) {
            if (closed.contains(nb)) continue;
            double wEdge = g.weightOf(cur.v, nb);
            double ng = cur.g + wEdge;
            Double old = gScore.get(nb);
            if (old == null || ng < old) {
                gScore.put(nb, ng);
                parent.put(nb, cur.v);
                double nf = ng + w * h.estimate(nb, goal);
                open.add(new Node(nb, ng, nf));
                ev = StepEvent.queue(nb);
            }
        }
        return ev;
    }

    private List<RC> reconstruct(RC meet){
        List<RC> path = new ArrayList<>();
        RC cur = meet;
        while (cur != null) { path.add(cur); cur = parent.get(cur); }
        Collections.reverse(path);
        return path;
    }

    @Override public void reset() {
        open.clear(); gScore.clear(); parent.clear(); closed.clear();
        started=false; finished=false;
    }
}
