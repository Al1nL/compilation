package ast;

import java.util.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstStmtList extends AstNode {

    /* DATA MEMBERS */
    public AstStmt head;
    public AstStmtList tail;

    /* CONSTRUCTOR(S) */
    public AstStmtList(AstStmt head, AstStmtList tail) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();

        /* COPY INPUT DATA MEMBERS ... */
        this.head = head;
        this.tail = tail;
    }

    /* The printing message for a statement list AST node */
    public void printMe() {
        /* AST NODE TYPE = AST STATEMENT LIST */
        System.out.print("AST NODE STMT LIST\n");

        /* RECURSIVELY PRINT HEAD + TAIL ... */
        if (head != null) {
            head.printMe();
        }
        if (tail != null) {
            tail.printMe();
        }

        /* PRINT to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(serialNumber, "STMT\nLIST\n");

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        if (head != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        }
        if (tail != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
        }
    }

    public TypeList semantMe() {
        Type headType = null;
        TypeList tailTypeList = null;

        if (head != null) {
            headType = head.semantMe();
        }

        if (tail != null) {
            tailTypeList = tail.semantMe();

        }

        return new TypeList(headType, tailTypeList);
    }

    public TypeList semantMe(Type expectedReturnType) {
        Type headType = null;
        TypeList tailTypeList = null;

        if (head != null) {
            headType = head.semantMe(expectedReturnType);
        }

        if (tail != null) {
            tailTypeList = tail.semantMe(expectedReturnType);
        }

        return new TypeList(headType, tailTypeList);
    }

    public Temp irMe()
    {
        if (head != null) head.irMe();
        if (tail != null) tail.irMe();

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
		if (head != null) {
            curIdx = head.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }

        if (tail != null) {
            curIdx = tail.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        return curIdx;
	}

    public void debugOffset(){
		if (head != null) {
            head.debugOffset();
        }
        if (tail != null) {
            tail.debugOffset();
        }
	}
}
