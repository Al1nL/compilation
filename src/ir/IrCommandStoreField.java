package ir;

import java.util.*;
import mips.MipsGenerator;
import temp.*;
import variable.Variable;

public class IrCommandStoreField extends IrCommand
{
    public Temp base;
    public Temp src;
    public String fieldname;
    public int offset;

    public IrCommandStoreField(Temp src, Temp base, String name, int offset)
    {
        this.base = base;
        this.src = src;
        this.fieldname = name;
        this.offset = offset;
    }

    @Override
    public Set<Temp> getDefTemps() {
        // storing to memory doesn't define any temp
        return new HashSet<>();
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (base != null) use.add(base); // object whose field is being loaded
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
        
        MipsGenerator.getInstance().storeToPointer(src, base, (1+offset)*4);
    }
}