package ast;

public class AstTypedefArray extends AstDec {
    public final String name;
    public final AstType type;

    public AstTypedefArray(String name, AstType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void printMe() {
        System.out.println("ARRAY TYPEDEF: " + name);
        if (type != null) type.printMe();
    }
}