package ast;

import types.*;
import symboltable.*;

public class AstParam extends AstNode {

    public final String type;
    public final String name;

    public AstParam(String type, String name) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
    }

    public void printMe() {
        System.out.print("AST NODE PARAM\n");

        String label = "PARAM\n" + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);
    }

    public Type semantMe() {
        
        Type paramType = null;

        if (type.equals("int")) {
            paramType = TypeInt.getInstance();

        } else if (type.equals("string")) {
            paramType = TypeString.getInstance();

        } else if (type.equals("void")) {
            throw new RuntimeException("ERROR: Parameter '" + name + "' cannot have type void");
        } else {
            // Must be a class or array type
            paramType = SymbolTable.getInstance().find(type);
            if (paramType == null) {
                throw new RuntimeException("ERROR: Type '" + type + "' is not defined");
            }
        }

        // Store the resolved type
        paramType.name = name; // This allows us to identify the param later

        return paramType;
    }
}
