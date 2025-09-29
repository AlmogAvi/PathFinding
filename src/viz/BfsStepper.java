package viz;

import grid.*;
import graphs.*;
import java.util.*;

public class BfsStepper implements Stepper {
    private final WeightedGraph<RC> g;
    private final RC start, goal;
    private final Queue<RC> q = new ArrayDeque<>();
    private final Set<RC> visited = new HashSet<>();
    private final Map<RC, RC> parent = new HashMap<>();
    private boolean started=false, finished=false;

    public BfsStepper(WeightedGraph<RC> g, RC start, RC goal){
        this.g=g; this.start=start; this.goal=goal;
    }

    @Override public boolean isFinished(){ return finished; }

    @Override public StepEvent step(){
        if (finished) return null;

        if (!started) {
            started = true;
            visited.add(start);
            q.add(start);
            return StepEvent.queue(start);
        }

        if (q.isEmpty()) {
            finished = true;
            return StepEvent.done(Collections.emptyList());
        }

        RC u = q.poll();
        // ביקור
        if (u.equals(goal)) {
            finished = true;
            var path = BFS.reconstructPath(parent, start, goal);
            if (path.isEmpty() && !start.equals(goal)) return StepEvent.done(Collections.emptyList());
            return StepEvent.done(path);
        }
        // מסמן "מבוקר"
        StepEvent event = StepEvent.visit(u);

        // הוספת שכנים חדשים לתור (אפשר לפזר על צעדים נפרדים אם תרצה)
        for (RC w : g.neighborsOf(u)) {
            if (!visited.contains(w)) {
                visited.add(w);
                parent.put(w, u);
                q.add(w);
                // כדי להראות גם הוספת תור, אפשר להחזיר מיד אירוע queue ולשמור את u ל"להבקר" בצעד הבא.
            }
        }
        return event;
    }

    @Override public void reset(){
        q.clear(); visited.clear(); parent.clear();
        started=false; finished=false;
    }
}
