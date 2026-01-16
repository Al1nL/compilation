package ast;
import ir.*;
import java.util.ArrayList;
import temp.*;
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
    public Type semantMe(){
        return semantMe(null);
    }
    public Type semantMe(Type expectedReturnType) {
        // Analyze the object to get its type
        Type objectType = object.semantMe();

        if (objectType == null) {
            System.out.format(">> ERROR[%d]: Object has no type in method call to '%s'\n", lineNumber, method);
            report();
        }

        // Ensure the object type is a class type
        if (!(objectType instanceof TypeClass)) {
            System.out.format(">> ERROR[%d]: Cannot call method '%s' on non-class type '%s'\n",
                    lineNumber, method, objectType.name);
            report();
        }

        TypeClass classType = (TypeClass) objectType;
        
        // Look up the method in the class
        Type methodType = classType.findField(method);
        if (!(methodType instanceof TypeFunction) && classType.isinitilized) {
            System.out.format(">> ERROR: Method '%s' not found in class '%s'\n",
                    method, classType.name);
            report();
        }
        else if(!(methodType instanceof TypeFunction) && !classType.isinitilized){
            return expectedReturnType;
        }
        return validateCall(method, (TypeFunction) methodType, args, true);
    }

    public Temp irMe()
    {
        /******************************/
        /* [1] Evaluate object        */
        /******************************/
        Temp objTemp = object.irMe();

        /*****************************************/
        /* [2] Runtime check: object != nil      */
        /*****************************************/
        String null_check = IrCommand.getFreshLabel("null_"+object.var.name+"_check");

        Ir.
            getInstance().
            AddIrCommand(new IrCommandJumpIfEqToZero(
                objTemp,
                null_check
            ));

        /******************************/
        /* [3] Evaluate arguments     */
        /******************************/
        ArrayList<Temp> argTemps = new ArrayList<>();

        for (AstExp exp : args)
        {
            argTemps.add(exp.irMe());
        }

        /******************************/
        /* [4] Allocate return temp   */
        /******************************/
        Temp dst = TempFactory.getInstance().getFreshTemp();

        /********************************************/
        /* [5] Virtual method call                  */
        /********************************************/
        Ir.
            getInstance().
            AddIrCommand(new IrCommandVirtualCall(
                dst,
                objTemp,
                method,
                argTemps
            ));

        /*****************************************/
        /* [5.5] Error handler (define label)    */
        /*****************************************/
        Ir.getInstance().AddIrCommand(new IrCommandLabel(null_check));
        //Ir.getInstance().AddIrCommand(new IrCommandRuntimeError("Null pointer dereference"));
        
        /*******************/
        /* [6] return dst */
        /*******************/
        return dst;
    }

}