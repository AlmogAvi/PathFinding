package graphs;

import java.util.*;

public class Dijkstra {

    public static <V> Result<V> run(WeightedGraph<V> g, V source) {
        Map<V, Double> dist = new HashMap<>();
        Map<V, V> parent = new HashMap<>();

        // אתחול
        for (V v : g.vertices()) dist.put(v, Double.POSITIVE_INFINITY);
        if (!g.containsVertex(source)) throw new IllegalArgumentException("Source not in graph");
        dist.put(source, 0.0);

        // תור עדיפויות לפי מרחק נוכחי
        PriorityQueue<V> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(source);

        Set<V> settled = new HashSet<>();

        while (!pq.isEmpty()) {
            V u = pq.poll();
            if (!settled.add(u)) continue; // אם כבר החזרנו u בעבר, דלג
            for (V w : g.neighborsOf(u)) {
                double alt = dist.get(u) + g.weightOf(u, w);
                if (alt < dist.get(w)) {
                    dist.put(w, alt);
                    parent.put(w, u);
                    pq.add(w);
                }
            }
        }
        return new Result<>(dist, parent);
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
        public final Map<V, Double> dist;
        public final Map<V, V> parent;
        public Result(Map<V, Double> dist, Map<V, V> parent) {
            this.dist = dist; this.parent = parent;
        }
    }
}
