package analysis;

import java.util.*;
import temp.Temp;
import variable.Variable;

public class DataFlowAnalyzer {

    public static Map<CFGNode, Set<Temp>> analyze(List<CFGNode> cfg, List<Variable> allVars) {
        if (cfg.isEmpty()) {
            return Collections.emptyMap();
        }
        
        boolean changed = true;
        int iter = 0;
        while (changed) {
            changed = false;
            Dbg.p("\n===== ITER " + iter + " =====");

            // Iterate backwards through CFG
            for (int idx = cfg.size() - 1; idx >= 0; idx--) {
                CFGNode node = cfg.get(idx);
                
                Set<Temp> newOut = meet(node.succs);

                // IN[n] = USE[n] ∪ (OUT[n] - DEF[n])
                Set<Temp> newIn = node.cmd.computeInSet(newOut);

        
                // Check if changed
                if (!newIn.equals(node.in) || !newOut.equals(node.out)) {
                    Dbg.p("node#" + idx + " " + node.cmd.getClass().getSimpleName());
                    Dbg.p("  OUT: " + tempSetToStr(newOut));
                    Dbg.p("Def: " + tempSetToStr(node.cmd.getDefTemps()));
                    Dbg.p("Use: " + tempSetToStr(node.cmd.getUseTemps()));
                    Dbg.p("  IN : " + tempSetToStr(newIn));
                    changed = true;
                    node.in = newIn;
                    node.out = newOut;
                }
            }
            iter++;
        }

        // Return mapping from nodes to their live-in sets
        Map<CFGNode, Set<Temp>> result = new HashMap<>();
        for (CFGNode node : cfg) {
            result.put(node, node.in);
        }
        return result;
    }
    
    /**
     * OUT[n] = ∪ IN[s] for all successors s of n
     */
    private static Set<Temp> meet(Set<CFGNode> succs) {
        Set<Temp> result = new HashSet<>();
        for (CFGNode succ : succs) {
            if (succ.in != null) {
                result.addAll(succ.in);
            }
        }
        return result;
    }

    private static String tempSetToStr(Set<Temp> temps) {
        if (temps.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Temp t : temps) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append("t").append(t.getSerialNumber());
        }
        sb.append("}");
        return sb.toString();
    }
}
