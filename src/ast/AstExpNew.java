package ast;

public class AstExpNew extends AstExp {
    public final AstType type;
    public final AstExp sizeExp; // may be null

    public AstExpNew(AstType type, AstExp sizeExp) {
        this.type = type;
        this.sizeExp = sizeExp;
    }

    @Override
    public void printMe() {
        System.out.print("NEW ");
        if (type != null) type.printMe();
        if (sizeExp != null) {
            System.out.print("{ ");
            sizeExp.printMe();
            System.out.println(" }");
        }
    }
}
