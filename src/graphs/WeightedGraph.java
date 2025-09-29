package graphs;

public interface WeightedGraph<V> extends Graph<V> {
    void addEdge(V u, V v, double w);
    double weightOf(V u, V v);  // IllegalArgumentException אם אין קשת
}
