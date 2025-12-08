package ast;

import java.util.ArrayList;
import types.*;

public class AstExpMethodCall extends AstExp {

    public final AstVar object;
    public final String method;
    public final ArrayList<AstExp> args;

    public AstExpMethodCall(AstVar object, String method, ArrayList<AstExp> args) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.object = object;
        this.method = method;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("METHOD CALL: ");

        System.out.print("." + method + "(");
        System.out.println(")");

        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)", method));
        if (object != null) {
            object.printMe();
        }
        if (args != null) {
            for (AstExp e : args) {
                e.printMe();
            }
        }
        System.out.println(")");
        if (object != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, object.serialNumber);
        }
        if (args != null) {
            for (AstExp e : args) {
                AstGraphviz.getInstance().logEdge(serialNumber, e.serialNumber);
            }
        }

    }

    public Type semantMe() {
        // 1. Analyze the object to get its type
        Type objectType = object.semantMe();

        if (objectType == null) {
            System.out.format(">> ERROR[%d]: Object has no type in method call to '%s'\n", lineNumber, method);
            report();
        }

        // 2. Ensure the object type is a class type
        if (!(objectType instanceof TypeClass)) {
            System.out.format(">> ERROR[%d]: Cannot call method '%s' on non-class type '%s'\n",
                    lineNumber, method, objectType.name);
            report();
        }

        TypeClass classType = (TypeClass) objectType;

        // 3. Look up the method in the class
        Type methodType = classType.findField(method);

        if (!(methodType instanceof TypeFunction)) {
            System.out.format(">> ERROR: Method '%s' not found in class '%s'\n",
                    method, classType.name);
            report();
        }

        return validateCall(method, (TypeFunction) methodType, args, true);
    }

}
