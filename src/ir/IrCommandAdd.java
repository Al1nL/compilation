/* PACKAGE */
package ir;

/* GENERAL IMPORTS */
import java.util.*;
import mips.MipsGenerator;
import temp.*;

public class IrCommandAdd extends IrCommand {

    public Temp dst;   // destination Temp
    public Temp t1;    // first operand
    public Temp t2;    // second operand

    public IrCommandAdd(Temp dst, Temp t1, Temp t2) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (t1 != null) {
            use.add(t1);
        }
        if (t2 != null) {
            use.add(t2);
        }
        return use;
    }

    @Override
    public Set<Temp> getDefTemps() {
        Set<Temp> def = new HashSet<>();
        if (dst != null) {
            def.add(dst);
        }
        return def;
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /***************/
	/* MIPS me !!! */
	/***************/
    @Override
	public void mipsMe()
	{
		if (t2 != null)
		{
			// pointer arithmetic: dst = t1 + t2 (base + index, scaled by word size)
			MipsGenerator.getInstance().addOffset(dst, t1, t2);
		}
		else
		{
			// dereference: dst = Memory[t1]
			MipsGenerator.getInstance().loadFromPointer(dst, t1, 0);
		}
	}
}
