package ast;

import symboltable.*;
import types.*;

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
    }

    @Override
    public Type semantMe() {
        // Check that the base type exists
        Type baseType = SymbolTable.getInstance().find(type);
        if (!SymbolTable.getInstance().isGlobalScope()) {
            System.out.format(">> ERROR: Array type defined not in global scope\n");
            report();
        }
        if (baseType == null) {
            System.out.format(">> ERROR: unknown base type '%s' in array typedef '%s'\n", type, name);
            report();
        }
        if (baseType.isSameType(TypeVoid.getInstance())) {
            System.out.format(">> ERROR: assigned void in array typedef '%s'\n", name);
            report();
        }
        if (SymbolTable.getInstance().find(name) != null) {
            System.out.format(">> ERROR: typedef '%s' already exists\n", name);
            report();
        }

        Type arrayType = new TypeArray(baseType, name);
        SymbolTable.getInstance().enter(name, arrayType);

        return arrayType;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe() {
        /*******************************/
        /* [1] Allocate a temp for the array typedef */
        /*******************************/
        Temp arrayTemp = TempFactory.getInstance().getFreshTemp();

        /******************************************/
        /* [2] Add IR command to register the typedef */
        /******************************************/
        Ir.getInstance().AddIrCommand(
            new IrCommandTypedefArray(type, name, arrayTemp)
        );

        /*******************************/
        /* [3] Return the temp */
        /*******************************/
        return arrayTemp;
    }
}