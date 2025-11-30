package ast;
import types.*;


public abstract class AstDec extends AstStmt
{
    public Type semantMe()
	{
		return null;
	}
	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
}