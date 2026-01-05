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

public class IrCommandJumpIfEqToZero extends IrCommandJumpLabel
{
	Temp t;
	
	public IrCommandJumpIfEqToZero(Temp t, String labelName)
	{
		super(labelName);
		this.t = t;
	}


	@Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
		return checkTempRead(usedAndUninited, prevOutSet, t);
	}

}
