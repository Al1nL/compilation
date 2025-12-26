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
import temp.*;
import variable.Variable;
import java.util.*;

public class IRcommandConstInt extends IrCommand
{
	Temp t;
	int value;

	public IRcommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
		return checkTempRead(usedAndUninited, prevOutSet, t);
	}
}
