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
public class IrCommandAllocate extends IrCommand
{
	Variable var;
	
	public IrCommandAllocate(Variable var)
	{
		this.var = var;
	}	
}
