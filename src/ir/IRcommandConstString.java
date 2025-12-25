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
import variable.Variable;

public class IRcommandConstString extends IrCommand
{
	Temp t;
	String value;
	
	public IRcommandConstString(Temp t, String value)
	{
		this.t = t;
		this.value = value;
	}
}
