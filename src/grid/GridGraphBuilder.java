package grid;

import graphs.*;

public class GridGraphBuilder {
    /** גרף ממושקל לא-מכוון. משקל = עלות כניסה ל-v. באלכסון יש פקטור √2. */
    public static WeightedGraph<RC> build(GridMap map) {
        WeightedGraph<RC> g = new WeightedAdjacencyListGraph<>(false);

        for (int r = 0; r < map.rows(); r++) {
            for (int c = 0; c < map.cols(); c++) {
                if (map.isWall(r, c)) continue;
                RC u = new RC(r, c);

                // 4 שכנים אורתוגונליים
                addIfFree(map, g, u, r-1, c); // up
                addIfFree(map, g, u, r, c+1); // right
                addIfFree(map, g, u, r+1, c); // down
                addIfFree(map, g, u, r, c-1); // left

                // 4 אלכסונים (אופציונלי)
                if (map.isAllowDiagonals()) {
                    addIfFreeDiagonal(map, g, u, r-1, c+1, r, c+1, r-1, c); // up-right
                    addIfFreeDiagonal(map, g, u, r+1, c+1, r, c+1, r+1, c); // down-right
                    addIfFreeDiagonal(map, g, u, r+1, c-1, r, c-1, r+1, c); // down-left
                    addIfFreeDiagonal(map, g, u, r-1, c-1, r, c-1, r-1, c); // up-left
                }
            }
        }
        return g;
    }

    private static void addIfFree(GridMap m, WeightedGraph<RC> g, RC u, int rr, int cc) {
        if (!m.inBounds(rr, cc) || m.isWall(rr, cc)) return;
        RC v = new RC(rr, cc);
        double w = m.getCost(rr, cc);
        g.addEdge(u, v, w);
    }

    private static void addIfFreeDiagonal(GridMap m, WeightedGraph<RC> g, RC u,
                                          int rr, int cc,
                                          int side1r, int side1c,
                                          int side2r, int side2c) {
        if (!m.inBounds(rr, cc) || m.isWall(rr, cc)) return;

        if (m.isAvoidCornerCut()) {
            boolean s1Blocked = !m.inBounds(side1r, side1c) || m.isWall(side1r, side1c);
            boolean s2Blocked = !m.inBounds(side2r, side2c) || m.isWall(side2r, side2c);
            if (s1Blocked && s2Blocked) return; // אל תחצה אם שתי הפינות חסומות
        }

        RC v = new RC(rr, cc);
        double w = m.getCost(rr, cc) * Math.sqrt(2.0); // פקטור √2 לאלכסון
        g.addEdge(u, v, w);
    }
}
