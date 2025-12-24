package analysis;

import ir.IrCommand;
import variable.Variable;
import java.util.*;

public class CFGNode {

    /** The IR instruction at this program point */
    public final IrCommand cmd;

    /** Control-flow edges */
    public final Set<CFGNode> preds = new HashSet<>();
    public final Set<CFGNode> succs = new HashSet<>();

    /** Dataflow state */
    public Map<Variable, Boolean> in;
    public Map<Variable, Boolean> out;

    public CFGNode(IrCommand cmd) {
        this.cmd = cmd;
        this.in = new HashMap<>();
        this.out = new HashMap<>();
    }

    public void addSucc(CFGNode succ) {
        this.succs.add(succ);
        succ.preds.add(this);
    }
}
