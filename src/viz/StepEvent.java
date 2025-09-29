package viz;

import grid.RC;
import java.util.List;

public final class StepEvent {
    public enum Type { QUEUE, VISIT, UPDATE, DONE }

    public final Type type;
    public final RC cell;              // לתור/ביקור/עדכון
    public final List<RC> pathIfDone;  // במסלול סופי
    public StepEvent(Type t, RC cell, List<RC> pathIfDone){
        this.type=t; this.cell=cell; this.pathIfDone=pathIfDone;
    }
    public static StepEvent queue(RC rc){ return new StepEvent(Type.QUEUE, rc, null); }
    public static StepEvent visit(RC rc){ return new StepEvent(Type.VISIT, rc, null); }
    public static StepEvent update(RC rc){ return new StepEvent(Type.UPDATE, rc, null); }
    public static StepEvent done(List<RC> path){ return new StepEvent(Type.DONE, null, path); }
}
