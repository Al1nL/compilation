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

public class IrCommandTypedefArray extends IrCommand {

    public final String type;
    public final String name;
    public final Temp temp;

    public IrCommandTypedefArray(String type, String name, Temp temp) {
        this.type = type;
        this.name = name;
        this.temp = temp;
    }

    // Array typedef declaration doesn't define temps
    
    @Override
    public Set<Temp> getDefTemps() {
    Set<Temp> def = new HashSet<>();
        if (temp != null) def.add(temp);
        return def;
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    @Override
    public void mipsMe() {
    }
}
