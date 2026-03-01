package ast;

import ir.*;
import java.util.*;
import temp.*;
import types.*;
import variable.*;

public class AstVarSubscript extends AstVar {

    public AstVar variable;
    public AstExp subscript;
    public Integer offset;

    /****************/
    /* CONSTRUCTOR(S) */
    /****************/
    public AstVarSubscript(AstVar var, AstExp subscript) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        serialNumber = AstNodeSerialNumber.getFresh();

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.variable = var;
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
        /*****************************************************/
        if (this.variable != null) {
            variable.printMe();
        }
        if (this.subscript != null) {
            subscript.printMe();
        }

        /*****************************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /*****************************************************/
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "SUBSCRIPT\nVAR\n...[...]");

        /*****************************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /*****************************************************/
        if (this.variable != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, variable.serialNumber);
        }
        if (this.subscript != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
        }
    }

    public Type semantMe() {
        Type ret = variable.semantMe();
        if (ret == null) {
            System.out.format(">> ERROR [%d] non existing type\n", lineNumber);
            report();
        }
        if (!ret.isArray()) {
            System.out.format(">> ERROR [%d] cannot subscript non array var\n", lineNumber);
            report();
        }
        Type t = subscript.semantMe();
        if (!t.isSameType(TypeInt.getInstance())) {
            System.out.format(">> ERROR [%d] indexing an array with non int argument\n", lineNumber);
            report();
        }
        if (subscript instanceof AstExpInt) {
            AstExpInt v = (AstExpInt) subscript;
            if (v.value < 0) {
                System.out.format(">> ERROR: indexing an array with a negativ index\n");
                report();
            }
        }
        TypeArray convertedRet = (TypeArray) ret;
        this.var = variable.var;
        return convertedRet.baseType;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

public Temp irMe() {
    Temp arr = variable.irMe();
    Temp idx = subscript.irMe();

    Temp addr = TempFactory.getInstance().getFreshTemp();
    Ir.getInstance().AddIrCommand(new IrCommandAddOffset(addr, arr, idx));

    Temp dst = TempFactory.getInstance().getFreshTemp();
    Ir.getInstance().AddIrCommand(new IrCommandLoadMemory(dst, addr, variable.var));

    if (arr.dependencySet != null) dst.dependencySet.addAll(arr.dependencySet);
    if (idx.dependencySet != null) dst.dependencySet.addAll(idx.dependencySet);

    analysis.Dbg.p("AstVarSubscript.irMe name=" + variable.var.name + " dependencies=" + dst.dependencySet);
    return dst;
}
    public Temp irMeAsAddress()
    {
        Temp arr = variable.irMe();
        Temp idx = subscript.irMe();

        Temp addr = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandAddOffset(addr, arr, idx));

        return addr;
    }    
    
    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels) {

        curIdx = variable.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        if (variable.var != null) {
            offset = variable.var.offset;
        }
        return subscript.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
    }

    public void debugOffset() {
        variable.debugOffset();
        subscript.debugOffset();
    }
}