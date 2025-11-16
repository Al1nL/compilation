package ast;

import java.util.ArrayList;

public class AstExpMethodCall extends AstExp {
    public final AstVar object;
    public final String method;
    public final ArrayList<AstExp> args;

    public AstExpMethodCall(AstVar object, String method, ArrayList<AstExp> args) {
        this.object = object;
        this.method = method;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("METHOD CALL: ");
        object.printMe();
        System.out.print("." + method + "(");
        if (args != null) for (AstExp e : args) e.printMe();
        System.out.println(")");
    }
}
