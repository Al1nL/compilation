package regalloc;

import analysis.CFGNode;
import java.util.*;
import temp.Temp;

public class InterferenceGraphBuilder {
    
    /**
     * Build interference graph from CFG with liveness information.
     * 
     * Algorithm:
     * For each node n in CFG:
     *   If t1 and t2 appear in the same IN[n] set:
     *     Create an edge between t1 and t2
     * 
     * @param cfg List of CFG nodes with liveness info (in/out sets)
     * @return The interference graph
     */
    public static InterferenceGraph build(List<CFGNode> cfg) {
        InterferenceGraph graph = new InterferenceGraph();
        
        for (CFGNode node : cfg) {
            Set<Temp> liveIn = node.in;
            
            // Add all live temps as nodes
            for (Temp temp : liveIn) {
                graph.addNode(temp);
            }
            
            // Add interference edges between all pairs in liveIn   
            addInterferencesForLiveSet(graph, liveIn);
        }
        
        return graph;
    }
    
    /**
     * Add interference edges between all pairs in the live set
     */
    private static void addInterferencesForLiveSet(
            InterferenceGraph graph, 
            Set<Temp> liveTemps) {
        
        List<Temp> tempList = new ArrayList<>(liveTemps);
        for (int i = 0; i < tempList.size(); i++) {
            for (int j = i + 1; j < tempList.size(); j++) {
                graph.addEdge(tempList.get(i), tempList.get(j));
            }
        }
    }
    
}