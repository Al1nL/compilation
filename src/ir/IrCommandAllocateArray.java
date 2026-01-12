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

public class IrCommandAllocateArray extends IrCommand
{
    public Temp dst;
    public String type;
    public Temp size;

    public IrCommandAllocateArray(Temp dst, String type, Temp size)
    {
        this.dst  = dst;
        this.type = type;
        this.size = size;
    }

    /***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		
	}
}