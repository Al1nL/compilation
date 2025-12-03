package ast;

import symboltable.*;
import types.*;

public class AstDecClass extends AstDec {

    /**
     * *************
     */
    /* DATA MEMBERS */
    /**
     * *************
     */
    public final String name;         // class name
    public final String parentName;   // null if no EXTENDS
    public final AstDecList fields;   // linked list of cFields

    /**
     * ***************
     */
    /* CONSTRUCTOR(S) */
    /**
     * ***************
     */
    public AstDecClass(String name, String parentName, AstDecList fields) {
        serialNumber = AstNodeSerialNumber.getFresh();

        if (parentName != null) {
            System.out.print("===== classDec -> CLASS ID EXTENDS ID LBRACE cField { cField } RBRACE\n"); 
        }else {
            System.out.print("===== classDec -> CLASS ID LBRACE cField { cField } RBRACE\n");
        }

        this.name = name;
        this.parentName = parentName;
        this.fields = fields;
    }

    @Override
    public void printMe() {
        /**
         * **********************************
         */
        /* RECURSIVELY PRINT HEAD + TAIL ... */
        /**
         * **********************************
         */
        System.out.format("CLASS DEC = %s\n", this.name);
        if (fields != null) {
            fields.printMe();
        }

        /**
         * ************************************
         */
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /**
         * ************************************
         */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("CLASS\n%s", this.name));

        /**
         * *************************************
         */
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /**
         * *************************************
         */
        AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }

    public Type semantMe() {
        if (SymbolTable.getInstance().find(this.name) != null) {
            report(); // class already declared
        }

        TypeClass parentTypeClass = null;
        //todo: check this after

        if (parentName != null) {
            Type parentType = SymbolTable.getInstance().find(parentName);
            if (parentType == null || !(parentType instanceof TypeClass)) {
                report(); // superclass does not exist or is not a class
            }
            parentTypeClass = (TypeClass) parentType;
        }

        TypeClass t = new TypeClass(parentTypeClass, this.name, null);
        SymbolTable.getInstance().enter(this.name, t);

        SymbolTable.getInstance().beginScope();

        /*  Semant Data Members */
        TypeList fieldTypes = fields.semantMe();

        // Update the TypeClass with the actual field types
        t.dataMembers = fieldTypes;

        /*  DON'T End Scope - class scope stays open!     */
        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }
}
