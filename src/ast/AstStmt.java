package ast;
import types.*;


public abstract class AstStmt extends AstNode
{
    public Type semantMe(Type expectedReturnType)
	{
		return null;
	}
}
