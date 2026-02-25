/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import mips.MipsGenerator;
import temp.*;

public class IrCommandPrintString extends IrCommand
{
	Temp t;
	
	public IrCommandPrintString(Temp t)
	{
		this.t = t;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().printInt(t);
	}
}
