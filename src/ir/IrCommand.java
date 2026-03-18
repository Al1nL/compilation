package ir;

import java.util.*;
import temp.Temp;
public abstract class IrCommand {

    /* Label Factory */
    protected static int labelCounter = 0;
    public IrCommandAllocateObject allocatedObject;
    public static String getFreshLabel(String msg) {
        return String.format("Label_%d_%s", labelCounter++, msg);
    }

    /**
     * Liveness analysis transfer function.
     * Default behavior: IN = OUT (no uses or defs).
     */
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /**
     * General liveness transfer function for most IR commands.
     * IN = USE ∪ (OUT - DEF)
     */
    public Set<Temp> generalComputeInSet(Set<Temp> out) {
        Set<Temp> use = getUseTemps();
        Set<Temp> def = getDefTemps();
        
        // IN = USE ∪ (OUT - DEF)
        Set<Temp> in = new HashSet<>(use);
        Set<Temp> outMinusDef = new HashSet<>(out);
        outMinusDef.removeAll(def);
        in.addAll(outMinusDef);
        
        return in;
    }

    /**
     * Get the set of temps used (read) by this command.
     */
    public Set<Temp> getUseTemps() {
        return new HashSet<>();
    }

    /**
     * Get the set of temps defined (written) by this command.
     */
    public Set<Temp> getDefTemps() {
        return new HashSet<>();
    }
    
    /***************/
	/* MIPS me !!! */
	/***************/
	public abstract void mipsMe();

    public Set<Temp> getAllTemps() {
        Set<Temp> all = new HashSet<>();
        all.addAll(getUseTemps());
        all.addAll(getDefTemps());
        return all;
    }
}