package ast;

import ir.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.Variable;
public class AstDecVar extends AstDec {

    public String type;
    public String name;
    public AstExp exp;
    public Variable var;
    public AstDecVar(String type, String name, AstExp exp) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;
        this.exp = exp;
    }

    public void printMe() {
        System.out.print("AST NODE VAR DEC\n");

        String label = "VAR DEC\n" + this.type + " " + this.name;
        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (exp != null) {
            exp.printMe();
        }

        if (exp != null) {
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
        }

    }

    public Type semantMe() {
        Type varType = null;
        Type varname = null;
        // Check for dups
        SymbolTableEntry e = SymbolTable.getInstance().findInScope(name);
        varname = e == null ? null : e.type;
        if (varname != null) {
            System.err.println("ERROR: Variable '" + this.name + "' is already defined in this scope");
            report();
        }
        // Check for primitive types
        if (type.equals("int")) {
            varType = TypeInt.getInstance();

        } else if (type.equals("string")) {
            varType = TypeString.getInstance();

        } else if (type.equals("void")) {
            // Variables cannot be void
            System.err.println("ERROR: Variable '" + this.name + "' cannot have type void");
            report();
        } else {
            // Must be a class or array type
            varType = SymbolTable.getInstance().find(type);
            if (varType == null) {
                System.err.println("ERROR: Type '" + this.type + "' is not defined");
                report();
            }
        }

        if (exp != null) {
            Type expType = exp.semantMe(null);
            if (expType == null) {
                            System.err.println("ERROR: the expression you want to assign to "+name+" is not defined");
                            report();
                        }
            if (!expType.canAssignTo(varType)) {
                System.err.println("ERROR: Cannot assign expType " + expType.name + " to " + varType.name);
                report();
            }
        }
        
    
        // Enter variable to symbol table   
    
        SymbolTable.getInstance().enter(name, varType);
        var = Variable.get(name, SymbolTable.getInstance().currScopeLevel);
        return varType;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public Temp irMe() {
        analysis.Dbg.p("AstDecVar.irMe name=" + var.name);
        Ir.getInstance().AddIrCommand(new IrCommandAllocate(var));

        if (exp != null) {
            Ir.getInstance().AddIrCommand(new IrCommandStore(var, exp.irMe()));
        }
        return null;
    }
}
