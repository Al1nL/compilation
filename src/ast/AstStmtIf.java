package ast;

import ir.*;
import java.util.*;
import symboltable.*;
import temp.*;
import types.*;
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
        /* Allocate fresh labels */    
        String labelElse = IrCommand.getFreshLabel("else");
        String labelEnd = IrCommand.getFreshLabel("end");

        /* cond.IRme(); */
        Temp condTemp = cond.irMe();

        /* Jump conditionally to the loop end */
        if(else_body != null){
            Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelElse));
        }else{
            Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp,labelEnd));
        }
        
        /* body.IRme() */
        body.irMe();

        /* Jump to the else entry */
        if(else_body != null){
            Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(labelEnd));  // skip else
            Ir.getInstance().AddIrCommand(new IrCommandLabel(labelElse));
            else_body.irMe();
        }
        
        /* add end label */
        Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
        
        if (cond != null) {
            curIdx = cond.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        if (body != null) {
            curIdx = body.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        if (else_body != null) {
            curIdx = else_body.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
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
