package ast;
import temp.*;
import ir.*;
import types.*;

public abstract class AstStmt extends AstNode {

    public Type semantMe() {
        return null;
    }

    public Type semantMe(Type expectedReturnType) {
        if(expectedReturnType == null) return semantMe();
        return semantMe(expectedReturnType);
    }

    public Temp irMe()
    {
        return null;
    }
    
}