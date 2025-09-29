// AdjacencyListGraph.java
package graphs;

import java.util.*;

public class AdjacencyListGraph<V> implements Graph<V> {
    private final boolean directed;
    // שומרים סדר הכנסה של קודקודים ושכנים
    private final Map<V, Set<V>> adj = new LinkedHashMap<>();
    private int edges = 0;

    public AdjacencyListGraph(boolean directed) { this.directed = directed; }
    @Override public boolean isDirected() { return directed; }

    @Override
    public void addVertex(V v) {
        adj.computeIfAbsent(v, k -> new LinkedHashSet<>()); // סדר שכנים נשמר
    }

    @Override
    public void addEdge(V u, V v) {
        addVertex(u);
        addVertex(v);
        boolean added = adj.get(u).add(v);
        if (!directed) {
            boolean addedBack = adj.get(v).add(u);
            if (added || addedBack) edges++;
        } else {
            if (added) edges++;
        }
    }

    @Override
    public Collection<V> vertices() {
        return Collections.unmodifiableCollection(adj.keySet());
    }

    @Override
    public Collection<V> neighborsOf(V v) {
        return Collections.unmodifiableCollection(adj.getOrDefault(v, Collections.emptySet()));
    }

    @Override public boolean containsVertex(V v) { return adj.containsKey(v); }
    @Override public int vertexCount() { return adj.size(); }
    @Override public int edgeCount() { return edges; }
}
