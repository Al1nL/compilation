package ast;

import types.*;
import temp.*;
import ir.*;

public class AstDecList extends AstNode {

    public AstDec head;
    public AstDecList tail;

    public AstDecList(AstDec head, AstDecList tail) {
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

        AstGraphviz.getInstance().logNode(serialNumber, "DEC LIST");

        if (head != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        }
        if (tail != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
        }
    }

    @Override
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

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        if (head != null) head.irMe();
        if (tail != null) tail.irMe();

        return null;
    }
}