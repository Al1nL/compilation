package ast;

import java.util.*;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstStmtIf extends AstStmt {

    public AstExp cond;
    public AstStmtList body;
    public AstStmtList else_body;

    /*  CONSTRUCTOR(S) */
    public AstStmtIf(AstExp cond, AstStmtList body, AstStmtList elseBody) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;
        this.else_body = elseBody;
    }

    @Override
    public void printMe() {
        System.out.print("IF");
        String label = "IF";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (cond != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        }
        if (body != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        }
        if (else_body != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, else_body.serialNumber);
        }

        if (cond != null) {
            cond.printMe();
        }
        if (body != null) {
            body.printMe();
        }
        if (else_body != null) {
            else_body.printMe();
        }
    }

    public Type semantMe(Type expectedReturnType) {
        // Check condition is int
        Type condType = cond.semantMe();
        if (!(condType instanceof TypeInt)) {
            System.err.println("ERROR: If condition must be int, got " + condType.name);
            report();
        }

        // Begin scope for if body
        SymbolTable.getInstance().beginScope();
        if (body != null) {
            body.semantMe(expectedReturnType);
        }
        SymbolTable.getInstance().endScope();

        // Begin scope for else body if exists
        if (else_body != null) {
            SymbolTable.getInstance().beginScope();
            else_body.semantMe(expectedReturnType);
            SymbolTable.getInstance().endScope();
        }
        return null;
    }

    public Temp irMe()
    {
        /*******************************/
        /* [1] Allocate fresh labels */
        /*******************************/
    
        String labelElse = IrCommand.getFreshLabel("else");
        String labelEnd = IrCommand.getFreshLabel("end");

        /*********************************/
        /* [2] entry label for the while */
        /*********************************/
        // Ir.
        //         getInstance().
        //         AddIrCommand(new IrCommandLabel(labelStart));

        /********************/
        /* [3] cond.IRme(); */
        /********************/
        Temp condTemp = cond.irMe();

        /******************************************/
        /* [4] Jump conditionally to the loop end */
        /******************************************/
        if(else_body != null){
            Ir.
                getInstance().
                AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelElse));
        }else{
            Ir.
                getInstance().
                AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelEnd));
        }
        

        /*******************/
        /* [5] body.IRme() */
        /*******************/
        body.irMe();

        /******************************/
        /* [6] Jump to the else entry */
        /******************************/
        if(else_body != null){
            Ir.
                    getInstance().
                    AddIrCommand(new IrCommandLabel(labelElse));
                
            else_body.irMe();
        }
        

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

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
        
        if (cond != null) {
            curIdx = cond.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        if (body != null) {
            curIdx = body.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        if (else_body != null) {
            curIdx = else_body.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
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
        if(else_body != null) {
            else_body.debugOffset();
        }
	}
}
