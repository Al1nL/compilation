package ast;

public class AstTypedefArray extends AstDec {
    public final String name;
    public final AstType type;

    public AstTypedefArray(String name, AstType type) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.type = type;
    }

    @Override
    public void printMe() {
        System.out.println("ARRAY TYPEDEF: " + name);
        String label = "ARRAY\n" + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);
        if (type != null){
            type.printMe();
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        } 
    }
}