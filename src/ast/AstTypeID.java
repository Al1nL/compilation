package ast;

public class AstTypeID extends AstType
{
    public final String name;

    public AstTypeID(String name) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
    }
    @Override
    public void printMe() {
        System.out.println("TYPE: " + name);
        AstGraphviz.getInstance().logNode(serialNumber, "TYPE: \n" + name);
        
    }
}