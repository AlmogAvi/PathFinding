package graphs;

import java.util.*;

public class BFS {

    // BFS מהצומת source: מחזיר parent map
    public static <V> Map<V, V> run(Graph<V> g, V source) {
        Map<V, V> parent = new HashMap<>();
        Set<V> visited = new HashSet<>();
        Queue<V> q = new ArrayDeque<>();

        visited.add(source);
        q.add(source);

        while (!q.isEmpty()) {
            V u = q.poll();
            for (V w : g.neighborsOf(u)) {
                if (!visited.contains(w)) {
                    visited.add(w);
                    parent.put(w, u);
                    q.add(w);
                }
            }
        }
        return parent;
    }

    // שחזור מסלול source → target לפי parent
    public static <V> List<V> reconstructPath(Map<V, V> parent, V source, V target) {
        List<V> path = new ArrayList<>();
        V cur = target;

        while (cur != null && !cur.equals(source)) {
            path.add(cur);
            cur = parent.get(cur);
        }

        if (cur == null) {
            return Collections.emptyList(); // אין מסלול
        }

        path.add(source);
        Collections.reverse(path);
        return path;
    }
}
