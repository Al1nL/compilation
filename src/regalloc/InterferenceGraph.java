package regalloc;

import java.util.*;
import temp.Temp;

/**
 * Interference graph for register allocation.
 * Two temporaries interfere if they are live at the same time.
 */
public class InterferenceGraph {
    private final Map<Temp, Set<Temp>> adjList;
    private final Set<Temp> allTemps;
    
    public InterferenceGraph() {
        this.adjList = new HashMap<>();
        this.allTemps = new HashSet<>();
    }
    
    /**
     * Add a temporary to the graph
     */
    public void addNode(Temp temp) {
        allTemps.add(temp);
        adjList.putIfAbsent(temp, new HashSet<>());
    }
    
    /**
     * Add an interference edge between two temporaries
     */
    public void addEdge(Temp t1, Temp t2) {
        if (t1.equals(t2)) return; // No self-interference
        
        addNode(t1);
        addNode(t2);
        adjList.get(t1).add(t2);
        adjList.get(t2).add(t1);
    }
    
    /**
     * Get all temporaries that interfere with the given temporary
     */
    public Set<Temp> getNeighbors(Temp temp) {
        return new HashSet<>(adjList.getOrDefault(temp, Collections.emptySet()));
    }
    
    /**
     * Get all temporaries in the graph
     */
    public Set<Temp> getAllTemps() {
        return new HashSet<>(allTemps);
    }
    
    /**
     * Get the degree of a temporary in the graph
     */
    public int getDegree(Temp temp) {
        return adjList.getOrDefault(temp, Collections.emptySet()).size();
    }
    
    /**
     * Remove a temporary from the graph
     */
    public void removeNode(Temp temp) {
        Set<Temp> neighbors = adjList.remove(temp);
        if (neighbors != null) {
            for (Temp neighbor : neighbors) {
                adjList.get(neighbor).remove(temp);
            }
        }
        allTemps.remove(temp);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n===== Interference Graph =====\n");
        List<Temp> sorted = new ArrayList<>(allTemps);
        sorted.sort(Comparator.comparingInt(Temp::getSerialNumber));
        
        for (Temp temp : sorted) {
            sb.append("t").append(temp.getSerialNumber())
              .append(" -> {");
            
            List<Temp> neighbors = new ArrayList<>(getNeighbors(temp));
            neighbors.sort(Comparator.comparingInt(Temp::getSerialNumber));
            
            for (int i = 0; i < neighbors.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("t").append(neighbors.get(i).getSerialNumber());
            }
            sb.append("}\n");
        }
        return sb.toString();
    }
}