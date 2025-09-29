package viz;

import grid.RC;
import graphs.*;
import java.util.*;

public class GreedyStepper implements Stepper {
    private final WeightedGraph<RC> g;
    private final RC start, goal;
    private final Heuristic<RC> h;
    private final double lambda; // ענישת עלות צעד (0 = Greedy קלאסי)

    private static class Entry {
        RC v; double p;
        Entry(RC v,double p){ this.v=v; this.p=p; }
    }

    private final PriorityQueue<Entry> open =
            new PriorityQueue<>(Comparator.comparingDouble(e -> e.p));
    private final Map<RC, Double> bestP = new HashMap<>();
    private final Set<RC> visited = new HashSet<>();
    private final Set<RC> inOpen  = new HashSet<>();
    private final Map<RC,RC> parent = new HashMap<>();
    private boolean started=false, finished=false;

    public GreedyStepper(WeightedGraph<RC> g, RC start, RC goal, Heuristic<RC> h) {
        this(g, start, goal, h, 0.0);
    }
    public GreedyStepper(WeightedGraph<RC> g, RC start, RC goal, Heuristic<RC> h, double lambda) {
        this.g=g; this.start=start; this.goal=goal; this.h=h; this.lambda=lambda;
    }

    @Override public boolean isFinished(){ return finished; }

    @Override public StepEvent step() {
        if (finished) return null;

        if (!started) {
            started = true;
            double p0 = h.estimate(start, goal);
            open.add(new Entry(start, p0));
            bestP.put(start, p0);
            parent.put(start, null);
            inOpen.add(start);
            return StepEvent.queue(start);
        }

        if (open.isEmpty()) { finished=true; return StepEvent.done(Collections.emptyList()); }

        Entry curE = open.poll();
        RC u = curE.v;
        inOpen.remove(u);

        if (visited.add(u)) {
            if (u.equals(goal)) { finished=true; return StepEvent.done(reconstruct(u)); }

            StepEvent ev = StepEvent.visit(u);
            for (RC w : g.neighborsOf(u)) {
                if (visited.contains(w)) continue;
                double stepCost = g.weightOf(u, w);
                double prio = h.estimate(w, goal) + lambda * stepCost;

                Double old = bestP.get(w);
                if (!inOpen.contains(w) || prio < old) {
                    parent.put(w, u);
                    bestP.put(w, prio);
                    open.add(new Entry(w, prio));
                    inOpen.add(w);
                    ev = StepEvent.queue(w);
                }
            }
            return ev;
        }
        return null;
    }

    private List<RC> reconstruct(RC meet){
        List<RC> path = new ArrayList<>();
        RC cur = meet;
        while (cur != null) { path.add(cur); cur = parent.get(cur); }
        Collections.reverse(path);
        return path;
    }

    @Override public void reset(){
        open.clear(); bestP.clear(); visited.clear(); inOpen.clear(); parent.clear();
        started=false; finished=false;
    }
}
