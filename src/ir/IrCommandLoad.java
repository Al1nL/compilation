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

public class IrCommandLoad extends IrCommand
{
	Temp dst;
    public final Variable var;
	
	public IrCommandLoad(Temp dst,Variable var)
	{
		this.dst = dst;
		this.var = var;
	}
	// Load reads from a variable (not a temp), so no temps are used
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
		if (var.isGlobal){
			MipsGenerator.getInstance().loadGlobal(dst, var.name);
		}
		else{
			int offset = var.offset < 0
                    ? -4 * var.offset + 4       // arguments: above $fp (positive offsets) - unchanged
                    : -(4 * var.offset + 44);   // locals: skip 40 bytes of saved $t registers
			MipsGenerator.getInstance().loadLocal(dst, offset);
		}
		
	}
}
