package ast;

public class AstDecList extends AstNode {
    public AstDec head;
    public AstDecList  tail;

    public AstDecList(AstDec head, AstDecList tail) {
        serialNumber = AstNodeSerialNumber.getFresh();

        if (tail != null)
            System.out.print("===== cFieldList -> cField cFieldList\n");
        else
            System.out.print("===== cFieldList -> cField\n");

        this.head = head;
        this.tail = tail;
    }
}