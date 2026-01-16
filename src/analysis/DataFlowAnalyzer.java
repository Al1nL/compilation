package analysis;

import java.util.*;

import ir.IrCommandLabel;
import variable.Variable;

public class DataFlowAnalyzer {

    public static Set<Variable> analyze(List<CFGNode> cfg, List<Variable> allVars) {
        if (cfg.isEmpty()) {
            return Collections.emptySet();
        }

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
                // Compute IN as meet over predecessors
                Map<Variable, Boolean> newIn = node == entry ? entry.in : meet(node.preds, allVars);

                // Compute OUT using node command
                Map<Variable, Boolean> newOut = node.cmd.computeOutSet(new HashSet<>(), newIn);

                if (!newIn.equals(node.in) || !newOut.equals(node.out)) {
                    Dbg.p("node#" + idx + " " + node.cmd.getClass().getSimpleName());
                    if(node.cmd instanceof IrCommandLabel){
                        IrCommandLabel lbl = (IrCommandLabel) node.cmd;
                        Dbg.p("name# " + lbl.getLabelName());
                    }
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
                // If predecessor's OUT is null, treat as "unknown / ignore"
                if (p.out != null && !p.out.isEmpty()) {
                    val &= p.out.getOrDefault(v, false);
                }
                // else ignore this predecessor (back edge not computed yet)
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
