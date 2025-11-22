package ast;

public class AstVarDec extends AstCField
{
    public AstType type;
    public String name;
    public AstExp exp;

    public AstVarDec(AstType type, String name, AstExp exp) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.exp = exp;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");

        String label = "VAR DEC\n" + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (type != null) type.printMe();
        if (exp != null) exp.printMe();

        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
        
    }
}
