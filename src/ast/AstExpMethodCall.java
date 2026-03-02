package ast;

import ir.*;
import java.util.*;
import temp.*;
import types.*;
import variable.*;

public class AstExpMethodCall extends AstExp {

    public final AstVar object;
    public final String method;
    public final ArrayList<AstExp> args;
    public String className;
    public Integer offset;

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
        } else if (!(methodType instanceof TypeFunction) && !classType.isinitilized) {
            return expectedReturnType;
        }
        className = classType.name;
        return validateCall(method, (TypeFunction) methodType, args, true);
    }

    public Temp irMe() {
        String nullErrLabel = IrCommand.getFreshLabel("null_deref_error");
        String afterCallLabel = IrCommand.getFreshLabel("after_method_call");

        // If null → error
        Temp objTemp = object.irMe();
        Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(objTemp, nullErrLabel));

        // eval args + call
        ArrayList<Temp> argTemps = new ArrayList<>();
        for (AstExp exp : args) {
            argTemps.add(exp.irMe());
        }
        Temp dst = TempFactory.getInstance().getFreshTemp();
        IrCommand vcCommand1, vcCommand2, vcCommand3, vcCommand4;
        vcCommand1 = new IrCommandVirtualCall(dst, objTemp, method, offset, argTemps);
        vcCommand2 = new  IrCommandJumpLabel(afterCallLabel); // Jump past error handler
        vcCommand3 = new IrCommandLabel(nullErrLabel); // Error handler
        vcCommand4 = new IrCommandLabel(afterCallLabel); // IrCommandRuntimeError("null pointer dereference") — print + exit
        if(Ir.curClass!=null){
            List<IrCommand> commandList = Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField);

            commandList.add(vcCommand1);
            commandList.add(vcCommand2);
            commandList.add(vcCommand3);
            commandList.add(vcCommand4);
        }
        else{
            Ir.getInstance().AddIrCommand(vcCommand1);
            Ir.getInstance().AddIrCommand(vcCommand2);
            Ir.getInstance().AddIrCommand(vcCommand3);
            Ir.getInstance().AddIrCommand(vcCommand4);
        }
        
        return dst;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels) {
        offset = classMethodOffsets.get(className).get(method);
        curIdx = object.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        if (args != null) {
            for (AstExp e : args) {
                curIdx = e.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
            }
        }
        return curIdx;

    }

    public void debugOffset() {
        object.debugOffset();
        if (offset != null) {
            System.out.println(method + "#" + className + " - " + offset + ":");
        }

        if (args != null) {
            for (AstExp e : args) {
                e.debugOffset();
            }
        }
    }

}
