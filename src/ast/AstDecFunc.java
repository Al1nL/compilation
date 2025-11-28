package ast;

import types.*;
import symboltable.*;

public class AstDecFunc extends AstDec {
    private final String returnType;
    private final String name;
    private final AstParamList params;
    private final AstStmtList body;

    public AstDecFunc(String returnType, String name, AstParamList params, AstStmtList body) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    public void printMe() {
        System.out.print("AST NODE FUNC DEC\n");

        // 1. Log THIS node first
        String label = "FUNC DEC\n" +returnType+" "+ name+"()";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        // 2. Log edges to children
        if (params != null)
            AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);

        // 3. Then print children
        if (params != null) params.printMe();
        if (body != null) body.printMe();
    }

	public Type semantMe()
	{
		Type t;
		//Type returnType = null;
		TypeList type_list = null;

		/*******************/
		/* [0] return type */
		/*******************/
        Type baseType = SymbolTable.getInstance().find(returnType);
		if (returnType == null)
		{
			System.out.format(">> ERROR [%d:%d] non existing return type %s\n",6,6,returnType);				
		}
	
		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		SymbolTable.getInstance().beginScope();

		/***************************/
		/* [2] Semant Input Params */
		/***************************/
		for (AstParamList it = params; it  != null; it = it.tail)
		{
			t = SymbolTable.getInstance().find(it.head.type);
			if (t == null)
			{
				System.out.format(">> ERROR [%d:%d] non existing type %s\n",2,2,it.head.type);				
			}
			else
			{
				type_list = new TypeList(t,type_list);
				SymbolTable.getInstance().enter(it.head.name,t);
			}
		}

		/*******************/
		/* [3] Semant Body */
		/*******************/
		body.semantMe();

		/*****************/
		/* [4] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/***************************************************/
		/* [5] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SymbolTable.getInstance().enter(name,new TypeFunction(baseType,name,type_list));

		/************************************************************/
		/* [6] Return value is irrelevant for function declarations */
		/************************************************************/
		return null;		
	}
}