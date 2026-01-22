/** ******** */
/* PACKAGE */
/** ******** */
package ir;

/**
 * ****************
 */
/* GENERAL IMPORTS */
/**
 * ****************
 */
import java.util.*;
import temp.*;

public class IrCommandReturn extends IrCommand {

    Temp t;

    public IrCommandReturn(Temp t) {
        this.t = t;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (t != null) use.add(t); // value being returned
        return use;
    }
    
    // Return doesn't define temps

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /***************/
	/* MIPS me !!! */
	/***************/
    @Override
	public void mipsMe()
	{
		
	}
}
