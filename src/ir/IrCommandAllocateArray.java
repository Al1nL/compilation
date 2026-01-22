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

public class IrCommandAllocateArray extends IrCommand
{
    public Temp dst;
    public String type;
    public Temp size;

    public IrCommandAllocateArray(Temp dst, String type, Temp size)
    {
        this.dst  = dst;
        this.type = type;
        this.size = size;
    }

        @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (size != null) use.add(size); // size temp
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