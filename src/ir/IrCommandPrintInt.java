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

public class IrCommandPrintInt extends IrCommand
{
	Temp t;
	
	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}

	@Override
    public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet){ 
		return this.generalComputeOutSet(usedAndUninited,prevOutSet,dst);
	}
}
