package ast;


public class AstParam extends AstNode{
    public final AstType type;
    public final String name;

    public AstParam(AstType type, String name) {
        this.type = type;
        this.name = name;
    }
}