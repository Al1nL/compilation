package ast;

import java.util.*;
import ir.*;
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
	Type t = SymbolTable.getInstance().find(this.name);
	this.var = Variable.get(name, e.scopeLevel); 
    return t;	
	}

	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
	public Temp irMe()
	{
		Temp t = TempFactory.getInstance().getFreshTemp();
		Ir.getInstance().AddIrCommand(new IrCommandLoad(t,var));
    	t.dependencySet.add(var);
		analysis.Dbg.p("AstVarSimple.irMe name=" + name + " dependencies=" + t.dependencySet);

		return t;
	}

	public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
        
		if(curClass!=null && classFieldOffsets.get(curClass).containsKey(name)){
			offset = classFieldOffsets.get(curClass).get(name);
		}
		else{
			offset = offsets.get(var);
		}
        
		return curIdx;
		
	}

	public void debugOffset(){
		
		if(offset!=null){
			System.out.println(var.name + "#" + var.scope + " - " + offset + ":");
		}

		
	}
}