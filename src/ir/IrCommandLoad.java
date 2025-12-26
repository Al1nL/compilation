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
import java.util.*;
public class IrCommandLoad extends IrCommand
{
	Temp dst;
    public final Variable var;
	
	public IrCommandLoad(Temp dst,Variable var)
	{
		this.dst      = dst;
		this.var = var;
	}
}
