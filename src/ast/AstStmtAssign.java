package ast;

import ir.*;
import temp.*;
import types.*;

public class AstStmtAssign extends AstStmt {

    /* var := exp */
    public AstVar var;
    public AstExp exp;

    /*  CONSTRUCTOR(S) */
    public AstStmtAssign(AstVar var, AstExp exp) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();
        /* COPY INPUT DATA MEMBERS ... */
        this.var = var;
        this.exp = exp;
    }

    /* The printing message for an assign statement AST node */
    public void printMe() {

        /* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
        System.out.print("AST NODE ASSIGN STMT\n");

        /* RECURSIVELY PRINT VAR + EXP ... */
        if (var != null) {
            var.printMe();
        }
        if (exp != null) {
            exp.printMe();
        }
        /* PRINT Node to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "ASSIGN\nleft := right\n");

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    public Type semantMe() {
        Type varType = var.semantMe();
        Type expType = exp.semantMe();

        if (!expType.canAssignTo(varType)) {
            System.err.println("ERROR: Cannot assign " + expType.name + " to " + varType + " " + lineNumber);
            report();
        }
        return null;
    }

    public Type semantMe(Type expectedReturnType) {
        Type varType = var.semantMe(expectedReturnType);
        Type expType = exp.semantMe(expectedReturnType);

        if (!expType.canAssignTo(varType)) {
            System.err.println("ERROR: Cannot assign " + expType.name + " to " + varType + " " + lineNumber);
            report();
        }
        return null;
    }

    public Temp irMe()
    {
        Temp src = exp.irMe();
        Ir.
                getInstance().
                AddIrCommand(new IrCommandStore(((AstVarSimple) var).var,src));

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
		if (var != null) {
            curIdx = var.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }

        if (exp != null) {
            curIdx = exp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        return curIdx;
	}
}