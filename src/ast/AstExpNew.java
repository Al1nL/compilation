package ast;


public class AstExpNew extends AstExp {
    public final String type;
    public final AstExp sizeExp; // may be null

    public AstExpNew(String type, AstExp sizeExp) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.sizeExp = sizeExp;
    }

    @Override
    public void printMe() {
        System.out.print("NEW ");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("NEW"));
        if (type != null) type.toString();
        if (sizeExp != null) {
            System.out.print("{ ");
            sizeExp.printMe();
            System.out.println(" }");
        }
        //if (type != null) AstGraphviz.getInstance().logEdge(serialNumber,type.serialNumber);
        if (sizeExp != null) AstGraphviz.getInstance().logEdge(serialNumber,sizeExp.serialNumber);
    }
}
