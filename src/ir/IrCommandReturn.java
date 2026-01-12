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
import variable.Variable;

public class IrCommandReturn extends IrCommand {

    Temp t;

    public IrCommandReturn(Temp t) {
        this.t = t;
    }

    @Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet) {
        if (t != null) {
            return checkTempRead(usedAndUninited, prevOutSet, t);
        }
        return checkTempRead(usedAndUninited, prevOutSet, t);
    }

    /***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		
	}
}
