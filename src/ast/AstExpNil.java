package ast;
import ir.*;
import temp.*;
import types.*;

public class AstExpNil extends AstExp {
    // public Variable var;

    public AstExpNil() {
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    @Override
    public void printMe() {
        System.out.println("NIL");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("NIL"));
    }

    public Type semantMe() {
        // var = Variable.get("NIL" + Integer.toString(serialNumber), SymbolTable.getInstance().currScopeLevel);
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
            AddIrCommand(new IrCommandConstInt(dst, 0));

        /*******************/
        /* [3] return dst */
        /*******************/  
        return dst;
    }
}
