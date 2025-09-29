package graphs;

import java.util.Collection;

public interface Graph<V> {
    boolean isDirected();
    void addVertex(V v);
    void addEdge(V u, V v);        // גרף ללא משקלים בשלב זה
    Collection<V> vertices();
    Collection<V> neighborsOf(V v);
    boolean containsVertex(V v);
    int vertexCount();
    int edgeCount();
}
