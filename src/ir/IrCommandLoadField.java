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
import java.util.HashSet;
import java.util.Set;
import temp.*;

public class IrCommandLoadField extends IrCommand
{
    public Temp dst;
    public Temp base;
    public String fieldname;

    public IrCommandLoadField(Temp dst, Temp base, String name)
    {
        this.dst = dst;
        this.base = base;
        this.fieldname = name;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (null != base) use.add(base); // object whose field is being loaded
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

