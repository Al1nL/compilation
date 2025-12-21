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
    public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet){ 
		return this.generalComputeOutSet(usedAndUninited,prevOutSet,dst);
	}
}
