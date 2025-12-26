package ast;

import java.util.ArrayList;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;

public class AstExpCall extends AstExp {

    public final String name;
    public final ArrayList<AstExp> args;

    public AstExpCall(String name, ArrayList<AstExp> args) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("CALL " + name + "(");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)", name));
        if (args != null) {
            for (AstExp e : args) {
                e.printMe();
            }
        }
        System.out.println(")");
        if (args != null) {
            for (AstExp e : args) {
                AstGraphviz.getInstance().logEdge(serialNumber, e.serialNumber);
            }
        }

    }

    public Type semantMe() {
        // Look up the function in the symbol table
        TypeFunction funcType = (TypeFunction) SymbolTable.getInstance().find(name);
        Type result = validateCall(name, funcType, args, false);
        if (result == null) {
            report();
        }
        return result;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        Temp t = null;

        // if (args != null) { t = args.head.irMe(); }
        if(args != null){
            for (AstExp e : args) {
                t = e.irMe();
            }
        }
        Ir.getInstance().AddIrCommand(new IrCommandPrintInt(t));

        return null;
    }
}
