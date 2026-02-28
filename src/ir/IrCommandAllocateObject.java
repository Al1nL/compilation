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
public class IrCommandAllocateObject extends IrCommand
{
    public Temp dst;
    public String type;

    public IrCommandAllocateObject(Temp dst, String type)
    {
        this.dst  = dst;
        this.type = type;
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
