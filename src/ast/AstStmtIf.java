package ast;
import types.*;
import symboltable.*;


public class AstStmtIf extends AstStmt
{
	public AstExp cond;
	public AstStmtList body;
	public AstStmtList else_body;
	

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AstStmtIf(AstExp cond, AstStmtList body,AstStmtList elseBody)
	{
		this.serialNumber = AstNodeSerialNumber.getFresh();
		this.cond = cond;
		this.body = body;
		this.else_body=elseBody;
	}

	@Override
    public void printMe() {
        System.out.print("IF");
        String label = "IF";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (cond != null) {AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);}
		if (body != null) {AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);}
		if (else_body != null) {AstGraphviz.getInstance().logEdge(serialNumber, else_body.serialNumber);}

		if (cond != null) {cond.printMe();}
		if (body != null) {body.printMe();}
		if (else_body != null) {else_body.printMe();}
    }
	
	public void semantMe(Type expectedReturnType)
{
    // Check condition is int
    Type condType = cond.semantMe();
    if (!(condType instanceof TypeInt))
    {
        System.err.println("ERROR: If condition must be int, got " + condType.name);
		report();
	}
    
    // Begin scope for if body
    SymbolTable.getInstance().beginScope();
    if (body != null)
    {
        body.semantMe();
    }
    SymbolTable.getInstance().endScope();
    
    // Begin scope for else body if exists
    if (else_body != null)
    {
        SymbolTable.getInstance().beginScope();
        else_body.semantMe();
        SymbolTable.getInstance().endScope();
    }
}
}