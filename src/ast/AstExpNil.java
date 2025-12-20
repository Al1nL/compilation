package ast;
import temp.*;
import ir.*;
import types.*;

public class AstExpNil extends AstExp {

    public AstExpNil() {
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    @Override
    public void printMe() {
        System.out.println("NIL");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("NIL"));
    }

    public Type semantMe() {
        return TypeNil.getInstance();
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        /******************************/
        /* [1] Allocate fresh temp   */
        /******************************/
        Temp dst = TempFactory.getInstance().getFreshTemp();
    
        /******************************/
        /* [2] dst = 0 (nil)         */
        /******************************/
        Ir.
            getInstance().
            AddIrCommand(new IRcommandConstInt(dst, 0));

        /*******************/
        /* [3] return dst */
        /*******************/
        return dst;
    }
}
