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
import variable.Variable;

public class IrCommandLoadMemory extends IrCommand
{
	Temp dst;
	public Temp src;
    public final Variable var;
	
	public IrCommandLoadMemory(Temp dst, Temp src, Variable var)
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
	public Set<Temp> getUseTemps() {
		Set<Temp> use = new HashSet<>();
		if (src != null) use.add(src);
		return use;
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
		// src is the fully-computed element address (base + (i+1)*4)
		// so we just dereference it at offset 0
		MipsGenerator.getInstance().loadFromPointer(dst, src, 0);
	}
}