package ast;

import ir.*;
import java.util.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.*;

public class AstExpCall extends AstExp {

    public final String name;
    public final ArrayList<AstExp> args;
    // Populated during offsetMe when this is an intra-class method call
    public Integer methodOffset = null;
    public String resolvedClassName = null;

    public AstExpCall(String name, ArrayList<AstExp> args) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.args = args;
    }

    @Override
    public void printMe() {
        System.out.print("CALL " + name + "(");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)", name));
        if (args != null) {
            for (AstExp e : args) {
                e.printMe();
            }
        }
        System.out.println(")");
        if (args != null) {
            for (AstExp e : args) {
                AstGraphviz.getInstance().logEdge(serialNumber, e.serialNumber);
            }
        }

    }

    public Type semantMe() {
        // Look up the function in the symbol table
        TypeFunction funcType = (TypeFunction) SymbolTable.getInstance().find(name);
        Type result = validateCall(name, funcType, args, false);
        if (result == null) {
            report();
        }
        return result;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

   public Temp irMe() {
    ArrayList<Temp> argTemps = new ArrayList<>();
    if (args != null) {
        for (AstExp e : args) {
            argTemps.add(e.irMe());   // collect ALL arg temps
        }
    }
    Temp dst = TempFactory.getInstance().getFreshTemp();
    if (name.equals("PrintString")) {
        Ir.getInstance().AddIrCommand(new IrCommandPrintString(argTemps.get(0)));
    } else if (name.equals("PrintInt")) {
        Ir.getInstance().AddIrCommand(new IrCommandPrintInt(argTemps.get(0)));
    } else {
        // Detect intra-class method call: bare call inside a method body where
        // the callee is a method of the current class (resolved via offsetMe).
        boolean isIntraClassMethod = (Ir.currentMethodClass != null) && (methodOffset != null);

        IrCommand curIrCommand;
        if (isIntraClassMethod) {
            // Load 'this' from the implicit first arg slot (offset -1 => 8($fp))
            Temp thisTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, null));
            // Virtual dispatch so overridden methods in subclasses are called correctly
            curIrCommand = new IrCommandVirtualCall(dst, thisTemp, name, methodOffset, argTemps);
        } else {
            // Free-function call (or library call resolved by label)
            String resolvedLabel = name;
            if (Ir.currentMethodClass != null) {
                Map<String, String> classLabels = Ir.methodLabelsMap.get(Ir.currentMethodClass);
                if (classLabels != null && classLabels.containsKey(name)) {
                    resolvedLabel = classLabels.get(name);
                }
            }
            curIrCommand = new IrCommandCallFunc(dst, resolvedLabel, argTemps);
        }

        // Emit to the correct IR stream: class field init, global init, or function body
        if (Ir.curClass != null && Ir.curField >= 0) {
            Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField).add(curIrCommand);
        } else {
            Ir.getInstance().AddIrCommand(curIrCommand);
        }
    }
    return dst;
}
    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
        // Detect intra-class method call: resolve vtable offset for virtual dispatch in irMe()
        if (curClass != null
                && classMethodOffsets.containsKey(curClass)
                && classMethodOffsets.get(curClass).containsKey(name)) {
            methodOffset = classMethodOffsets.get(curClass).get(name);
            resolvedClassName = curClass;
        }
        if(args != null){
            for (AstExp e : args) {
                curIdx = e.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
            }
        }
        return curIdx;
	}

    public void debugOffset(){
		if(args != null){
            for (AstExp e : args) {
                e.debugOffset();
            }
        }
	}
}