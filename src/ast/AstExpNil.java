package ast;

public class AstExpNil extends AstExp {

    public AstExpNil() {
        // NIL has no value
    }

    @Override
    public void printMe() {
        System.out.println("NIL");
    }
}
