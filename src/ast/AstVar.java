package ast;
import variable.Variable;

public abstract class AstVar extends AstNode
{
    public Variable var;   // semantic variable (name + scope)

}
