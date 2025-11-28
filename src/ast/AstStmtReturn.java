package ast;

import types.*;


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

    public void semantMe(Type expectedReturnType)
{
    if (exp == null)
    {
        // return; with no value
        if (!(expectedReturnType instanceof TypeVoid))
        {
            System.err.println("ERROR: Function must return a value of type " + expectedReturnType.name);
            report();
        }
    }
    else
    {
        // return exp;
        Type returnType = exp.semantMe();
        
        if (!returnType.canAssignTo(expectedReturnType))
        {
            System.err.println("ERROR: Return type " + returnType.name + " does not match expected type " + expectedReturnType.name);
            report();   
        }
    }
}
}
