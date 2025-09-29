package graphs;

import java.util.*;

public class AStar {

    public static <V> Result<V> run(WeightedGraph<V> g, V source, V target, Heuristic<V> h) {
        Map<V, Double> gScore = new HashMap<>(); // מרחק אמיתי מהמקור
        Map<V, Double> fScore = new HashMap<>(); // g + h
        Map<V, V> parent = new HashMap<>();

        for (V v : g.vertices()) {
            gScore.put(v, Double.POSITIVE_INFINITY);
            fScore.put(v, Double.POSITIVE_INFINITY);
        }
        if (!g.containsVertex(source) || !g.containsVertex(target))
            throw new IllegalArgumentException("Source/Target not in graph");

        gScore.put(source, 0.0);
        fScore.put(source, h.estimate(source, target));

        PriorityQueue<V> open = new PriorityQueue<>(Comparator.comparingDouble(fScore::get));
        open.add(source);
        Set<V> closed = new HashSet<>();

        while (!open.isEmpty()) {
            V u = open.poll();
            if (u.equals(target)) {
                return new Result<>(gScore, parent); // מצאנו דרך, parent/gScore תקפים
            }
            if (!closed.add(u)) continue;

            for (V w : g.neighborsOf(u)) {
                if (closed.contains(w)) continue;
                double tentative = gScore.get(u) + g.weightOf(u, w);
                if (tentative < gScore.get(w)) {
                    parent.put(w, u);
                    gScore.put(w, tentative);
                    fScore.put(w, tentative + h.estimate(w, target));
                    open.add(w);
                }
            }
        }
        return new Result<>(gScore, parent); // אם אין מסלול: reconstruct יחזיר ריק
    }

    public static <V> List<V> reconstructPath(Map<V,V> parent, V source, V target) {
        List<V> path = new ArrayList<>();
        V cur = target;
        while (cur != null && !cur.equals(source)) {
            path.add(cur);
            cur = parent.get(cur);
        }
        if (cur == null) return Collections.emptyList();
        path.add(source);
        Collections.reverse(path);
        return path;
    }

    public static final class Result<V> {
        public final Map<V, Double> gScore;
        public final Map<V, V> parent;
        public Result(Map<V, Double> gScore, Map<V, V> parent) {
            this.gScore = gScore; this.parent = parent;
        }
    }
}
