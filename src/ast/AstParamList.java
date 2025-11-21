package ast;

public class AstParamList extends AstNode
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    public AstParam head;
    public AstParamList tail;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AstParamList(AstParam head, AstParamList tail)
    {
        serialNumber = AstNodeSerialNumber.getFresh();

        if (tail != null)
            System.out.print("===== paramList -> param paramList\n");
        else
            System.out.print("===== paramList -> param\n");

        this.head = head;
        this.tail = tail;
    }
    public void printMe() {
    if (head != null) head.printMe();
    if (tail != null) tail.printMe();

    
    AstGraphviz.getInstance().logNode(serialNumber, "PARAM LIST");

    if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
    
    if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
}
}
