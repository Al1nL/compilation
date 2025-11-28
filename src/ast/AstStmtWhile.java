package ast;

import types.*;
import symboltable.*;



public class AstStmtWhile extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtWhile(AstExp cond, AstStmtList body)
	{
		this.serialNumber = AstNodeSerialNumber.getFresh();
		this.cond = cond;
		this.body = body;
	}

	@Override
    public void printMe() {
        System.out.print("WHILE");
        String label = "WHILE";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (cond != null) {AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);}
		if (body != null) {AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);}

		if (cond != null) {cond.printMe();}
		if (body != null) {body.printMe();}
		
    }

	public void semantMe(Type expectedReturnType)
{
    // Check condition is int
    Type condType = cond.semantMe();
    if (!(condType instanceof TypeInt))
    {
        System.err.println("ERROR: While condition must be int, got " + condType.name);
    	report();
	}
    
    // Begin scope for while body
    SymbolTable.getInstance().beginScope();
    if (body != null)
    {
        body.semantMe();
    }
    SymbolTable.getInstance().endScope();
}
}