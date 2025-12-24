package analysis;

import ir.*;
import java.util.*;
import variable.Variable;

public class CFGBuilder {

    /**
     * Builds a CFG from a linear list of IR commands.
     */
    private final Set<Variable> allVars = new HashSet<>();

    public Set<Variable> getAllVariables() {
        return allVars;
    }

    public List<CFGNode> build(List<IrCommand> ir) {

        List<CFGNode> nodes = new ArrayList<>();
        Map<String, CFGNode> labelMap = new HashMap<>();

        /* Create nodes and label map */
        for (IrCommand cmd : ir) {
            CFGNode node = new CFGNode(cmd);
            nodes.add(node);

            if (cmd instanceof IrCommandLabel) {
                IrCommandLabel lbl = (IrCommandLabel) cmd;
                labelMap.put(lbl.getLabelName(), node);
            }
        }

        /* Add control-flow edges */
        for (int i = 0; i < nodes.size(); i++) {

            CFGNode curr = nodes.get(i);
            IrCommand cmd = curr.cmd;

            /* RETURN has no successors */
            if (cmd instanceof IrCommandReturn) {
                continue;
            }

            /* Unconditional jump */
            if (cmd instanceof IrCommandJumpLabel) {
                IrCommandJumpLabel j = (IrCommandJumpLabel) cmd;
                CFGNode target = labelMap.get(j.getLabelName());
                addEdge(curr, target);
                continue;
            }

            /* Conditional jump */
            if (cmd instanceof IrCommandJumpIfEqToZero) {
                IrCommandJumpIfEqToZero j
                        = (IrCommandJumpIfEqToZero) cmd;

                // jump edge
                CFGNode target = labelMap.get(j.getLabelName());
                addEdge(curr, target);

                // fall-through edge
                if (i + 1 < nodes.size()) {
                    addEdge(curr, nodes.get(i + 1));
                }
                continue;
            }
            if (cmd instanceof IrCommandStore s) {
                allVars.add(s.var);
            }

            if (cmd instanceof IrCommandLoad l) {
                allVars.add(l.var);
            }

            /* Default: fall-through */
            if (i + 1 < nodes.size()) {
                addEdge(curr, nodes.get(i + 1));
            }
        }
        for (int i = 0; i < nodes.size(); i++) {
            CFGNode n = nodes.get(i);
            Dbg.p("CFG node #" + i + " cmd=" + n.cmd.getClass().getSimpleName());

            Dbg.p("  succs: " + n.succs.stream()
                    .map(s -> Integer.toString(nodes.indexOf(s)))
                    .toList());

            Dbg.p("  preds: " + n.preds.stream()
                    .map(p -> Integer.toString(nodes.indexOf(p)))
                    .toList());
        }

        return nodes;
    }

    private static void addEdge(CFGNode from, CFGNode to) {
        from.succs.add(to);
        to.preds.add(from);
    }
}
