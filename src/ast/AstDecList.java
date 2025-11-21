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
    public void printMe() {
    if (head != null) head.printMe();
    if (tail != null) tail.printMe();

    
    AstGraphviz.getInstance().logNode(serialNumber, "DEC LIST");

    if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
    
    if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.head.serialNumber);
}
}