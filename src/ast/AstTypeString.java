package ast;

public class AstTypeString extends AstType {
    public AstTypeString() {
        this.serialNumber = AstNodeSerialNumber.getFresh();  // assign unique ID
    }

    @Override
    public void printMe() {
        System.out.println(" TYPE STRING");
        AstGraphviz.getInstance().logNode(serialNumber, "STRING");
    }
}