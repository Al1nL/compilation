package ir;

import java.util.HashSet;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;

/**
 * Computes the address of an object field: addr = base + (fieldOffset+1)*4
 *
 * This is distinct from IrCommandAddOffset (which is for ARRAYS and performs
 * null + bounds checking). Object fields are accessed by fixed, statically-known
 * offsets — no runtime bounds check is needed or correct here.
 *
 * The null check on base must be emitted separately (IrCommandNullCheck)
 * before this command.
 */
public class IrCommandAddFieldOffset extends IrCommand {

    public final Temp dst;
    public final Temp base;
    public final int fieldOffset;

    public IrCommandAddFieldOffset(Temp dst, Temp base, int fieldOffset) {
        this.dst = dst;
        this.base = base;
        this.fieldOffset = fieldOffset;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (base != null) use.add(base);
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
        MipsGenerator.getInstance().addFieldOffset(dst, base, fieldOffset);
    }
}