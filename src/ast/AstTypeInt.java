package ast;

public class AstTypeInt extends AstType {
    public AstTypeInt() {
        this.serialNumber = AstNodeSerialNumber.getFresh();  // assign unique ID
    }

    @Override
    public void printMe() {
        System.out.println("TYPE INT");
        AstGraphviz.getInstance().logNode(serialNumber, "INT");
    }
}
