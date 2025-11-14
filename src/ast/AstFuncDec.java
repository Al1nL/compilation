package ast;

public class AstFuncDec extends AstCField {
    private final AstType returnType;
    private final String name;
    private final AstParamList params;
    private final AstStmtList body;

    public AstFuncDec(AstType returnType, String name,
                      AstParamList params, AstStmtList body) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
}