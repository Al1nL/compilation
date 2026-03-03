package ast;

import ir.*;
import java.util.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.*;

public class AstVarSimple extends AstVar
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	public Integer offset;
	public boolean isFieldInMethod = false;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSimple(String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();
	
		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.name = name;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void printMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR ( %s )\n", this.name);

		/*********************************/
		/* Print to AST GRAPHVIZ DOT file */
		/*********************************/
		AstGraphviz.getInstance().logNode(serialNumber, String.format("SIMPLE\nVAR\n(%s)", this.name));
	}

	public Type semantMe()
	{
	SymbolTableEntry e = SymbolTable.getInstance().findEntry(this.name);
	if (e == null) {
    	System.out.format(">> ERROR [%d] Variable %s used but not declared!\n", lineNumber + 1, name);
        report();
	}
	Type t = e.type;
	this.var = Variable.get(name, e.scopeLevel, AstDecFunc.currentFunctionName);
	//System.out.println("varname: " + name + " "+ lineNumber);
    return t;	
	}

	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
	public Temp irMe()
	{
		Temp t = TempFactory.getInstance().getFreshTemp();
		IrCommand curIrCommand = new IrCommandLoad(t,var);
        if(Ir.curClass!=null){
            Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField).add(curIrCommand);
        }
        else{
			if(isFieldInMethod){ //we're referncing a field in a method
				Temp base = TempFactory.getInstance().getFreshTemp();
				IrCommand loadObjectCommand = new IrCommandLoad(base,null); //loading the object
				curIrCommand = new IrCommandLoadField(t, base, name, offset); //loading the field from the object
				Ir.getInstance().AddIrCommand(loadObjectCommand); 
			}
            Ir.getInstance().AddIrCommand(curIrCommand); 
        }
		
    	t.dependencySet.add(var);

		return t;
	}

	public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
        
		if(curClass!=null && classFieldOffsets.get(curClass).containsKey(name)){
			offset = classFieldOffsets.get(curClass).get(name);
			isFieldInMethod = true;
		}
		else{
			offset = offsets.get(var);
		}
		
		if (offset != null) {
			var.offset = offset;
		}
		return curIdx;
		
	}

	public void debugOffset(){
		
		if(offset!=null){
			System.out.println(var.name + "#" + var.scope + " - " + offset + ":");
		}

		
	}
}