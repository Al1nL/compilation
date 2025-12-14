package ast;

import types.*;

public class AstVarField extends AstVar {

    public AstVar var;
    public String fieldName;

    /* CONSTRUCTOR(S) */
    public AstVarField(AstVar var, String fieldName) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();

        this.var = var;
        this.fieldName = fieldName;
    }

    /* The printing message for a field var AST node */
    public void printMe() {
        /* AST NODE TYPE = AST FIELD VAR */
        System.out.print("AST NODE FIELD VAR\n");

        /* RECURSIVELY PRINT VAR, then FIELD NAME ... */
        if (var != null) {
            var.printMe();
        }
        System.out.format("FIELD NAME( %s )\n", this.fieldName);

        /* PRINT Node to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("FIELD\nVAR\n...->%s", this.fieldName));

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        if (var != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        }
    }

    public Type semantMe() {
        Type t = null;
        TypeClass tc = null;

        /* Recursively semant var */
        if (var != null) {
            t = var.semantMe();
        }

        /* Make sure type is a class */
        if (t.isClass() == false) {
            System.out.format(">> ERROR [%d] access %s field of a non-class variable\n", lineNumber + 1, fieldName);
            report();
        }

        tc = (TypeClass) t;
        /* Look for fiedlName inside tc or fathers */
        Type found = tc.findField(this.fieldName);

        if (found == null) {
            System.out.format(">> ERROR [%d] field %s does not exist in class\n", lineNumber, fieldName);
            report();
        }

        return found;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }
}