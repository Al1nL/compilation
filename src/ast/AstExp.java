package ast;

import java.util.ArrayList;
import types.*;
import temp.*;
import ir.*;

public abstract class AstExp extends AstStmt {

    // public Variable var;
    public Type semantMe() {
        return null;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    /*****************************************/
    /* The default IR action for an AST node */
    /*****************************************/
    public Temp irMe()
    {
        return null;
    }
    // Helper method to validate function/method call arguments

    public static Type validateCall(String callName, TypeFunction funcType,
            ArrayList<AstExp> args, boolean isMethodCall) {
        if (funcType == null) {
            System.err.format(">> ERROR: %s '%s' is not defined\n",
                    isMethodCall ? "Method" : "Function", callName);
            return null;
        }

        // Check argument count
        int expectedArgs = (funcType.params != null) ? funcType.params.len : 0;
        int actualArgs = (args != null) ? args.size() : 0;

        if (expectedArgs != actualArgs) {
            System.err.format(">> ERROR: %s '%s' expects %d arguments but got %d\n",
                    isMethodCall ? "Method" : "Function",
                    callName, expectedArgs, actualArgs);
            return null;
        }

        // Type-check each argument
        if (args != null && funcType.params != null) {
            TypeList paramList = funcType.params;

            for (int i = 0; i < args.size(); i++) {
                Type argType = args.get(i).semantMe();
                Type paramType = paramList.head;

                if (argType == null) {
                    System.err.format(">> ERROR: Argument %d in call to '%s' has no type\n",
                            i + 1, callName);
                    args.get(i).report();
                    return null;
                }
                if (paramType == null) {
                    System.err.format(">> ERROR: Parameter %d of %s '%s' has no type defined\n",
                            i + 1,
                            isMethodCall ? "method" : "function",
                            callName);
                    args.get(i).report();
                    return null;
                }
                if (!argType.canAssignTo(paramType)) {
                    System.err.format(">> ERROR: Argument %d of %s '%s': expected '%s' but got '%s'\n",
                            i + 1,
                            isMethodCall ? "method" : "function",
                            callName, paramType.name, argType.name);
                    args.get(i).report();
                    return null;
                }

                paramList = paramList.tail;
            }
        }

        return funcType.returnType;
    }
}
