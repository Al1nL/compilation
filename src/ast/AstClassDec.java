package ast;

public class AstClassDec extends AstDec
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    public final String name;         // class name
    public final String parentName;   // null if no EXTENDS
    public final AstDecList fields;   // linked list of cFields

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AstClassDec(String name, String parentName, AstDecList fields)
    {
        serialNumber = AstNodeSerialNumber.getFresh();

        if (parentName != null)
            System.out.print("===== classDec -> CLASS ID EXTENDS ID LBRACE cField { cField } RBRACE\n");
        else
            System.out.print("===== classDec -> CLASS ID LBRACE cField { cField } RBRACE\n");

        this.name = name;
        this.parentName = parentName;
        this.fields = fields;
    }
    @Override
    public void printMe() {
        String extendedName = parentName != null? name + "\n" + "EXTENDS\n" + parentName : name;
        System.out.print("CLASS " + name);
        if (parentName != null) System.out.print("Extends " + parentName);
        AstGraphviz.getInstance().logNode(serialNumber, "CLASS\n" + extendedName);
        if (fields != null){
            fields.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber,fields.serialNumber);
        } 

    }
}