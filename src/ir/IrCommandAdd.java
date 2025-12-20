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

public class IrCommandAdd extends IrCommand
{
    public Temp dst;   // destination Temp
    public Temp t1;    // first operand
    public Temp t2;    // second operand

    public IrCommandAdd(Temp dst, Temp t1, Temp t2)
    {
        this.dst = dst;
        this.t1  = t1;
        this.t2  = t2;
    }
}
