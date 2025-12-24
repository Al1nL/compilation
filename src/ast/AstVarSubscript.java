package ast;

import ir.*;
import java.util.HashSet;
import temp.*;
import types.*;

public class AstVarSubscript extends AstVar {

    public AstVar var;
    public AstExp subscript;

    /**
     * ***************
     */
    /* CONSTRUCTOR(S) */
    /**
     * ***************
     */
    public AstVarSubscript(AstVar var, AstExp subscript) {
        /**
         * ***************************
         */
        /* SET A UNIQUE SERIAL NUMBER */
        /**
         * ***************************
         */
        serialNumber = AstNodeSerialNumber.getFresh();

        /**
         * ****************************
         */
        /* COPY INPUT DATA MEMBERS ... */
        /**
         * ****************************
         */
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
        /*****************************************************/
        if (this.var != null) {
            var.printMe();
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
        if (this.var != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        }
        if (this.subscript != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
        }
    }

    public Type semantMe() {
        Type ret = var.semantMe();
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
        return convertedRet.baseType;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

	public Temp irMe()
	{
		/******************************/
        /* [1] Evaluate array variable */
		/******************************/
        Temp arr = var.irMe();

		/******************************/
        /* [2] Evaluate index          */
		/******************************/
        Temp idx = subscript.irMe();

		/******************************/
        /* [3] Null check             */
		/******************************/
        Ir.getInstance().AddIrCommand(
                new IrCommandJumpIfEqToZero(arr, "_null_pointer_error")
        );

		/******************************/
        /* [4] Compute element address */
		/******************************/
        Temp addr = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(
                new IrCommandAdd(addr, arr, idx)
        );

		/******************************/
        /* [5] Load value from var   */
		/******************************/
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(
                new IrCommandAdd(dst, addr, null)
        );

        dst.dependencySet = new HashSet<>();
        dst.dependencySet.addAll(arr.dependencySet);
        dst.dependencySet.addAll(idx.dependencySet);

        /*****************************************************/
        /* [6] Return value            */
        /*****************************************************/
        return dst;
    }
}
