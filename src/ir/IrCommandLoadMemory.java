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
import mips.MipsGenerator;

public class IrCommandLoadMemory extends IrCommand
{
	Temp dst;
	public Temp src;
    public final Variable var;
	
	public IrCommandLoadMemory(Temp dst,Temp src,Variable var)
	{
		this.src = src;
		this.dst = dst;
		this.var = var;
	}

	@Override
	public Set<Temp> getDefTemps() {
		Set<Temp> def = new HashSet<>();
		if (dst != null) def.add(dst);
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
	}
}
