package grid;

public class GridMap {
    private final int rows, cols;
    private final boolean[][] wall;
    private final double[][] cost;
    private double defaultCost = 1.0;

    // NEW: קונפיג אלכסונים + מניעת "חיתוך פינות"
    private boolean allowDiagonals = false;
    private boolean avoidCornerCut = true;

    public GridMap(int rows, int cols){
        this.rows = rows; this.cols = cols;
        this.wall = new boolean[rows][cols];
        this.cost = new double[rows][cols];
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) cost[r][c] = defaultCost;
    }

    public int rows(){ return rows; }
    public int cols(){ return cols; }

    public boolean isWall(int r,int c){ return wall[r][c]; }
    public void setWall(int r,int c, boolean isWall){ wall[r][c] = isWall; }

    public double getCost(int r,int c){ return cost[r][c]; }
    public void setCost(int r,int c, double value){
        if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value))
            throw new IllegalArgumentException("cost must be positive finite");
        cost[r][c] = value;
    }

    // NEW: דגלים
    public boolean isAllowDiagonals(){ return allowDiagonals; }
    public void setAllowDiagonals(boolean b){ this.allowDiagonals = b; }

    public boolean isAvoidCornerCut(){ return avoidCornerCut; }
    public void setAvoidCornerCut(boolean b){ this.avoidCornerCut = b; }

    // NEW: עזר
    public boolean inBounds(int r,int c){ return r>=0 && r<rows && c>=0 && c<cols; }
    public boolean isWalkable(int r,int c){ return inBounds(r,c) && !isWall(r,c); }

    // עלות מינימלית קיימת (ל-A* אדמיסבילי/Greedy scaled)
    public double minWalkableCost(){
        double m = Double.POSITIVE_INFINITY;
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++)
            if (!wall[r][c]) m = Math.min(m, cost[r][c]);
        return (m == Double.POSITIVE_INFINITY ? 1.0 : m);
    }
}
