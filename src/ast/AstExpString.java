package ast;

public class AstExpString extends AstExp {
    public final String value;

    public AstExpString(String value) {
        this.value = value;
    }

    @Override
    public void printMe() {
        System.out.println("STRING: \"" + value + "\"");
    }
}
