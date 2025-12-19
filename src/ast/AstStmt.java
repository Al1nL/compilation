package ast;
import temp.*;
import ir.*;
import types.*;

public abstract class AstStmt extends AstNode {

    public Type semantMe() {
        return null;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        return null;
    }
    
}