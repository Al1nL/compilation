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

public class IrCommandTypedefArray extends IrCommand {

    public final String type;
    public final String name;
    public final Temp temp;

    public IrCommandTypedefArray(String type, String name, Temp temp) {
        this.type = type;
        this.name = name;
        this.temp = temp;
    }

    /***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		
	}
}
