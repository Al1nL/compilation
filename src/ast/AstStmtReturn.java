package ast;

public class AstStmtReturn extends AstStmt {
    public final AstExp exp;  // The expression being returned, can be null for `return;`

    public AstStmtReturn(AstExp exp) {
        this.exp = exp;
    }

    @Override
    public void printMe() {
        System.out.print("RETURN");
        if (exp != null) {
            System.out.print(" ");
            exp.printMe();
        }
        System.out.println(";");
    }
}
