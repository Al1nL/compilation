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

    private final String type;
    private final String name;
    private final Temp temp;

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
