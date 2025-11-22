package ast;

public class AstTypeVoid extends AstType {
    public AstTypeVoid() {
        this.serialNumber = AstNodeSerialNumber.getFresh();  // assign unique ID
    }

    @Override
    public void printMe() {
        System.out.println("TYPE VOID");
        AstGraphviz.getInstance().logNode(serialNumber, "VOID");
    }
}