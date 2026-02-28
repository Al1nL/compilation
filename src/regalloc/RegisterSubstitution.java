package regalloc;

import analysis.CFGNode;
import temp.Temp;
import java.util.*;

public class RegisterSubstitution {

    public static void apply(List<CFGNode> nodes, Map<Temp, String> allocation) {
        for (CFGNode node : nodes) {
            for (Temp t : node.cmd.getAllTemps()) {
                String reg = allocation.get(t);
                if (reg != null) {
                    t.setPhysicalReg(reg);
                } else {
                    System.err.println("Warning: no allocation for t" + t.getSerialNumber());
                }
            }
        }
    }
}