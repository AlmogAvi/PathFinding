package viz;

import grid.RC;
import graphs.*;
import java.util.*;

public class BidirectionalBfsStepper implements Stepper {
    private final WeightedGraph<RC> g;
    private final RC start, goal;

    private final Queue<RC> qF = new ArrayDeque<>();
    private final Queue<RC> qB = new ArrayDeque<>();
    private final Set<RC> visF = new HashSet<>();
    private final Set<RC> visB = new HashSet<>();
    private final Map<RC,RC> parF = new HashMap<>();
    private final Map<RC,RC> parB = new HashMap<>();

    private boolean started=false, finished=false;
    private int initPhase = 0;  // 0: תור קדמי, 1: תור אחורי
    private boolean forwardTurn = true; // נחליף תורות

    public BidirectionalBfsStepper(WeightedGraph<RC> g, RC start, RC goal){
        this.g=g; this.start=start; this.goal=goal;
    }

    @Override public boolean isFinished(){ return finished; }

    @Override public StepEvent step() {
        if (finished) return null;

        if (!started) {
            started = true;
            if (start.equals(goal)) {
                finished=true;
                return StepEvent.done(Collections.singletonList(start));
            }
            qF.add(start); visF.add(start); parF.put(start, null);
            qB.add(goal);  visB.add(goal);  parB.put(goal, null);
            initPhase = 0;
            return StepEvent.queue(start);
        }

        // שלב שני של האתחול – כדי "להראות" גם את הצד האחורי נכנס לתור
        if (initPhase == 0) { initPhase = 1; return StepEvent.queue(goal); }

        // בחר צד להרחבה
        boolean doForward;
        if (qF.isEmpty()) doForward = false;
        else if (qB.isEmpty()) doForward = true;
        else doForward = forwardTurn = !forwardTurn; // סבב

        return doForward ? expandOne(qF, visF, parF, visB, true)
                : expandOne(qB, visB, parB, visF, false);
    }

    private StepEvent expandOne(Queue<RC> q,
                                Set<RC> visThis, Map<RC,RC> parThis,
                                Set<RC> visOther, boolean forwardSide) {
        if (q.isEmpty()) {
            if (qF.isEmpty() && qB.isEmpty()) { finished=true; return StepEvent.done(Collections.emptyList()); }
            return null;
        }

        RC u = q.poll();
        StepEvent ev = StepEvent.visit(u);

        for (RC w : g.neighborsOf(u)) {
            if (visThis.contains(w)) continue;
            visThis.add(w);
            parThis.put(w, u);
            q.add(w);

            // מפגש?
            if (visOther.contains(w)) {
                finished = true;
                List<RC> path = reconstruct(w);
                return StepEvent.done(path);
            }

            ev = StepEvent.queue(w); // נחזיר אירוע QUEUE אחרון אם אין DONE
        }
        return ev;
    }

    private List<RC> reconstruct(RC meet){
        // start -> meet
        List<RC> left = new ArrayList<>();
        RC cur = meet;
        while (cur != null) { left.add(cur); cur = parF.get(cur); }
        Collections.reverse(left); // start ... meet

        // goal -> meet
        List<RC> right = new ArrayList<>();
        cur = meet;
        while (cur != null) { right.add(cur); cur = parB.get(cur); }
        // right: meet ... goal  (נרצה לצרף מהאלמנט השני כדי לא לכפול את meet)
        Collections.reverse(right); // goal ... meet
        // נהפוך למ meet->goal
        Collections.reverse(right); // meet ... goal

        if (!right.isEmpty()) right.remove(0); // להסיר meet שכבר נמצא ב-left סוף
        left.addAll(right);
        return left;
    }

    @Override public void reset(){
        qF.clear(); qB.clear(); visF.clear(); visB.clear(); parF.clear(); parB.clear();
        started=false; finished=false; initPhase=0; forwardTurn=true;
    }
}
