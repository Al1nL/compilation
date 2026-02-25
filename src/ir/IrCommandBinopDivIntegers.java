package ir;

import java.util.*;
import mips.MipsGenerator;
import temp.*;

public class IrCommandBinopDivIntegers extends IrCommand
{
    public Temp t1;
    public Temp t2;
    public Temp dst;

    public IrCommandBinopDivIntegers(Temp dst, Temp t1, Temp t2)
    {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (t1 != null) use.add(t1);
        if (t2 != null) use.add(t2);
        return use;
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
        MipsGenerator.getInstance().div(dst,t1,t2);
    }
}