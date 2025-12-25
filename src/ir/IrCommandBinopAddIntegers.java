/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;
import variable.Variable;
import java.util.*;
import analysis.Dbg;

public class IrCommandBinopAddIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;
	public final Variable var;

	public IrCommandBinopAddIntegers(Temp dst, Temp t1, Temp t2, Variable var)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
		this.var = var;
	}

	@Override
	public Map<Variable, Boolean> computeOutSet(
			Set<Variable> usedAndUninited,
			Map<Variable, Boolean> in) {
		
		
		Dbg.p("BinopAddIntegers, deps=" + dst.dependencySet);
		Dbg.p("  IN=" + in);
		Map<Variable, Boolean> out = new HashMap<>(in);

		boolean rhsInitialized = true;

		// Check RHS usage
		for (Variable v : dst.dependencySet) {
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
}
