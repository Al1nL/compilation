package ir;

import java.util.HashSet;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;

/**
 * String content equality: dst = (strcmp(t1, t2) == 0) ? 1 : 0
 * Spec: "tested for (contents) equality with binary operator ="
 */
public class IrCommandBinopEqStrings extends IrCommand
{
    public Temp t1;
    public Temp t2;
    public Temp dst;

    public IrCommandBinopEqStrings(Temp dst, Temp t1, Temp t2)
    {
        this.dst = dst;
        this.t1  = t1;
        this.t2  = t2;
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

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().eqStrings(dst, t1, t2);
    }
}