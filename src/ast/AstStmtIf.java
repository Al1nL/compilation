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
	
	public Type semantMe()
	{
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.semantMe() != TypeInt.getInstance())
		{
			System.out.format(">> ERROR [%d:%d] condition inside IF is not integral\n",2,2);
		}
		
		/*************************/
		/* [1] Begin If Scope */
		/*************************/
		SymbolTable.getInstance().beginScope();

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		body.semantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/***************************************************/
		/* [4] Return value is irrelevant for if statement */
		/**************************************************/
		return null;		
	}
}