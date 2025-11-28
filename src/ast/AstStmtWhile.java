package ast;


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
}