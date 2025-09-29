package graphs;

@FunctionalInterface
public interface Heuristic<V> {
    double estimate(V a, V b);
}
