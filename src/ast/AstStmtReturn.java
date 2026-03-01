package ast;

import ir.*;
import java.util.*;
import returncounter.ReturnCounter;
import temp.*;
import types.*;
import variable.*;

public class AstStmtReturn extends AstStmt {

    public static String currentFunctionName = null;
    public final AstExp exp;  // The expression being returned, can be null for `return;`

    public AstStmtReturn(AstExp exp) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;
    }

    @Override
    public void printMe() {
        System.out.print("RETURN");
        String label = "RETURN";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (exp != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);

            // Recursively print the expression
            exp.printMe();
        }
    }

    public Type semantMe(Type expectedReturnType) {
        if (exp == null) {
            // return; with no value
            if (!(expectedReturnType instanceof TypeVoid)) {
                System.err.println("ERROR: Function must return a value of type " + expectedReturnType.name);
                report();
            }
        } else {
            // return exp;
            if (expectedReturnType instanceof TypeVoid) {
                System.err.println("ERROR: return <expression> is not allowed, even if the expression has type void");
                report();
            }
            Type returnType = exp.semantMe();

            if (!returnType.canAssignTo(expectedReturnType)) {
                System.err.println("ERROR: Return type " + returnType.name + " does not match expected type " + expectedReturnType.name);
                report();
            }
        }
        ReturnCounter c = ReturnCounter.getInstance();
        c.setCount(1);
        return null;
    }

    public Type semantMe() {
        return null;
    }

    public Temp irMe() {
        Temp t = null;
        if (this.exp != null) {
            t = this.exp.irMe();
        }
        // Always emit return — handles both void and non-void
        Ir.getInstance().AddIrCommand(new IrCommandReturn(t, currentFunctionName));
        return t;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels) {

        if (exp != null) {
            curIdx = exp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        return curIdx;

    }

    public void debugOffset() {
        if (exp != null) {
            exp.debugOffset();
        }

    }

}
