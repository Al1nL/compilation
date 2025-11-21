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

        if (exp != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
            exp.printMe();
        }
    }
}
