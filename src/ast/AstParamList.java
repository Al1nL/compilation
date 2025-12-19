package ast;
import temp.*;
import ir.*;

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

    public Temp irMe()
    {
        return head.irMe();
    }
}