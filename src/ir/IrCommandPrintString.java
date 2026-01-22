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
import mips.MipsGenerator;

public class IrCommandPrintString extends IrCommand
{
	Temp t;
	
	public IrCommandPrintString(Temp t)
	{
		this.t = t;
	}

	@Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
    	return checkTempRead(usedAndUninited, prevOutSet, t);
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().printInt(t);
	}
}
