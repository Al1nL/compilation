package ir;

import java.util.HashSet;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;

/**
 * Inline null-pointer guard: if ptr == null, print error and exit.
 * Used before pointer arithmetic where the computed address would no longer
 * be zero even if the original object pointer was null.
*/
public class IrCommandNullCheck extends IrCommand {

    public final Temp ptr;

    public IrCommandNullCheck(Temp ptr) {
        this.ptr = ptr;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (ptr != null) use.add(ptr);
        return use;
    }

    @Override
    public Set<Temp> getDefTemps() {
        return new HashSet<>();  // nothing defined
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().checkNullPtr(ptr);
    }
}