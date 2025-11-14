package ast;

public class AstVarDec extends AstCField
{
    public AstType type;
    public String name;
    public AstExp exp;

    public AstVarDec(AstType type, String name, AstExp exp) {
        this.type = type;
        this.name = name;
        this.exp = exp;
    }
}
