package ast;

import types.*;
import symboltable.*;

public class AstTypedefArray extends AstDec {

    public final String name;
    public final String type;

    public AstTypedefArray(String name, String type) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.type = type;
    }

    @Override
    public void printMe() {
        System.out.println("ARRAY TYPEDEF: " + name);
        String label = "ARRAY\n" + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);
        // if (type != null) {
        //     type.toString();
        //     AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        // }
    }

    @Override
    public Type semantMe() {

        // Step 1: Check that the base type exists
        Type baseType = SymbolTable.getInstance().find(type);
        if (baseType == null) {
            System.out.format(">> ERROR: unknown base type '%s' in array typedef '%s'\n", type, name);
            System.exit(0);
        }
        if (SymbolTable.getInstance().find(name) != null) {
            System.out.format(">> ERROR: typedef '%s' already exists\n", name);
            System.exit(0);
        }

        Type arrayType = new TypeArray(baseType);
        SymbolTable.getInstance().enter(name, arrayType);

        return arrayType;
    }

}
