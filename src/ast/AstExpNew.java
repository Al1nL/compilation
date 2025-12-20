package ast;

import symboltable.*;
import types.*;
import temp.*;
import ir.*;

public class AstExpNew extends AstExp {

    public final String type;
    public final AstExp sizeExp; // may be null

    public AstExpNew(String type, AstExp sizeExp) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.sizeExp = sizeExp;
    }

    @Override
    public void printMe() {
        System.out.print("NEW ");
        AstGraphviz.getInstance().logNode(serialNumber, String.format("NEW"));
        if (type != null) {
            type.toString();
        }
        if (sizeExp != null) {
            System.out.print("{ ");
            sizeExp.printMe();
            System.out.println(" }");
        }
        if (sizeExp != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, sizeExp.serialNumber);
        }
    }

    public Type semantMe() {
        Type ret = SymbolTable.getInstance().find(type);
        if (ret == null) {
            System.out.format(">> ERROR [%d] non existing type %s\n", lineNumber, type);
            report();
        }
        if (sizeExp == null) {
            return ret;
        }
        Type t = sizeExp.semantMe();
        if (!t.isSameType(TypeInt.getInstance())) {
            System.out.format(">> ERROR: initializing array with non int argument\n");
            report();
        }
        if (sizeExp instanceof AstExpInt) {
            AstExpInt v = (AstExpInt) sizeExp;
            if (v.value <= 0) {
                System.out.format(">> ERROR: initializing array of non positive length\n");
                report();
            }

        }
        return new TypeArray(ret);
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        /******************************/
        /* [1] Allocate fresh temp   */
        /******************************/
        Temp dst = TempFactory.getInstance().getFreshTemp();
    
        /*********************************************/
        /* [2] Case 1: new TYPE (object allocation) */
        /*********************************************/
        if (sizeExp == null)
        {
            Ir.
                getInstance().
                AddIrCommand(new IrCommandAllocateObject(dst, type));
    
            /*******************/
            /* [3] return dst */
            /*******************/
            return dst;
        }
    
        /********************************************/
        /* [4] Case 2: new TYPE[size] (array alloc) */
        /********************************************/
        Temp sizeTemp = sizeExp.irMe();
    
        Ir.
            getInstance().
            AddIrCommand(new IrCommandAllocateArray(dst, type, sizeTemp));
    
        /*******************/
        /* [5] return dst */
        /*******************/
        return dst;
    }
    
}