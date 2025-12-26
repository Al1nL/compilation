package ast;

import ir.*;
import java.util.HashSet;
import temp.*;
import types.*;
import symboltable.*;
// import variable.Variable;

public class AstExpInt extends AstExp {

    public int value;

    /* CONSTRUCTOR(S) */
    public AstExpInt(int value) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();

        /* COPY INPUT DATA MEMBERS ... */
        this.value = value;
    }

    /* The printing message for an int exp AST node */
    public void printMe() {
        /* PRINT CORRESPONDING DERIVATION RULE */
        System.out.format("====================== exp -> INT( %d )\n", value);

        /* AST NODE TYPE = AST INT EXP */
        System.out.format("AST NODE INT( %d )\n", value);

        /* Print to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(serialNumber, String.format("INT(%d)", value));
    }

    public Type semantMe() {
        // var = Variable.get("TypeInt" + Integer.toString(value), SymbolTable.getInstance().currScopeLevel);
        return TypeInt.getInstance();
    }

    public Type semantMe(Type expectedReturnType) {
        return TypeInt.getInstance();
    }

    public Temp irMe()
    {
        Temp t = TempFactory.getInstance().getFreshTemp(); 
        Ir.getInstance().AddIrCommand(new IRcommandConstInt(t,value));
        return t;
    }
}