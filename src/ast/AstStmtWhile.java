package ast;

import java.util.*;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstStmtWhile extends AstStmt {

    public AstExp cond;
    public AstStmtList body;

    /*  CONSTRUCTOR(S) */
    public AstStmtWhile(AstExp cond, AstStmtList body) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void printMe() {
        System.out.print("WHILE");
        String label = "WHILE";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (cond != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        }
        if (body != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        }

        if (cond != null) {
            cond.printMe();
        }
        if (body != null) {
            body.printMe();
        }

    }

    public Type semantMe(Type expectedReturnType) {
        // Check condition is int
        Type condType = cond.semantMe();
        if (!(condType instanceof TypeInt)) {
            System.err.println("ERROR: While condition must be int, got " + condType.name);
            report();
        }

        // Begin scope for while body
        SymbolTable.getInstance().beginScope();
        if (body != null) {
            body.semantMe(expectedReturnType);
        }
        SymbolTable.getInstance().endScope();
        return null;
    }

    public Temp irMe()
    {
        /*******************************/
        /* [1] Allocate 2 fresh labels */
        /*******************************/
        String labelEnd   = IrCommand.getFreshLabel("end");
        String labelStart = IrCommand.getFreshLabel("start");

        /*********************************/
        /* [2] entry label for the while */
        /*********************************/
        Ir.
                getInstance().
                AddIrCommand(new IrCommandLabel(labelStart));

        /********************/
        /* [3] cond.IRme(); */
        /********************/
        Temp condTemp = cond.irMe();

        /******************************************/
        /* [4] Jump conditionally to the loop end */
        /******************************************/
        Ir.
                getInstance().
                AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelEnd));

        /*******************/
        /* [5] body.IRme() */
        /*******************/
        body.irMe();

        /******************************/
        /* [6] Jump to the loop entry */
        /******************************/
        Ir.
                getInstance().
                AddIrCommand(new IrCommandJumpLabel(labelStart));

        /**********************/
        /* [7] Loop end label */
        /**********************/
        Ir.
                getInstance().
                AddIrCommand(new IrCommandLabel(labelEnd));

        /*******************/
        /* [8] return null */
        /*******************/
        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
        
        if (cond != null) {
            curIdx = cond.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        if (body != null) {
            curIdx = body.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        return curIdx;
		
	}

    public void debugOffset(){
		if (cond != null) {
            cond.debugOffset();
        }
        if (body != null) {
            body.debugOffset();
        }

	}
}