package ast;

import java.util.*;
import temp.*;
import temp.*;
import ir.*;
import variable.*;


public class AstExpList extends AstNode
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AstExp head;
	public AstExpList tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstExpList(AstExp head, AstExpList tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		this.head = head;
		this.tail = tail;
	}
	/*******************************************************/
	/* The printing message for a expression list AST node */
	/*******************************************************/
	public void printMe()
	{
		/********************************/
		/* AST NODE TYPE = AST EXP LIST */
		/********************************/
		System.out.print("AST NODE EXP LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.printMe();
		if (tail != null) tail.printMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"EXP\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AstGraphviz.getInstance().logEdge(serialNumber,head.serialNumber);
		if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber,tail.serialNumber);
	}

	public Temp irMe()
	{
		return head.irMe();
	}
	public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
        if (head  != null){ 
			curIdx = head.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
		}
		return curIdx;
		
	}
}
