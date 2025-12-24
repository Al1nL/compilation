/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;
import temp.*;
import variable.Variable;

public class IrCommandPrintInt extends IrCommand
{
	Temp t;
	
	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}

	@Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
    	return checkTempRead(usedAndUninited, prevOutSet, t);
	}
}
