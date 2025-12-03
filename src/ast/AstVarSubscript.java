package ast;
import types.*;

public class AstVarSubscript extends AstVar
{
	public AstVar var;
	public AstExp subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AstVarSubscript(AstVar var, AstExp subscript)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		serialNumber = AstNodeSerialNumber.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA MEMBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void printMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSCRIPT ... */
		/****************************************/
		if (this.var != null) var.printMe();
		if (this.subscript != null) subscript.printMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AstGraphviz.getInstance().logNode(
				serialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (this.var       != null) AstGraphviz.getInstance().logEdge(serialNumber,var.serialNumber);
		if (this.subscript != null) AstGraphviz.getInstance().logEdge(serialNumber,subscript.serialNumber);
	}
	public Type semantMe()
	{
		Type ret = var.semantMe();
		if(ret==null){
			System.out.format(">> ERROR [%d] non existing type\n",lineNumber);
            report();
		}
		Type t = subscript.semantMe();
            if (!t.isSameType(TypeInt.getInstance())){
                System.out.format(">> ERROR: indexing an array with non int argument\n");
                report();
            }
		if(subscript instanceof AstExpInt){
                AstExpInt v = (AstExpInt) subscript;
                if(v.value<0){
                    System.out.format(">> ERROR: indexing an array with a negativ index\n");
                    report();
                }
                
            }
		TypeArray convertedRet = (TypeArray) ret;
            return convertedRet.baseType;

	}

	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}


}
