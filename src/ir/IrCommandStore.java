package ir;

import analysis.Dbg;
import java.util.*;
import temp.Temp;
import variable.Variable;
import mips.MipsGenerator;


public class IrCommandStore extends IrCommand {

    public final Variable var;

    Temp src;

    public IrCommandStore(Variable var, Temp src) {
        this.var = var;
        this.src = src;
    }

    @Override
    public Map<Variable, Boolean> computeOutSet(
            Set<Variable> usedAndUninited,
            Map<Variable, Boolean> in) {
        Dbg.p("STORE " + var.name + "  deps=" + src.dependencySet);
        Dbg.p("  IN=" + in);
        Map<Variable, Boolean> out = new HashMap<>(in);

        boolean rhsInitialized = true;

        // Check RHS usage
        for (Variable v : src.dependencySet) {
            if (!in.getOrDefault(v, false)) {
                usedAndUninited.add(v);
                rhsInitialized = false;
            }
        }

        // Update only the assigned variable
        out.put(var, rhsInitialized);
        Dbg.p("  rhsInitialized=" + rhsInitialized);

        return out;
    }

    /***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().store(var.name,src);
	}

}
