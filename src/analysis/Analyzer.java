package analysis;

import java.util.*;
import temp.Temp;
import variable.Variable;

public class Analyzer {

    public static List<CFGNode> analyze(List<CFGNode> cfg, List<Variable> allVars) {
        if (cfg.isEmpty()) {
            return Collections.emptyList();
        }
        
        boolean changed = true;
        int iter = 0;
        Dbg.p("\n===== Starting analysis =====");
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
                    changed = true;
                    node.in = newIn;
                    node.out = newOut;
                }
            }
            printCfg(cfg);
            iter++;
        }
            Dbg.p("\n===== Finished analysis =====");

        // Return the annotated CFG
        return cfg;
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

    public static void printCfg(List<CFGNode> cfg) {
        for (int idx = 0; idx < cfg.size(); idx++) {
            CFGNode node = cfg.get(idx);
            Dbg.p("node#" + idx + ": " + node.cmd.getClass().getSimpleName());
            Dbg.p(" OUT: " + tempSetToStr(node.out));
            Dbg.p(" Def: " + tempSetToStr(node.cmd.getDefTemps()));
            Dbg.p(" Use: " + tempSetToStr(node.cmd.getUseTemps()));
            Dbg.p(" IN : " + tempSetToStr(node.in));
        }
    }

    private static String tempSetToStr(Set<Temp> temps) {
        if (temps.isEmpty()) return "{}";

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Temp t : temps) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(t.toString()); // uses physicalReg if set, else "tN"
        }
        sb.append("}");
        return sb.toString();
    }

    // todo - add deadcode elimination after reg allocation - some defs may become unused
}
