package ir;

import analysis.Dbg;
import java.util.*;
import temp.Temp;
import variable.Variable;
import types.*;
import mips.MipsGenerator;
public abstract class IrCommand {

    /* Label Factory */
    protected static int labelCounter = 0;

    public static String getFreshLabel(String msg) {
        return String.format("Label_%d_%s", labelCounter++, msg);
    }

    /**
     * Transfer function.
     * Default behavior: OUT = IN.
     */
    public Map<Variable, Boolean> computeOutSet(
            Set<Variable> usedAndUninited,
            Map<Variable, Boolean> inSet) {
        return new HashMap<>(inSet);
    }

    /**
     * Helper for IR commands that ASSIGN to a TEMP.
     * Checks that all variables used in computing the TEMP are initialized.
     * Does not modify the initialized-variable set.
     */
    protected Map<Variable, Boolean> generalComputeOutSet(
            Set<Variable> usedAndUninited,
            Map<Variable, Boolean> inSet,
            Temp t) {

        for (Variable var : t.dependencySet) {
            if (!inSet.getOrDefault(var, false)) {
                usedAndUninited.add(var);
            }
        }

        return new HashMap<>(inSet);
    }

    /**
     * Helper for IR commands that READ a TEMP without assigning.
     */
    protected Map<Variable, Boolean> checkTempRead(
            Set<Variable> usedAndUninited,
            Map<Variable, Boolean> inSet,
            Temp t) {

        for (Variable var : t.dependencySet) {
            if (!inSet.getOrDefault(var, false)) {
                usedAndUninited.add(var);
                 Dbg.p("!!! USED BEFORE SET: " + var.name + "@" + var.scope
        + " in " + this.getClass().getSimpleName());
            }
        }

        return new HashMap<>(inSet);
    }

    /***************/
	/* MIPS me !!! */
	/***************/
	public abstract void mipsMe();
}
