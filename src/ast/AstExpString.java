package ast;

import ir.*;
import temp.*;
import types.*;


public class AstExpString extends AstExp {

    public final String value;

    public AstExpString(String value) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.value = value;
    }

    @Override
    public void printMe() {
        System.out.println("STRING: " + value);

        // Escape for Graphviz
        String clean = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("String(%s)", clean)
        );
    }

    public Type semantMe() {
        return TypeString.getInstance();
    }

    public Type semantMe(Type expectedReturnType) {
        return TypeString.getInstance();
    }

    public Temp irMe()
    {
        Temp t = TempFactory.getInstance().getFreshTemp();
        IrCommand curIrCommand = new IrCommandConstString(t,value);
        if(Ir.curClass!=null){
            Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField).add(curIrCommand);
        }
        else{
            Ir.getInstance().AddIrCommand(curIrCommand); 
        }
        return t;
    }
}