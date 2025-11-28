package ast;

import types.*;
import symboltable.*;

public class AstDecVar extends AstDec {

    public String type;
    public String name;
    public AstExp exp;

    public AstDecVar(String type, String name, AstExp exp) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.exp = exp;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");

        String label = "VAR DEC\n" + type + " " + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (exp != null) {
            exp.printMe();
        }

        if (exp != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
        }

    }

    public Type semantMe() {
        Type varType = null;

        // Check for primitive types
        if (type.equals("int")) {
            varType = TypeInt.getInstance();

        } else if (type.equals("string")) {
            varType = TypeString.getInstance();

        } else if (type.equals("void")) {
            // Variables cannot be void
            System.err.println("ERROR: Variable '" + name + "' cannot have type void");
        } else {
            // Must be a class or array type
            varType = SymbolTable.getInstance().find(type);
            if (varType == null) {
                System.err.println("ERROR: Type '" + type + "' is not defined");
                report();
            }
        }

        if (exp != null) {
            Type expType = exp.semantMe();

            if (!expType.canAssignTo(varType)) {
                System.err.println("ERROR: Cannot assign " + expType.name + " to " + varType.name);
                report();
            }
        }

        // Enter variable to symbol table   
        SymbolTable.getInstance().enter(name, varType);

        return varType;
    }
}
