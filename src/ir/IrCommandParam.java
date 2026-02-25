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

public class IrCommandParam extends IrCommand {

    private final String type;
    private final String name;
    private final Temp temp;

    public IrCommandParam(String type, String name, Temp temp) {
        this.type = type;
        this.name = name;
        this.temp = temp;
    }

     // Param declaration doesn't use temps

    @Override
    public Set<Temp> getDefTemps() {
        Set<Temp> def = new HashSet<>();
        if (temp != null) def.add(temp); // param is defined
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
