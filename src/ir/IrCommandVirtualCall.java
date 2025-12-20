/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.ArrayList;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

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
