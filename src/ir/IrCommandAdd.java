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

public class IrCommandAdd extends IrCommand
{
    public Temp dst;   // destination Temp
    public Temp t1;    // first operand
    public Temp t2;    // second operand

    public IrCommandAdd(Temp dst, Temp t1, Temp t2)
    {
        this.dst = dst;
        this.t1  = t1;
        this.t2  = t2;
    }

    @Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
		return this.generalComputeOutSet(usedAndUninited,prevOutSet,dst);
	}

}
