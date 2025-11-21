package ast;

import java.util.ArrayList;

public class AstExpCall extends AstExp {
    public final String name;
    public final ArrayList<AstExp> args;

    public AstExpCall(String name, ArrayList<AstExp> args) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("CALL " + name + "(");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)",name));
        if (args != null) for (AstExp e : args) e.printMe();
        System.out.println(")");
        if (args != null) for (AstExp e : args) AstGraphviz.getInstance().logEdge(serialNumber,e.serialNumber);

    }
}
