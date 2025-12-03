package ast;

import symboltable.*;
import types.*;

public class AstVarField extends AstVar {

    public AstVar var;
    public String fieldName;

    /**
     * ***************
     */
    /* CONSTRUCTOR(S) */
    /**
     * ***************
     */
    public AstVarField(AstVar var, String fieldName) {
        /**
         * ***************************
         */
        /* SET A UNIQUE SERIAL NUMBER */
        /**
         * ***************************
         */
        serialNumber = AstNodeSerialNumber.getFresh();

        /**
         * ************************************
         */
        /* PRINT CORRESPONDING DERIVATION RULE */
        /**
         * ************************************
         */
        System.out.format("====================== var -> var DOT ID( %s )\n", fieldName);

        /**
         * ****************************
         */
        /* COPY INPUT DATA MEMBERS ... */
        /**
         * ****************************
         */
        this.var = var;
        this.fieldName = fieldName;
    }

    /**
     * **********************************************
     */
    /* The printing message for a field var AST node */
    /**
     * **********************************************
     */
    public void printMe() {
        /**
         * ******************************
         */
        /* AST NODE TYPE = AST FIELD VAR */
        /**
         * ******************************
         */
        System.out.print("AST NODE FIELD VAR\n");

        /**
         * *******************************************
         */
        /* RECURSIVELY PRINT VAR, then FIELD NAME ... */
        /**
         * *******************************************
         */
        if (var != null) {
            var.printMe();
        }
        System.out.format("FIELD NAME( %s )\n", this.fieldName);

        /**
         * ************************************
         */
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /**
         * ************************************
         */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("FIELD\nVAR\n...->%s", this.fieldName));

        /**
         * *************************************
         */
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /**
         * *************************************
         */
        if (var != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        }
    }

    public Type semantMe() {
        Type t = null;
        TypeClass tc = null;

        /**
         * ***************************
         */
        /* [1] Recursively semant var */
        /**
         * ***************************
         */
        if (var != null) {
            t = var.semantMe();
        }

        /**
         * ******************************
         */
        /* [2] Make sure type is a class */
        /**
         * ******************************
         */
        if (t.isClass() == false) {
            System.out.format(">> ERROR [%d] access %s field of a non-class variable\n", lineNumber + 1, fieldName);
            System.exit(0);
        } else {
            tc = (TypeClass) t;
        }
        /* Look for fiedlName inside tc */

        TypeClass currentClass = tc;
        Type found = null;
        while (currentClass != null) {
            found = SymbolTable.getInstance()
                    .findInClassScope(currentClass, fieldName);

            if (found != null) {
                return found;
            }

            currentClass = currentClass.father;  // go to parent
        }
        if (found == null) {
            System.err.println("var:" + var.lineNumber);
            System.out.format(">> ERROR [%d] field %s does not exist in class\n", lineNumber, fieldName);
            System.exit(0);
        }

        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }
}
