package viz;

import grid.RC;
import graphs.*;
import java.util.*;

public class DijkstraStepper implements Stepper {
    private final WeightedGraph<RC> g;
    private final RC start, goal;
    private final Map<RC,Double> dist = new HashMap<>();
    private final Map<RC,RC> parent = new HashMap<>();
    private final PriorityQueue<RC> pq;
    private final Set<RC> settled = new HashSet<>();
    private boolean started=false, finished=false;

    public DijkstraStepper(WeightedGraph<RC> g, RC start, RC goal){
        this.g=g; this.start=start; this.goal=goal;
        for(RC v : g.vertices()) dist.put(v, Double.POSITIVE_INFINITY);
        pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
    }

    @Override public boolean isFinished(){ return finished; }

    @Override public StepEvent step(){
        if(finished) return null;
        if(!started){ started=true; dist.put(start,0.0); pq.add(start); return StepEvent.queue(start); }
        if(pq.isEmpty()){ finished=true; return StepEvent.done(Collections.emptyList()); }
        RC u = pq.poll();
        if(!settled.add(u)) return StepEvent.update(u);
        if(u.equals(goal)){
            finished=true;
            var path=Dijkstra.reconstructPath(parent,start,goal);
            return StepEvent.done(path);
        }
        StepEvent ev = StepEvent.visit(u);
        for(RC w : g.neighborsOf(u)){
            double alt = dist.get(u) + g.weightOf(u,w);
            if(alt < dist.get(w)){
                dist.put(w,alt);
                parent.put(w,u);
                pq.add(w);
            }
        }
        return ev;
    }

    @Override public void reset(){
        pq.clear(); settled.clear(); parent.clear();
        dist.replaceAll((k,v)->Double.POSITIVE_INFINITY);
        started=false; finished=false;
    }
}
