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

public class IrCommandVirtualCall extends IrCommand
{
    public Temp dst;
    public Temp object;
    public String method;
    public ArrayList<Temp> args;

    public IrCommandVirtualCall(
        Temp dst,
        Temp object,
        String method,
        ArrayList<Temp> args)
    {
        this.dst    = dst;
        this.object = object;
        this.method = method;
        this.args   = args;
    }
}
