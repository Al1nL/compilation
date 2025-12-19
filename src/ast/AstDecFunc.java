package ast;

import returncounter.ReturnCounter;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;

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

        // Log THIS node first
        String label = "FUNC DEC\n" + returnType + " " + name + "()";
        AstGraphviz.getInstance().logNode(serialNumber, label);

        // Log edges to children
        if (params != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, params.serialNumber);
        }
        if (body != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
        }

        // Then print children
        if (params != null) {
            params.printMe();
        }
        if (body != null) {
            body.printMe();
        }
    }

    public Type semantMe() {
        /* Forbid overriding built-ins */
        if (name.equals("PrintInt") || name.equals("PrintString")) {
            System.out.format(">> ERROR [%d] cannot override built-in function %s\n",
                    lineNumber,
                    name
            );
            report();
        }
        TypeList type_list = null;
        TypeList cur_list = type_list;

        /* Return type */
        Type baseType = SymbolTable.getInstance().find(returnType);
        if (baseType == null) {
            System.out.format(">> ERROR [%d] non existing return type %s\n", lineNumber, returnType);
            report();
        }

        // Create function type first (with null params for now)
        TypeFunction t = new TypeFunction(baseType, name, null);

        SymbolTable.getInstance().enter(name, t);

        /* Begin Function Scope */
        SymbolTable.getInstance().beginScope();

        /* Semant Input Params */
        int length = 0;
        for (AstParamList it = params; it != null; it = it.tail) {
            Type paramType = SymbolTable.getInstance().find(it.head.type);
            if (paramType == null) {
                System.out.format(">> ERROR [%d] non existing type %s\n", lineNumber, it.head.type);
                report();
            } else if (paramType == TypeVoid.getInstance()) {
                System.out.format(">> ERROR [%d] parameter cannot be of type void %s\n", lineNumber, it.head.type);
                report();
            } else {
                length++;
                if (cur_list == null) {
                    type_list = new TypeList(paramType, type_list);
                    cur_list = type_list;
                } else {
                    cur_list.tail = new TypeList(paramType, null);
                    cur_list = cur_list.tail;
                }
                if (SymbolTable.getInstance().findInScope(it.head.name) != null) {
                    System.out.format(">> ERROR [%d] duplicate parameter name %s\n", lineNumber, it.head.name);
                    report();
                }

                SymbolTable.getInstance().enter(it.head.name, paramType);
            }
        }
        if (type_list != null) {
            type_list.len = length;
        }

        // Update function type with actual parameters
        t.params = type_list;  // Assuming TypeFunction has a params field

        /* Semant Body */
        ReturnCounter c = ReturnCounter.getInstance();
        if (returnType.equals("void")) {
            c.setCount(1);

        } else {
            c.setCount(0);
        }

        body.semantMe(baseType);
        c.setCount(0);

        /* End Scope */
        SymbolTable.getInstance().endScope();

        /* Function is already in symbol table, just return */
        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        Ir.
                getInstance().
                AddIrCommand(new IrCommandLabel("main"));
        if (body != null) body.irMe();

        return null;
    }
}