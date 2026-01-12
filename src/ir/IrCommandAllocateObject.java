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
public class IrCommandAllocateObject extends IrCommand
{
    public Temp dst;
    public String type;

    public IrCommandAllocateObject(Temp dst, String type)
    {
        this.dst  = dst;
        this.type = type;
    }

    /***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		
	}
}
