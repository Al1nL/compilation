package ast;


public class AstStmtReturn extends AstStmt {
    public final AstExp exp;  // The expression being returned, can be null for `return;`

    public AstStmtReturn(AstExp exp) {
        this.serialNumber = AstNodeSerialNumber.getFresh(); 
        this.exp = exp;
    }

    @Override
    public void printMe() {
        System.out.print("RETURN");
        String label = "RETURN";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (exp != null) {
        AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);

        // 3. Recursively print the expression
        exp.printMe();
        }
    }
}
