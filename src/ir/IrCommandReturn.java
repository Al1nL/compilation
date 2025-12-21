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

public class IrCommandReturn extends IrCommand
{
	Temp t;
	
	public IrCommandReturn(Temp t)
	{
		this.t = t;
	}

	@Override
    public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet){ 
		return this.generalComputeOutSet(usedAndUninited,prevOutSet,dst);
	}
}
