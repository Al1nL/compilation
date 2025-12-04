package ast;

import symboltable.*;
import types.*;

public class AstDecFunc extends AstDec {
    private final String returnType;
    public final String name;
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
    TypeList type_list = null;

    /*******************/
    /* [0] Return type */
    /*******************/
    Type baseType = SymbolTable.getInstance().find(returnType);
    if (baseType == null)
    {
        System.out.format(">> ERROR [%d] non existing return type %s\n", lineNumber, returnType);
        // You should probably report() here
    }

    /*****************************************************/
    /* [1] Enter function name BEFORE opening its scope  */
    /*****************************************************/
    // Create function type first (with null params for now)
    TypeFunction t = new TypeFunction(baseType, name, null);
    SymbolTable.getInstance().enter(name, t);

    /****************************/
    /* [2] Begin Function Scope */
    /****************************/
    SymbolTable.getInstance().beginScope();

    /***************************/
    /* [3] Semant Input Params */
    /***************************/
    for (AstParamList it = params; it != null; it = it.tail)
    {
        Type paramType = SymbolTable.getInstance().find(it.head.type);
        if (paramType == null)
        {
            System.out.format(">> ERROR [%d] non existing type %s\n", lineNumber, it.head.type);
        }
        else
        {
            type_list = new TypeList(paramType, type_list);
            SymbolTable.getInstance().enter(it.head.name, paramType);
        }
    }

    // Update function type with actual parameters
    t.params = type_list;  // Assuming TypeFunction has a params field

    /*******************/
    /* [4] Semant Body */
    /*******************/
    body.semantMe(baseType);

    /*****************/
    /* [5] End Scope */
    /*****************/
    SymbolTable.getInstance().endScope();

    /********************************************************/
    /* [6] Function is already in symbol table, just return */
    /********************************************************/
    return t;
}
	public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}
}