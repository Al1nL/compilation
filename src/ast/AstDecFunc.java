package ast;

import ir.*;
import java.util.*;
import returncounter.ReturnCounter;
import symboltable.*;
import temp.*;
import types.*;
import variable.*;


public class AstDecFunc extends AstDec {

    public static String currentFunctionName = null;
    public final String returnType;
    public final String name;
    public String label;
    public final AstParamList params;
    private final AstStmtList body;
    public Integer offset;
    public int localVarCount;

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

    /**
     * Register the function signature (return type + param types)
     * into the symbol table WITHOUT analyzing the body.
     */
    public TypeFunction buildSignature() {
        if (name.equals("PrintInt") || name.equals("PrintString")) {
            System.out.format(">> ERROR [%d] cannot override built-in function %s\n", lineNumber, name);
            report();
        }

        Type baseType = SymbolTable.getInstance().find(returnType);
        if (baseType == null) {
            System.out.format(">> ERROR [%d] non existing return type %s\n", lineNumber, returnType);
            report();
        }

        TypeList type_list = null;
        TypeList cur_list = null;
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
                    type_list = new TypeList(paramType, null);
                    cur_list = type_list;
                } else {
                    cur_list.tail = new TypeList(paramType, null);
                    cur_list = cur_list.tail;
                }
            }
        }
        if (type_list != null) type_list.len = length;

        TypeFunction t = new TypeFunction(baseType, name, type_list);
        SymbolTable.getInstance().enter(name, t);
        return t;
    }

    /**
     * Analyze the method body (signature already registered).
     * Returns the TypeFunction (unchanged).
     */
    public TypeFunction analyzeBody() {
        currentFunctionName = name;
        Type baseType = SymbolTable.getInstance().find(returnType);

        TypeFunction t = (TypeFunction) SymbolTable.getInstance().find(name);

        SymbolTable.getInstance().beginScope();

        // Re-enter params into the new scope
        for (AstParamList it = params; it != null; it = it.tail) {
            Type paramType = SymbolTable.getInstance().find(it.head.type);
            if (paramType != null && paramType != TypeVoid.getInstance()) {
                if (SymbolTable.getInstance().findInScope(it.head.name) != null) {
                    System.out.format(">> ERROR [%d] duplicate parameter name %s\n", lineNumber, it.head.name);
                    report();
                }
                SymbolTable.getInstance().enter(it.head.name, paramType);
                it.head.var = Variable.get(it.head.name, SymbolTable.getInstance().currScopeLevel, name);
            }
        }

        ReturnCounter c = ReturnCounter.getInstance();
        if (returnType.equals("void")) {
            c.setCount(1);
        } else {
            c.setCount(0);
        }

        body.semantMe(baseType);
        c.setCount(0);

        SymbolTable.getInstance().endScope();
        return t;
    }

    public Type semantMe() {
        currentFunctionName = name;
        /* Forbid overriding built-ins */
        if (name.equals("PrintInt") || name.equals("PrintString")) {
            System.out.format(">> ERROR [%d] cannot override built-in function %s\n", lineNumber, name);
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
                it.head.var = Variable.get(it.head.name, SymbolTable.getInstance().currScopeLevel, name);
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

        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe() {
        String curClass = null;
        if(Ir.curClass != null){
            curClass = Ir.curClass;
            Ir.curClass = null;
        }
        // Track which class this method belongs to so AstExpCall can resolve sibling calls
        Ir.currentMethodClass = curClass;

        String name = this.label!=null? this.label : this.name;
        if (name.equals("main")) name = "user_main";
        else if (this.label == null) name = "_func_" + name;  // free functions: avoid MIPS reserved words
        Ir.getInstance().AddIrCommand(new IrCommandPrologue(name, localVarCount));
        if (params != null) params.irMe();
        AstStmtReturn.currentFunctionName = name;
        if (body != null) body.irMe();
        Ir.getInstance().AddIrCommand(new IrCommandEpilogue(name));
        Ir.curClass = curClass;
        Ir.currentMethodClass = null;  // clear after method body
        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
        if (curClass!=null){
            this.label = IrCommand.getFreshLabel(String.format("%s_%s", curClass, this.name));
            methodLabels.get(curClass).put(name, label);
            offset = classMethodOffsets.get(curClass).size();
            if(!classMethodOffsets.get(curClass).containsKey(name)){
                classMethodOffsets.get(curClass).put(name, offset);
            } else {
                offset = classMethodOffsets.get(curClass).get(name);
            }
        }
        int bodyIdx=0;
        int paramIdx=-1;
        offsets = new HashMap<Variable, Integer>();
        if (curClass != null) {
            paramIdx--;
        }
        if (params != null) {
            params.offsetMe(offsets, paramIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        if (body != null) {
            localVarCount = body.offsetMe(offsets, bodyIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        return 0;
    }

    public void debugOffset(){
        if (offset!=null){
            System.out.println(name + " - " + offset + ":");
        }
        if (params != null) {
            params.debugOffset();
        }
        if (body != null) {
            body.debugOffset();
        }
    }
}