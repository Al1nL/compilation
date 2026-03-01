package ir;

import java.util.*;
import mips.MipsGenerator;
import temp.*;
import variable.Variable;

public class IrCommandStoreMemory extends IrCommand
{
    Temp dst;   // the computed address (base + (i+1)*4)
    Temp src;   // value to store
    public final Variable var;

    public IrCommandStoreMemory(Temp dst, Temp src, Variable var)
    {
        this.dst = dst;
        this.src = src;
        this.var = var;
    }

    @Override
    public Set<Temp> getDefTemps() {
        // storing to memory doesn't define any temp
        return new HashSet<>();
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (dst != null) use.add(dst);
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
        // dst is the fully-computed element address (base + (i+1)*4)
        // so we just store src into *dst at offset 0
        MipsGenerator.getInstance().storeToPointer(src, dst, 0);
    }
}