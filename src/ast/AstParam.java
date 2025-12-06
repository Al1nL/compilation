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

        String label = "PARAM\n" + this.name;
        AstGraphviz.getInstance().logNode(serialNumber, label);
    }

    public Type semantMe() {
        
        Type paramType = null;

        if (type.equals("int")) {
            paramType = TypeInt.getInstance();

        } else if (type.equals("string")) {
            paramType = TypeString.getInstance();

        } else if (type.equals("void")) {
            System.out.print("ERROR: Parameter '" + this.name + "' cannot have type void");
            report();
        } else {
            // Must be a class or array type
            paramType = SymbolTable.getInstance().find(type);
            if (paramType == null) {
                System.out.print("ERROR: Type '" + this.type + "' is not defined");
                report();
            }
        }

        // Store the resolved type
        paramType.name = name; // This allows us to identify the param later

        return paramType;
    }
    public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
}
