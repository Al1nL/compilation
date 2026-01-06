package ast;

import types.*;
import temp.*;
import ir.*;

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
}
