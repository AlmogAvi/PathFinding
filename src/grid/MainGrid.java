package grid;

import graphs.*;

public class MainGrid {
    public static void main(String[] args) {
        GridMap m = new GridMap(7, 10);

        // קיר שחוסם נתיב קצר
        for (int r=1; r<=5; r++) m.setWall(r, 4, true);

        // אזור “בוץ” יקר (עלות 5) בין עמודות 6..8
        for (int r=0; r<m.rows(); r++)
            for (int c=6; c<=8; c++)
                if (!m.isWall(r,c)) m.setCost(r,c, 5.0);

        // “כביש מהיר” זול (עלות 0.5) בשורה 6
        for (int c=0; c<m.cols(); c++)
            if (!m.isWall(6,c)) m.setCost(6, c, 0.5);

        RC start = new RC(0,0);
        RC goal  = new RC(6,9);

        WeightedGraph<RC> g = GridGraphBuilder.build(m);

        // BFS מתעלם מעלויות → יחפש את הקצר בקפיצות
        var parentB = BFS.run(g, start);
        var pathB = BFS.reconstructPath(parentB, start, goal);
        System.out.println("BFS path len (steps): " + Math.max(0, pathB.size()-1));

        // Dijkstra – מוצא זול באמת (ייקח את הכביש המהיר)
        var resD = Dijkstra.run(g, start);
        var pathD = Dijkstra.reconstructPath(resD.parent, start, goal);
        System.out.println("Dijkstra path cost: " + resD.dist.get(goal));
        System.out.println("Dijkstra path: " + pathD);

        // A* עם היוריסטיקה מותאמת ל-minCost
        double minCost = m.minWalkableCost(); // כאן זה 0.5
        Heuristic<RC> h = Heuristics.manhattanScaled(minCost);
        var resA = AStar.run(g, start, goal, h);
        var pathA = AStar.reconstructPath(resA.parent, start, goal);
        System.out.println("A* path cost: " + resA.gScore.get(goal));
        System.out.println("A* path: " + pathA);
    }
}
