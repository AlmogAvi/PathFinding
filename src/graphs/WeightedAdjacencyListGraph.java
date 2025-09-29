package graphs;

import java.util.*;

public class WeightedAdjacencyListGraph<V> extends AdjacencyListGraph<V> implements WeightedGraph<V> {
    // טבלת משקלים: לכל u -> (v -> weight)
    private final Map<V, Map<V, Double>> w = new HashMap<>();

    public WeightedAdjacencyListGraph(boolean directed) {
        super(directed);
    }

    @Override
    public void addVertex(V v) {
        super.addVertex(v);
        w.putIfAbsent(v, new HashMap<>());
    }

    @Override
    public void addEdge(V u, V v) {
        addEdge(u, v, 1.0); // כברירת מחדל, משקל 1.0
    }

    @Override
    public void addEdge(V u, V v, double weight) {
        if (weight < 0) throw new IllegalArgumentException("Negative weights not allowed for Dijkstra");
        super.addVertex(u);
        super.addVertex(v);
        // הוסף לשכנים הלוגיים (Graph base)
        // super.addEdge(u, v) יגדיל מונה קשתות וינהל דו-כיווניות אם צריך:
        super.addEdge(u, v);

        w.get(u).put(v, weight);
        if (!isDirected()) {
            w.get(v).put(u, weight);
        }
    }

    @Override
    public double weightOf(V u, V v) {
        Map<V, Double> m = w.get(u);
        if (m == null || !m.containsKey(v)) throw new IllegalArgumentException("No such edge: " + u + "→" + v);
        return m.get(v);
    }
}
