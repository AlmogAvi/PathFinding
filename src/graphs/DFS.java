package graphs;

import java.util.*;

public class DFS {

    // מחזיר סדר ביקור (Preorder) מרגע ההתחלה
    public static <V> List<V> dfsOrder(Graph<V> g, V source) {
        List<V> order = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();
        stack.push(source);

        while (!stack.isEmpty()) {
            V u = stack.pop();
            if (visited.add(u)) {
                order.add(u);
                // כדי לקבל התנהגות "יציבה", כדאי לדחוף שכנים בסדר הפוך
                List<V> neighbors = new ArrayList<>(g.neighborsOf(u));
                Collections.reverse(neighbors);
                for (V w : neighbors) stack.push(w);
            }
        }
        return order;
    }

    // בונה parent map לשחזור מסלול (לא קיים parent ל-source)
    public static <V> Map<V, V> dfsParents(Graph<V> g, V source) {
        Map<V, V> parent = new HashMap<>();
        Set<V> visited = new HashSet<>();
        dfsRec(g, source, visited, parent);
        return parent;
    }

    private static <V> void dfsRec(Graph<V> g, V u, Set<V> visited, Map<V, V> parent) {
        if (!visited.add(u)) return;
        for (V w : g.neighborsOf(u)) {
            if (!visited.contains(w)) {
                parent.put(w, u);
                dfsRec(g, w, visited, parent);
            }
        }
    }

    // שחזור מסלול לפי parent, כמו ב-BFS
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
}
