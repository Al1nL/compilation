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
}
