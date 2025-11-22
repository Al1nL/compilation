package ast;

public class AstExpNil extends AstExp {

    public AstExpNil() {
        serialNumber = AstNodeSerialNumber.getFresh();
    }

    @Override
    public void printMe() {
        System.out.println("NIL");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("NIL"));
    }
}
