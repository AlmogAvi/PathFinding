package grid;

public final class RC {
    public final int r, c;
    public RC(int r, int c){ this.r=r; this.c=c; }

    @Override public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof RC)) return false;
        RC x=(RC)o; return r==x.r && c==x.c;
    }
    @Override public int hashCode(){ return 31*r + c; }
    @Override public String toString(){ return "(" + r + "," + c + ")"; }
}
