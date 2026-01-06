package analysis;

import java.util.*;
import variable.Variable;

public class DataFlowAnalyzer {

    public static Set<Variable> analyze(List<CFGNode> cfg, List<Variable> allVars) {
        // Initialize IN/OUT maps
        for (CFGNode node : cfg) {
            node.in = new HashMap<>();
            node.out = new HashMap<>();
            for (Variable v : allVars) {
                node.in.put(v, false);  // initially uninitialized
                node.out.put(v, false); // initially uninitialized
            }
        }

        boolean changed = true;
        int iter = 0;
        while (changed) {
            changed = false;
            Dbg.p("\n===== ITER " + iter + " =====");

            for (int idx = 0; idx < cfg.size(); idx++) {
                CFGNode node = cfg.get(idx);
                // Compute IN as meet over predecessors
                Map<Variable, Boolean> newIn = meet(node.preds, allVars);

                // Compute OUT using node command
                Map<Variable, Boolean> newOut = node.cmd.computeOutSet(new HashSet<>(), newIn);

                if (!newIn.equals(node.in) || !newOut.equals(node.out)) {
                    Dbg.p("node#" + idx + " " + node.cmd.getClass().getSimpleName());
                    Dbg.p("  IN : " + mapToStr(newIn));
                    Dbg.p("  OUT: " + mapToStr(newOut));
                    changed = true;
                    node.in = newIn;
                    node.out = newOut;
                }
            }

            iter++;
        }

        // Collect used-before-initialization variables
        Set<Variable> usedBeforeSet = new HashSet<>();
        for (CFGNode node : cfg) {
            node.cmd.computeOutSet(usedBeforeSet, node.in);
        }

        return usedBeforeSet;
    }

    private static Map<Variable, Boolean> meet(Set<CFGNode> preds, List<Variable> universe) {
        Map<Variable, Boolean> result = new HashMap<>();
        for (Variable v : universe) {
            boolean val = true;
            for (CFGNode p : preds) {
                // If predecessor does not define v, treat as uninitialized (false)
                val &= p.out.getOrDefault(v, false);
            }
            result.put(v, val);
        }
        return result;
    }

    private static String mapToStr(Map<Variable, Boolean> m) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var e : m.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            Variable v = e.getKey();
            sb.append(v.name).append("@").append(v.scope).append("=").append(e.getValue());
        }
        sb.append("}");
        return sb.toString();
    }
}
