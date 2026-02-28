package ast;

import ir.*;
import java.util.*;
import temp.*;
import types.*;
import variable.*;

public class AstVarField extends AstVar {

    public AstVar variable;
    public String fieldName;
    public String className;
    public Integer fieldOffset;

    /* CONSTRUCTOR(S) */
    public AstVarField(AstVar var, String fieldName) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();

        this.variable = var;
        this.fieldName = fieldName;
    }

    /* The printing message for a field var AST node */
    public void printMe() {
        /* AST NODE TYPE = AST FIELD VAR */
        System.out.print("AST NODE FIELD VAR\n");

        /* RECURSIVELY PRINT VAR, then FIELD NAME ... */
        if (variable != null) {
            variable.printMe();
        }
        System.out.format("FIELD NAME( %s )\n", this.fieldName);

        /* PRINT Node to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("FIELD\nVAR\n...->%s", this.fieldName));

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        if (variable != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, variable.serialNumber);
        }
    }

    public Type semantMe() {
        Type t = null;
        TypeClass tc = null;

        /* Recursively semant var */
        if (variable != null) {
            t = variable.semantMe();
            this.var = variable.var;
        }

        /* Make sure type is a class */
        if (t.isClass() == false) {
            System.out.format(">> ERROR [%d] access %s field of a non-class variable\n", lineNumber + 1, fieldName);
            report();
        }

        tc = (TypeClass) t;
        /* Look for fiedlName inside tc or fathers */
        Type found = tc.findField(this.fieldName);

        if (found == null) {
            System.out.format(">> ERROR [%d] field %s does not exist in class\n", lineNumber, fieldName);
            report();
        }
        className = tc.name;

        return found;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        Temp base = variable.irMe();  // object address
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(
            new IrCommandLoadField(t, base, this.fieldName, fieldOffset)
        );
        
        t.dependencySet.addAll(base.dependencySet);
        return t;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets){
        
        fieldOffset = classFieldOffsets.get(className).get(fieldName);
        int res = variable.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets);
        return res;
		
	}

    public void debugOffset(){
        variable.debugOffset();
        if(fieldOffset!=null){
            System.out.println(fieldName + "#" + className + " - " + fieldOffset + ":");
        }
        else{
            System.out.println("Tried Class field and failed!!!!");
        }
		
	}
}