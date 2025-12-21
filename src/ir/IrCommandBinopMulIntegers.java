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

public class IrCommandBinopMulIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;
	
	public IrCommandBinopMulIntegers(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
    public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet){ 
		return this.generalComputeOutSet(usedAndUninited,prevOutSet,dst);
	}
}
