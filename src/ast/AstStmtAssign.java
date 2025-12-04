package ast;

import types.*;

public class AstStmtAssign extends AstStmt {

    /**
     * ************
     */
    /*  var := exp */
    /**
     * ************
     */
    public AstVar var;
    public AstExp exp;

    /**
     * ****************
     */
    /*  CONSTRUCTOR(S) */
    /**
     * ****************
     */
    public AstStmtAssign(AstVar var, AstExp exp) {
        /**
         * ***************************
         */
        /* SET A UNIQUE SERIAL NUMBER */
        /**
         * ***************************
         */
        serialNumber = AstNodeSerialNumber.getFresh();

        /**
         * ************************************
         */
        /* PRINT CORRESPONDING DERIVATION RULE */
        /**
         * ************************************
         */
        System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

        /**
         * ****************************
         */
        /* COPY INPUT DATA MEMBERS ... */
        /**
         * ****************************
         */
        this.var = var;
        this.exp = exp;
    }

    /**
     * ******************************************************
     */
    /* The printing message for an assign statement AST node */
    /**
     * ******************************************************
     */
    public void printMe() {
        /**
         * *****************************************
         */
        /* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
        /**
         * *****************************************
         */
        System.out.print("AST NODE ASSIGN STMT\n");

        /**
         * ********************************
         */
        /* RECURSIVELY PRINT VAR + EXP ... */
        /**
         * ********************************
         */
        if (var != null) {
            var.printMe();
        }
        if (exp != null) {
            exp.printMe();
        }

        /**
         * ************************************
         */
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /**
         * ************************************
         */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "ASSIGN\nleft := right\n");

        /**
         * *************************************
         */
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /**
         * *************************************
         */
        AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    public Type semantMe() {
        System.out.println("############## ################");
        Type varType = var.semantMe();
        Type expType = exp.semantMe();
        
        if(varType==null){
            System.out.println("1NULLLLLLLLLLLLLLLLLLLLLLLLLLL");
        }
        if(expType==null){
            System.out.println("2NULLLLLLLLLLLLLLLLLLLLLLLLLLL");
        }

        if (!expType.canAssignTo(varType)) {
            System.err.println("ERROR: Cannot assign " + expType.name + " to " + varType + " "+ lineNumber);
			report();
        }
        return null;
    }
    public Type semantMe(Type expectedReturnType)
	{
		return semantMe();
	}

}
