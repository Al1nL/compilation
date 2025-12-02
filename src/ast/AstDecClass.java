package ast;
import types.*;
import symboltable.*;


public class AstDecClass extends AstDec
{
    /****************/
    /* DATA MEMBERS */
    /****************/
    public final String name;         // class name
    public final String parentName;   // null if no EXTENDS
    public final AstDecList fields;   // linked list of cFields

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AstDecClass(String name, String parentName, AstDecList fields)
    {
        serialNumber = AstNodeSerialNumber.getFresh();
		
        if (parentName != null)
            System.out.print("===== classDec -> CLASS ID EXTENDS ID LBRACE cField { cField } RBRACE\n");
        else
            System.out.print("===== classDec -> CLASS ID LBRACE cField { cField } RBRACE\n");

        this.name = name;
        this.parentName = parentName;
        this.fields = fields;
    }
    @Override
    public void printMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("CLASS DEC = %s\n",this.name);
		if (fields != null) fields.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
                serialNumber,
			String.format("CLASS\n%s",this.name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
	}

    
	public Type semantMe()
	{	
		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SymbolTable.getInstance().beginScope();
		TypeClass parentTypeClass = null;
		//todo: check this after
		if (parentName != null) {
			Type parentType = SymbolTable.getInstance().find(parentName);
			if (parentType == null || !(parentType instanceof TypeClass)) {
				report(); // superclass does not exist or is not a class
			}
			parentTypeClass = (TypeClass) parentType;
		}

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		TypeClass t = new TypeClass(parentTypeClass ,this.name, fields.semantMe());

		/*****************/
		/* [3] End Scope */
		/*****************/
		SymbolTable.getInstance().endScope();

		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SymbolTable.getInstance().enter(this.name,t);

		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}
	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
}