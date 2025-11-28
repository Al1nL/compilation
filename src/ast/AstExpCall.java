package ast;
import java.util.ArrayList;

import symboltable.*;
import types.*;

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
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)",name));
        if (args != null) for (AstExp e : args) e.printMe();
        System.out.println(")");
        if (args != null) for (AstExp e : args) AstGraphviz.getInstance().logEdge(serialNumber,e.serialNumber);

    }

    public Type SemantMe() {
        // Look up the function in the symbol table
        TypeFunction funcType = (TypeFunction) SymbolTable.getInstance().find(name);
        if (funcType == null) {
            System.err.println("Function " + name + " is not defined");
            report();
        }

        //  Check the arguments against the function's parameters
        if (args.size() != funcType.params.len) {
            System.err.println("Function " + name + " expects " +
                                        funcType.params.len + " arguments, but got " + args.size());
            report();
        }
        TypeList p=funcType.params;
        for (int i = 0; i < args.size(); i++) {
            Type argType = args.get(i).semantMe();
            Type paramType = p.head;
            if (!argType.canAssignTo(paramType)) {
                System.err.println("Argument " + (i + 1) + " of function " + name +
                                            " has incompatible type. Expected " + paramType + ", got " + argType);
            report();
            }
            p=p.tail;
        }

        // Return the function's return type
        return funcType.returnType;
    }
}
