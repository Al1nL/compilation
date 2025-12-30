package analysis;

import java.util.*;
import variable.Variable;
import ir.*;

public class DataFlowAnalyzer {

    public static Set<Variable> analyze(
            List<CFGNode> cfg,
            Set<Variable> allVars
    ) {
        CFGNode entry = cfg.get(0);

        /* Entry IN = all vars not initiated yet */
        Map<Variable, Boolean> entryIn = new HashMap<>();
        for (Variable v : allVars) {
            entryIn.put(v, false);
        }
        entry.in = entryIn;

        boolean changed = true;
        int iter = 0;
        while (changed) {
            changed = false;
            Dbg.p("\n===== ITER " + iter + " =====");

            for (int idx = 0; idx < cfg.size(); idx++) {
                CFGNode node = cfg.get(idx);
                Map<Variable, Boolean> newIn
                        = node == entry ? entry.in : meet(node.preds);

                Map<Variable, Boolean> newOut
                        = node.cmd.computeOutSet(new HashSet<>(), newIn);

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
        // 3. Final Pass: Now that IN/OUT are stable, collect the actual errors
        Set<Variable> usedBeforeSet = new HashSet<>();
        for (CFGNode node : cfg) {
            node.cmd.computeOutSet(usedBeforeSet, node.in);
        }
        return usedBeforeSet;
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

    /* Meet over predecessors (AND) */
    private static Map<Variable, Boolean> meet(Set<CFGNode> preds) {

        Map<Variable, Boolean> result = new HashMap<>();

        if (preds.isEmpty()) {
            return result;
        }

        // Collect ALL variables appearing in ANY predecessor
        Set<Variable> universe = new HashSet<>();
        for (CFGNode p : preds) {
            universe.addAll(p.out.keySet());
        }

        // AND across ALL predecessors for EACH variable
        for (Variable v : universe) {
            boolean val = true;
            for (CFGNode p : preds) {
                val &= p.out.getOrDefault(v, true);
            }
            result.put(v, val);
        }

        return result;
    }

}
