package ast;

import java.util.ArrayList;

public class AstExpCall extends AstExp {
    public final String name;
    public final ArrayList<AstExp> args;

    public AstExpCall(String name, ArrayList<AstExp> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("CALL " + name + "(");
        if (args != null) for (AstExp e : args) e.printMe();
        System.out.println(")");
    }
}
