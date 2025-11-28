package ast;



public class AstParam extends AstNode{
    public final String type;
    public final String name;

    public AstParam(String type, String name) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
    }

    public void printMe() {
        System.out.print("AST NODE PARAM\n");

        String label = "PARAM\n" + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);

        // if (type != null) {
        //     AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        //     type.printMe();
        // }
    }
}