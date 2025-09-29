package grid;

import graphs.Heuristic;

public class Heuristics {
    /** Manhattan × minCost – מתאים ללא אלכסונים. */
    public static Heuristic<RC> manhattanScaled(double minCost){
        return (a, b) -> minCost * (Math.abs(a.r - b.r) + Math.abs(a.c - b.c));
    }

    /** Euclidean × minCost – שימושי אם עלות אלכסון היא √2. */
    public static Heuristic<RC> euclideanScaled(double minCost){
        return (a, b) -> {
            double dr = a.r - b.r, dc = a.c - b.c;
            return minCost * Math.sqrt(dr*dr + dc*dc);
        };
    }

    /** Octile × minCost – ההיוריסטיקה הקלאסית ל-8 שכנים עם √2. */
    public static Heuristic<RC> octileScaled(double minCost){
        return (a, b) -> {
            int dx = Math.abs(a.c - b.c), dy = Math.abs(a.r - b.r);
            int m = Math.min(dx, dy), M = Math.max(dx, dy);
            return minCost * (m * Math.sqrt(2.0) + (M - m));
        };
    }
}
