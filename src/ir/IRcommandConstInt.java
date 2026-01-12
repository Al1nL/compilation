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
// import variable.Variable;
import java.util.*;
import mips.MipsGenerator;


public class IRcommandConstInt extends IrCommand
{
	Temp t;
	int value;
	// public final Variable var;
	public IRcommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
		// this.var = var;
	}

	// @Override
    // public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
	// 	// 1. Create a copy of the IN set (to avoid mutating the input map)
	// 	Map<Variable, Boolean> outSet = new HashMap<>(prevOutSet);

	// 	// 2. This command represents: t = [constant]
	// 	// Since we are assigning a value to 't', it is now initialized.
	// 	// We update its status to 'true' in the OUT set.
	// 	outSet.put(this.var, true);
		
	// 	// 3. Constant assignment does not "use" any variables, 
	// 	// so we don't need to check if anything was used before initialization.
		
	// 	return outSet;
	// }

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().li(t,value);
	}
}
