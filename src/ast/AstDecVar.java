package ast;

import ir.*;
import java.util.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.*;
public class AstDecVar extends AstDec {

    public String type;
    public String name;
    public AstExp exp;
    public Variable var;
    public Integer offset;
    public AstDecVar(String type, String name, AstExp exp) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.exp = exp;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");

        String label = "VAR DEC\n" + this.type + " " + this.name;
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (exp != null) {
            exp.printMe();
        }

        if (exp != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
        }

    }

    public Type semantMe() {
        Type varType = null;
        Type varname = null;
        // Check for dups
        SymbolTableEntry e = SymbolTable.getInstance().findInScope(name);
        varname = e == null ? null : e.type;
        if (varname != null) {
            System.err.println("ERROR: Variable '" + this.name + "' is already defined in this scope");
            report();
        }
        // Check for primitive types
        if (type.equals("int")) {
            varType = TypeInt.getInstance();

        } else if (type.equals("string")) {
            varType = TypeString.getInstance();

        } else if (type.equals("void")) {
            // Variables cannot be void
            System.err.println("ERROR: Variable '" + this.name + "' cannot have type void");
            report();
        } else {
            // Must be a class or array type
            varType = SymbolTable.getInstance().find(type);
            if (varType == null) {
                System.err.println("ERROR: Type '" + this.type + "' is not defined");
                report();
            }
        }

        if (exp != null) {
            Type expType = exp.semantMe(null);
            if (expType == null) {
                            System.err.println("ERROR: the expression you want to assign to "+name+" is not defined");
                            report();
                        }
            if (!expType.canAssignTo(varType)) {
                System.err.println("ERROR: Cannot assign expType " + expType.name + " to " + varType.name);
                report();
            }
        }
        
    
        // Enter variable to symbol table   
    
        SymbolTable.getInstance().enter(name, varType);
        var = Variable.get(name, SymbolTable.getInstance().currScopeLevel, AstDecFunc.currentFunctionName);
        return varType;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe() {
        analysis.Dbg.p("AstDecVar.irMe name=" + var.name);
        if(Ir.curClass!=null){
            if (exp != null) {
                Ir.curField = offset;
                Ir.fieldInitIrCommands.get(Ir.curClass).put(Ir.curField, new ArrayList<>());
                List<IrCommand> commandListofField = Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField);
                commandListofField.add(new IrCommandAllocate(var));
                commandListofField.add(new IrCommandStore(var, exp.irMe()));
            }
        }
        else{
            Ir.getInstance().AddIrCommand(new IrCommandAllocate(var));

            if (exp != null) {
                Ir.getInstance().AddIrCommand(new IrCommandStore(var, exp.irMe()));
            }
        }
        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
		
        if(curClass!=null&&offsets==null){
            classFieldOffsets.get(curClass).put(name, curIdx);
            var.offset = curIdx;
            offset = curIdx;
            curIdx++;
        }

        else if (offsets!=null){
            if (!offsets.containsKey(var)) {
                offsets.put(var, curIdx);
                var.offset = curIdx;
                curIdx++;
                offset = var.offset;
                //TODO: figure out where to save the index in here?
                //in its own field or vairble
            }
            else{
                offset = offsets.get(var);
            }
            
        }

        if (exp != null) {
            curIdx = exp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        return curIdx;

        /*if (offsets!=null){
            offsets.put(var, curIdx);
            var.offset = curIdx;
            curIdx++;
            offset = var.offset;
            //TODO: figure out where to save the index in here?
            //in its own field or vairble

        }
        else if(curClass!=null){
            classFieldOffsets.get(curClass).put(name, curIdx);
            var.offset = curIdx;
            curIdx++;
        }
        if (exp != null) {
            curIdx = exp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        }
        return curIdx;
        */
        
	}
    public void debugOffset(){
        if(offset!=null){
            System.out.println(var.name + "#" + var.scope + " - " + offset + ":");
        }

		if (exp != null) {
            exp.debugOffset();
        }
	}
}
