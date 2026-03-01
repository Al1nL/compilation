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
import mips.MipsGenerator;
import temp.*;

public class IrCommandReturn extends IrCommand {

    Temp t;
    String functionName;

    public IrCommandReturn(Temp t, String functionName) {
        this.t = t;
        this.functionName = functionName;
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
        MipsGenerator.getInstance().returnToCaller(t, functionName);
	}
}
