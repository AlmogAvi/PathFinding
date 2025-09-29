package viz;

import grid.RC;
import graphs.Graph;
import graphs.DFS;
import java.util.*;

public class DfsStepper implements Stepper {
    private final Graph<RC> g;
    private final RC start, goal;
    private final Deque<RC> st = new ArrayDeque<>();
    private final Set<RC> visited = new HashSet<>();
    private final Map<RC,RC> parent = new HashMap<>();
    private boolean started=false, finished=false;

    public DfsStepper(Graph<RC> g, RC start, RC goal){
        this.g=g; this.start=start; this.goal=goal;
    }
    @Override public boolean isFinished(){ return finished; }

    @Override public StepEvent step(){
        if(finished) return null;
        if(!started){ started=true; st.push(start); return StepEvent.queue(start); }
        if(st.isEmpty()){ finished=true; return StepEvent.done(Collections.emptyList()); }
        RC u = st.pop();
        if(!visited.add(u)) return StepEvent.update(u);
        if(u.equals(goal)){
            finished=true;
            var path=DFS.reconstructPath(parent,start,goal);
            if(path.isEmpty() && !start.equals(goal)) return StepEvent.done(Collections.emptyList());
            return StepEvent.done(path);
        }
        StepEvent ev = StepEvent.visit(u);
        List<RC> nbs = new ArrayList<>(g.neighborsOf(u));
        Collections.reverse(nbs);
        for(RC w : nbs) if(!visited.contains(w)){ parent.put(w,u); st.push(w); }
        return ev;
    }

    @Override public void reset(){
        st.clear(); visited.clear(); parent.clear(); started=false; finished=false;
    }
}
