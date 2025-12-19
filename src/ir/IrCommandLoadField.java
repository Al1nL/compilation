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

public class IrCommandLoadField extends IrCommand
{
    public Temp dst;
    public Temp base;
    public String fieldname;

    public IrCommandLoadField(Temp dst, Temp base, String name)
    {
        this.dst = dst;
        this.base = base;
        this.fieldname = name;
    }
}

