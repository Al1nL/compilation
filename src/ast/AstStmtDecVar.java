package ast;

import java.util.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstStmtDecVar extends AstStmt
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstDecVar var;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstStmtDecVar(AstDecVar var)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.var = var;
	}
	
	public void printMe()
	{
		var.printMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("STMT\nDEC\nVAR"));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
	}

	public Type semantMe()
	{
		return var.semantMe();
	}
	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
	public Temp irMe() { return var.irMe(); }

	public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
        
        return var.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
		
	}

	public void debugOffset(){
		var.debugOffset();
	}
}
