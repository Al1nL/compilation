/***********/
/* PACKAGE */
/***********/
package ir;

import mips.MipsGenerator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import temp.*;

public class IrCommandVirtualCall extends IrCommand
{
    public Temp dst;
    public Temp object;
    public String method;
    public int offset;
    public ArrayList<Temp> args;

    public IrCommandVirtualCall(
        Temp dst,
        Temp object,
        String method,
        int offset,
        ArrayList<Temp> args)
    {
        this.dst    = dst;
        this.object = object;
        this.method = method;
        this.offset = offset;
        this.args   = args;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (object != null) use.add(object); // object on which method is called
        if (args != null) {
            for (Temp param : args) {
                if (param != null) use.add(param);
            }
        }
        return use;
    }

    @Override
    public Set<Temp> getDefTemps() {
        Set<Temp> def = new HashSet<>();
        if (dst != null) def.add(dst); // return value (if any)
        return def;
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().callMethod(dst, object, offset, args);
    }
}
