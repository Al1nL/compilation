package ast;
import java.util.ArrayList;

public class AstExpMethodCall extends AstExp {
    public final AstVar object;
    public final String method;
    public final ArrayList<AstExp> args;

    public AstExpMethodCall(AstVar object, String method, ArrayList<AstExp> args) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.object = object;
        this.method = method;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("METHOD CALL: ");
        
        System.out.print("." + method + "(");
        System.out.println(")");

        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)",method));
        if (object != null) object.printMe();
        if (args != null) for (AstExp e : args) e.printMe();
        System.out.println(")");
        if (object != null) AstGraphviz.getInstance().logEdge(serialNumber,object.serialNumber);
        if (args != null) for (AstExp e : args) AstGraphviz.getInstance().logEdge(serialNumber,e.serialNumber);

    }
}
