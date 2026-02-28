package regalloc;

import analysis.Dbg;
import java.util.*;
import temp.Temp;

/**
 * Simplification-based register allocator using graph coloring. Allocates
 * registers $t0-$t9 (10 registers available).
 *
 * Algorithm: 1. Build interference graph from liveness analysis 2. Simplify:
 * repeatedly remove nodes with degree < K 3. Select: assign colors to nodes in
 * reverse order 4. If any node cannot be colored, allocation fails
 */
public class RegisterAllocator {

    private static final int NUM_REGISTERS = 10; // $t0 through $t9
    private static final String[] REGISTER_NAMES = {
        "$t0", "$t1", "$t2", "$t3", "$t4",
        "$t5", "$t6", "$t7", "$t8", "$t9"
    };

    private final InterferenceGraph graph;
    private final Map<Temp, String> allocation;
    private final Stack<Temp> selectStack;

    /**
     * Main entry point for register allocation
     */
    public static Map<Temp, String> allocateRegisters(InterferenceGraph graph) {
        RegisterAllocator allocator = new RegisterAllocator(graph);
        Map<Temp, String> result = allocator.allocate();

        if (result == null) {
            // Allocation failed - print error and terminate
            System.err.println("Register Allocation Failed");
            System.exit(1);
        }

        return result;
    }

    public RegisterAllocator(InterferenceGraph graph) {
        this.graph = graph;
        this.allocation = new HashMap<>();
        this.selectStack = new Stack<>();
    }

    /**
     * Perform register allocation.
     *
     * @return Map from temporaries to register names, or null if allocation
     * fails
     */
    public Map<Temp, String> allocate() {
        // Make a working copy of the graph
        InterferenceGraph workGraph = copyGraph(graph);

        // Simplification
        if (!simplify(workGraph)) {
            return null; // Allocation failed
        }

        // Assign colors
        if (!select()) {
            return null; // Allocation failed
        }

        return allocation;
    }

    /**
     * Simplification phase: repeatedly remove nodes with degree < K and push
     * them onto a stack.
     *
     * @return true if simplification succeeds, false if we get stuck
     */
    private boolean simplify(InterferenceGraph workGraph) {
        selectStack.clear();

        while (!workGraph.getAllTemps().isEmpty()) {
            // Find a node with degree < K
            Temp lowDegreeNode = findLowDegreeNode(workGraph);

            if (lowDegreeNode == null) {
                // No node with degree < K found
                // This means allocation will fail
                System.err.println("Register Allocation Failed");
                System.err.println("Cannot simplify - all remaining nodes have degree >= " + NUM_REGISTERS);
                System.err.println("Remaining nodes: " + workGraph.getAllTemps().size());
                return false;
            }

            // Remove this node and push onto stack
            selectStack.push(lowDegreeNode);
            workGraph.removeNode(lowDegreeNode);
        }

        return true;
    }

    /**
     * Find a node with degree < K
     */
    private Temp findLowDegreeNode(InterferenceGraph workGraph) {
        for (Temp temp : workGraph.getAllTemps()) {
            if (workGraph.getDegree(temp) < NUM_REGISTERS) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Try assigning colors to nodes in reverse order. Pop nodes from
     * stack and assign them colors that don't conflict with their neighbors.
     *
     * @return true if selection succeeds, false if we can't color a node
     */
    private boolean select() {
        allocation.clear();

        while (!selectStack.isEmpty()) {
            Temp temp = selectStack.pop();

            // Find colors used by neighbors
            Set<String> usedColors = new HashSet<>();
            for (Temp neighbor : graph.getNeighbors(temp)) {
                String neighborColor = allocation.get(neighbor);
                if (neighborColor != null) {
                    usedColors.add(neighborColor);
                }
            }

            // Find an available color
            String color = findAvailableColor(usedColors);

            if (color == null) {
                // No color available - allocation failed
                System.err.println("Register Allocation Failed");
                System.err.println("Cannot color node: t" + temp.getSerialNumber());
                return false;
            }

            allocation.put(temp, color);
        }

        return true;
    }

    /**
     * Find an available register that's not in the used set
     */
    private String findAvailableColor(Set<String> usedColors) {
        for (String register : REGISTER_NAMES) {
            if (!usedColors.contains(register)) {
                return register;
            }
        }
        return null; // No color available
    }

    /**
     * Create a working copy of the interference graph
     */
    private InterferenceGraph copyGraph(InterferenceGraph original) {
        InterferenceGraph copy = new InterferenceGraph();
        for (Temp temp : original.getAllTemps()) {
            copy.addNode(temp);
        }
        for (Temp temp : original.getAllTemps()) {
            for (Temp neighbor : original.getNeighbors(temp)) {
                copy.addEdge(temp, neighbor);
            }
        }
        return copy;
    }

    /**
     * Print the allocation
     */
    public static void printAllocation(Map<Temp, String> allocation) {
        Dbg.p("\n===== Register Allocation =====\n");

        if (allocation.isEmpty()) {
            Dbg.p("No allocation available");
            return;
        }

        Map<Temp, String> temps = getAllocation(allocation);
        for (Temp temp : temps.keySet()) {
            Dbg.p("  t" + temp.getSerialNumber() + " -> " + temps.get(temp));
        }
    }

    /**
     * Get the allocation map sorted by temp serial number
     */
    public static Map<Temp, String> getAllocation(Map<Temp, String> alloc) {
        List<Temp> temps = new ArrayList<>(alloc.keySet());
        temps.sort(Comparator.comparingInt(Temp::getSerialNumber));
        Map<Temp, String> sortedAllocation = new LinkedHashMap<>();
        for (Temp temp : temps) {
            sortedAllocation.put(temp, alloc.get(temp));
        }
        return sortedAllocation;
    }
}
