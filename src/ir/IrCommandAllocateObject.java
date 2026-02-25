package ir;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;
public class IrCommandAllocateObject extends IrCommand
{
    public Temp dst;
    public String type;
    // if type is a class
    public Map<String,Integer> methodOffsets;
    public int fieldCount;

    public IrCommandAllocateObject(Temp dst, String type, Map<String,Integer> methodOffsets, int fieldCount)
    {
        this.dst  = dst;
        this.type = type;
        this.methodOffsets = methodOffsets;
        this.fieldCount = fieldCount;
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
        MipsGenerator.getInstance().allocateObject(dst, type,methodOffsets, fieldCount);
    }
}
