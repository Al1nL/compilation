package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.Variable;

public class AstVarSimple extends AstVar
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;

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
	Type t = SymbolTable.getInstance().find(name);
	SymbolTableEntry e = SymbolTable.getInstance().findEntry(this.name);
	int scope = SymbolTable.getInstance().currScopeLevel;
	if(e != null){ scope = e.scopeLevel; }
	this.var = Variable.get(name, scope); 
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
		analysis.Dbg.p("AstVarSimple.irMe name=" + name + " deps=" + t.dependencySet);

		return t;
	}
}