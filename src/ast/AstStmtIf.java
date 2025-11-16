package ast;

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
		this.cond = cond;
		this.body = body;
		this.else_body=elseBody;
	}
}