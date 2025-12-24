package ast;

import ir.*;
import java.util.HashSet;
import temp.*;
import types.*;
public class AstExpBinop extends AstExp {

    int op;
    public AstExp left;
    public AstExp right;

    /* CONSTRUCTOR(S) */
    public AstExpBinop(AstExp left, AstExp right, int op) {
        /* SET A UNIQUE SERIAL NUMBER */
        serialNumber = AstNodeSerialNumber.getFresh();

        /* COPY INPUT DATA MEMBERS ... */
        this.left = left;
        this.right = right;
        this.op = op;
    }

    /* The printing message for a binop exp AST node */
    public void printMe() {
        /* PRINT CORRESPONDING DERIVATION RULE */
        System.out.print("====================== exp -> exp BINOP exp\n");

        String sop = "";

        /* CONVERT op to a printable sop */
        if (op == 0) {
            sop = "+";
        }
        if (op == 1) {
            sop = "-";
        }
        if (op == 2) {
            sop = "*";
        }
        if (op == 3) {
            sop = "/";
        }
        if (op == 4) {
            sop = "<";
        }
        if (op == 5) {
            sop = ">";
        }
        if (op == 6) {
            sop = "=";
        }

        /* AST NODE TYPE = AST BINOP EXP */
        System.out.print("AST NODE BINOP EXP\n");

        /* RECURSIVELY PRINT left + right ... */
        if (left != null) {
            left.printMe();
        }
        if (right != null) {
            right.printMe();
        }

        /* PRINT Node to AST GRAPHVIZ DOT file */
        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("BINOP(%s)", sop));

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        if (left != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, left.serialNumber);
        }
        if (right != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, right.serialNumber);
        }
    }

    public Type semantMe() {
        Type t1 = null;
        Type t2 = null;

        if (left != null) {
            t1 = left.semantMe();
        }
        if (right != null) {
            t2 = right.semantMe();
        }
        if (t1 == null || t2 == null) {
            System.out.format(">> ERROR: binop expression either rigth or left are null\n");
            report();
        }
        if (op == 6) {
            if (t1.canAssignTo(t2) || t2.canAssignTo(t1)) {
                return TypeInt.getInstance();
            } else {
                System.out.format(">> ERROR: binop expression %s and %s are not comparable\n", t1.toString(), t2.toString());
                report();
                return null;
            }
        }
        if (op == 0) {
            if (t1.isSameType(TypeInt.getInstance()) && t2.isSameType(TypeInt.getInstance())) {
                return TypeInt.getInstance();
            }
            if (t1.isSameType(TypeString.getInstance()) && t2.isSameType(TypeString.getInstance())) {
                return TypeString.getInstance();
            } else {
                System.out.format(">> ERROR: binop expression cannot add %s and %s\n", t1.toString(), t2.toString());
                report();
                return null;
            }
        }

        if ((t1 == TypeInt.getInstance()) && (t2 == TypeInt.getInstance())) {
            if (op == 3 && right instanceof AstExpInt) {
                AstExpInt v = (AstExpInt) right;
                if (v.value == 0) {
                    System.out.format(">> ERROR: Deviding by zero\n");
                    report();
                    return null;
                }
            }
            return TypeInt.getInstance();
        }
        System.out.format(">> ERROR: math binop expression not on ints\n");
        report();
        return null;

    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe()
    {
        Temp t1 = null;
        Temp t2 = null;
        Temp dst = TempFactory.getInstance().getFreshTemp();

        if (left  != null) t1 = left.irMe();
        if (right != null) t2 = right.irMe();
        
        dst.dependencySet = new HashSet<>();
dst.dependencySet.addAll(t1.dependencySet);
dst.dependencySet.addAll(t2.dependencySet);
        if (op == 0)
        {
            if(right instanceof AstExpInt && left instanceof AstExpInt){
                Ir.
                        getInstance().
                        AddIrCommand(new IrCommandBinopAddIntegers(dst,t1,t2));  
            }else{
                Ir.
                        getInstance().
                        AddIrCommand(new IrCommandBinopAddStrings(dst,t1,t2));
            }
            
        }
        if (op == 2)
        {
            Ir.
                    getInstance().
                    AddIrCommand(new IrCommandBinopMulIntegers(dst,t1,t2));
        }
        if (op == 3)
        {
            Ir.
                    getInstance().
                    AddIrCommand(new IrCommandBinopEqIntegers(dst,t1,t2));
        }
        if (op == 4)
        {
            Ir.
                    getInstance().
                    AddIrCommand(new IrCommandBinopLtIntegers(dst,t1,t2));
        }
        return dst;
    }

}