package analysis;

import ir.IrCommand;
import java.util.*;
import temp.Temp;

public class CFGNode {

    /* The IR instruction at this program point */
    public final IrCommand cmd;

    /* Control-flow edges */
    public final Set<CFGNode> preds = new HashSet<>();
    public final Set<CFGNode> succs = new HashSet<>();

    /* Dataflow state */
    public Set<Temp> in;
    public Set<Temp> out;

    public CFGNode(IrCommand cmd) {
        this.cmd = cmd;
        this.in = new HashSet<>();
        this.out = new HashSet<>();
    }

    public void addSucc(CFGNode succ) {
        this.succs.add(succ);
        succ.preds.add(this);
    }
}
