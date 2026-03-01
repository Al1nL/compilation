package ast;
import ir.*;
import java.util.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.*;

public class AstParam extends AstNode {

    public final String type;
    public final String name;
    public Variable var;
    public Integer offset;

    public AstParam(String type, String name) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
    }

    public void printMe() {
        System.out.print("AST NODE PARAM\n");

        String label = "PARAM\n" + this.name;
        AstGraphviz.getInstance().logNode(serialNumber, label);
    }

    public Type semantMe() {

        Type paramType = null;

        if (type.equals("int")) {
            paramType = TypeInt.getInstance();

        } else if (type.equals("string")) {
            paramType = TypeString.getInstance();

        } else if (type.equals("void")) {
            System.out.print("ERROR: Parameter '" + this.name + "' cannot have type void");
            report();
        } else {
            // Must be a class or array type
            paramType = SymbolTable.getInstance().find(type);
            if (paramType == null) {
                System.out.print("ERROR: Type '" + this.type + "' is not defined");
                report();
            }
        }

        paramType.name = name; // This allows us to identify the param later

        return paramType;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe() {
        /*******************************/
        /* [1] Allocate a temporary for this parameter */
        /*******************************/
            this.var = Variable.get(name, var.scope, AstDecFunc.currentFunctionName); // unique key per function

        Temp paramTemp = TempFactory.getInstance().getFreshTemp();

        /******************************************/
        /* [2] Add IR command to declare the param */
        /******************************************/
        Ir.getInstance().AddIrCommand(
            new IrCommandParam(type, name, paramTemp)
        );

        /*******************/
        /* [3] Return the temp */
        /*******************/
        return paramTemp;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
		if (offsets!=null){
            offsets.put(var, curIdx);
            var.offset = curIdx;
            offset = curIdx;
        }
        return curIdx;
        
	}

    public void debugOffset(){
        if(offset!=null){
            System.out.println(var.name + "#" + var.scope + " - " + offset + ":");
        }
		
	}
}