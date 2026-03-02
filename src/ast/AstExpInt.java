package ast;

import ir.*;
import temp.*;
import types.*;

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
        return TypeInt.getInstance();
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        Temp t = TempFactory.getInstance().getFreshTemp();
        IrCommand curIrCommand = new IrCommandConstInt(t,value);
        if(Ir.curClass!=null){
            Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField).add(curIrCommand);
        }
        else{
            Ir.getInstance().AddIrCommand(curIrCommand); 
        }
        return t;
    }
}