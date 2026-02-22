package ast;

import java.util.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstStmtCall extends AstStmt {

    /* DATA MEMBERS */
    public AstExp callExp;

    /* CONSTRUCTOR(S) */
    public AstStmtCall(AstExp callExp) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();

        this.callExp = callExp;
    }

    public void printMe() {
        callExp.printMe();

        /* PRINT Node to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(serialNumber, String.format("STMT\nCALL"));

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logEdge(serialNumber, callExp.serialNumber);
    }

    @Override
    public Type semantMe() {

        return callExp.semantMe();
    }

    public Type semantMe(Type expectedReturnType) {
        return callExp.semantMe(expectedReturnType);
    }

    public Temp irMe()
    {
        if (callExp != null) callExp.irMe();

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
        
        if (callExp != null){
            curIdx = callExp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        return curIdx;
	}

    public void debugOffset(){
		if (callExp != null) {
            callExp.debugOffset();
        }
	}
}
