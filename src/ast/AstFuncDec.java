package ast;

public class AstFuncDec extends AstCField {
    private final AstType returnType;
    private final String name;
    private final AstParamList params;
    private final AstStmtList body;

    public AstFuncDec(AstType returnType, String name, AstParamList params, AstStmtList body) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    public void printMe() {
        System.out.print("AST NODE FUNC DEC\n");

        // 1. Log THIS node first
        String label = "FUNC DEC\n" + name;
        AstGraphviz.getInstance().logNode(serialNumber, label);

        // 2. Log edges to children
        if (returnType != null)
            AstGraphviz.getInstance().logEdge(serialNumber, returnType.serialNumber);
        if (params != null)
            AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);

        // 3. Then print children
        if (returnType != null) returnType.printMe();
        if (params != null) params.printMe();
        if (body != null) body.printMe();
    }
}