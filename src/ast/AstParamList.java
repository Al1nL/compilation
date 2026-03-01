package ast;
import java.util.*;
import temp.*;
import ir.*;
import variable.*;

public class AstParamList extends AstNode {

    /* DATA MEMBERS */
    public AstParam head;
    public AstParamList tail;

    /* CONSTRUCTOR(S) */
    public AstParamList(AstParam head, AstParamList tail) {
        serialNumber = AstNodeSerialNumber.getFresh();

        this.head = head;
        this.tail = tail;
    }

    public void printMe() {
        if (head != null) {
            head.printMe();
        }
        if (tail != null) {
            tail.printMe();
        }
        AstGraphviz.getInstance().logNode(serialNumber, "PARAM LIST");

        if (head != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        }

        if (tail != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
        }
    }

    public Temp irMe() {
        /*******************************/
        /* [1] Generate IR for head param */
        /*******************************/
        if (head != null) {
            head.irMe();
        }

        /*******************************/
        /* [2] Recursively generate IR for tail params */
        /*******************************/
        if (tail != null) {
            tail.irMe();
        }

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
		if (head != null) {
            curIdx = head.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }

        if (tail != null) {
            curIdx = tail.offsetMe(offsets, curIdx-1, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
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