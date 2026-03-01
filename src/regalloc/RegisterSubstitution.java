package regalloc;

import analysis.CFGNode;
import java.util.*;
import temp.Temp;

public class RegisterSubstitution {

    public static void apply(List<CFGNode> nodes, Map<Temp, String> allocation) {
        for (CFGNode node : nodes) {
            for (Temp t : node.cmd.getAllTemps()) {
                String reg = allocation.get(t);
                if (reg != null) {
                    t.setPhysicalReg(reg);
                } else {
                    // temp is never live (dead), assign unused register (e.g., $s3) for safety
                    t.setPhysicalReg("$s3");}
            }
        }
    }
}