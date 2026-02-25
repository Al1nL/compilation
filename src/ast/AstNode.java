package ast;

import java.util.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;


public abstract class AstNode
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int serialNumber;
	public int lineNumber=-1;
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void printMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	public Type semantMe()
	{
		return null;
	}
	public void report() {
        throw new RuntimeException("ERROR(" + this.lineNumber+")");
    }
	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
	/*****************************************/
	/* The default IR action for an AST node */
	/*****************************************/
	public Temp irMe()
	{
		return null;
	}

	public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
		return curIdx;
		
	}
	public void debugOffset(){
		
	}
}
