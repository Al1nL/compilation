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

public class IrCommandJumpIfEqToZero extends IrCommand
{
	Temp t;
	String labelName;
	
	public IrCommandJumpIfEqToZero(Temp t, String labelName)
	{
		this.t          = t;
		this.labelName = labelName;
	}


	@Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
		return checkTempRead(usedAndUninited, prevOutSet, t);
	}
	public String getLabelName() {
    return labelName;
}
}
