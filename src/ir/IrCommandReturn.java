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
}
