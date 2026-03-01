package ast;

import ir.*;
import java.util.*;
import temp.*;
import types.*;
import variable.*;

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

    public Temp irMe() {
        Temp src = exp.irMe();

        if (var instanceof AstVarSubscript) {
            // Get the computed element address without emitting a load
            Temp addr = ((AstVarSubscript) var).irMeAsAddress();
            // Store src into *addr
            Ir.getInstance().AddIrCommand(new IrCommandStoreMemory(addr, src, var.var));
        }
        else if (var instanceof AstVarField) {
            // Get the computed field address without emitting a load
            Temp addr = ((AstVarField) var).irMeAsAddress();
            // Store src into *addr
            Ir.getInstance().AddIrCommand(new IrCommandStoreMemory(addr, src, var.var));
        }
         else {
            // Normal variable assignment (stack slot)
            Ir.getInstance().AddIrCommand(new IrCommandStore(var.var, src));
        }

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets) {
        if (var != null) {
            curIdx = var.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }

        if (exp != null) {
            curIdx = exp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        return curIdx;
    }

    public void debugOffset() {
        if (var != null) {
            var.debugOffset();
        }
        if (exp != null) {
            exp.debugOffset();
        }
    }
}
