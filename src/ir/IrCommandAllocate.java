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

import variable.Variable;
import mips.MipsGenerator;

public class IrCommandAllocate extends IrCommand
{
	Variable var;
	
	public IrCommandAllocate(Variable var)
	{
		this.var = var;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().allocate(var.name);
	}	
}
