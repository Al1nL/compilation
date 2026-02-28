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
import java.util.*;
import mips.MipsGenerator;
import temp.*;


public class IrCommandConstInt extends IrCommand
{
	Temp t;
	int value;

	public IrCommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
	public Set<Temp> getDefTemps() {
		Set<Temp> def = new HashSet<>();
		if (t != null) def.add(t);
		return def;
	}

	@Override
	public Set<Temp> computeInSet(Set<Temp> out) {
		return generalComputeInSet(out);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().li(t,value);
	}
}
