package ast;

import java.util.*;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstExpCall extends AstExp {

    public final String name;
    public final ArrayList<AstExp> args;

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
        IrCommand curIrCommand = new IrCommandCallFunc(dst, name, argTemps);
        if(Ir.curClass!=null){
            Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField).add(curIrCommand);
        }
        else{
            Ir.getInstance().AddIrCommand(curIrCommand); // jal
        }
        
    }
    return dst;
}
    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
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
