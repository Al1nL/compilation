package ast;
import java.util.HashSet;
import symboltable.*;
import types.*;

public class AstDecClass extends AstDec {

    public final String name;         // class name
    public final String parentName;   // null if no EXTENDS
    public final AstDecList fields;   // linked list of cFields

    public AstDecClass(String name, String parentName, AstDecList fields) {
        serialNumber = AstNodeSerialNumber.getFresh();

        if (parentName != null) {
            System.out.print("===== classDec -> CLASS ID EXTENDS ID LBRACE cField { cField } RBRACE\n");
        } else {
            System.out.print("===== classDec -> CLASS ID LBRACE cField { cField } RBRACE\n");
        }

        this.name = name;
        this.parentName = parentName;
        this.fields = fields;
    }

    @Override
    public void printMe() {
        System.out.format("CLASS DEC = %s\n", this.name);

        if (fields != null) {
            fields.printMe();
        }

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("CLASS\n%s", this.name));

        AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }

    public Type semantMe() {

        System.err.println("\n====== CLASS " + this.name + " START ======");

        // Check duplicate class
        if (SymbolTable.getInstance().find(this.name) != null) {
            report();
        }

        // Check parent
        TypeClass parentTypeClass = null;
        if (parentName != null) {
            Type parentType = SymbolTable.getInstance().find(parentName);
            if (parentType == null || !(parentType instanceof TypeClass)) {
                report();
            }
            parentTypeClass = (TypeClass) parentType;
        }

        // Create empty class type first
        TypeClass t = new TypeClass(parentTypeClass, this.name, null);

        // ENTER class name
        SymbolTable.getInstance().enter(this.name, t);

        // BEGIN SCOPE
        SymbolTable.getInstance().beginScope();

        // PROCESS FIELDS
        t.dataMembers = this.processFields(parentTypeClass);
        // TypeList fieldTypes = fields.semantMe();

        System.err.println("====== CLASS " + this.name + " END ======\n");

        /* End Scope */
        SymbolTable.getInstance().endScope();

        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public TypeClassVarDecList processFields(TypeClass parent) {
        TypeClassVarDecList result = null;
        TypeClassVarDecList last = null;
        HashSet<String> addedNames = new HashSet<>();

        Type t;
        String name;
        for (AstDecList it = fields; it != null; it = it.tail) {
            AstDec dec = it.head;

            if (dec instanceof AstDecVar) {
                System.err.format("var:%s\n", ((AstDecVar) dec).name);
                t = ((AstDecVar) dec).semantMe();
                name = ((AstDecVar) dec).name;

            } else if (dec instanceof AstDecFunc) {
                System.err.format("method:%s\n", ((AstDecFunc) dec).name);
                t = ((AstDecFunc) dec).semantMe();
                name = ((AstDecFunc) dec).name;
            } else {
                continue;
            }

            if (parent != null) {
                Type same = parent.findField(name);

                if (same != null) {

                    if (!same.isSameType(t)) {
                        dec.report();
                    }

                    if (dec instanceof AstDecVar) {
                        System.out.format(">> ERROR class cannot define a field %s with the same name as an existing field in superclass %d\n", name, lineNumber);
                        dec.report();
                    } else {
                        TypeFunction func = (TypeFunction) same;
                        boolean ok = func.compareFunctions((TypeFunction) t);
                        if (!ok) {
                            dec.report();
                        }
                    }
                }
            }

            if (addedNames.contains(name)) {
                System.out.format(
                        ">> ERROR class cannot define multiple fields with the same name %s in the same class %d\n",
                        name, lineNumber);
                dec.report();
            } else {
                addedNames.add(name); // Add this name to the set
            }

            // Convert Type → TypeClassVarDec
            TypeClassVarDec decv = new TypeClassVarDec(t, name);

            // First element
            if (result == null) {
                result = new TypeClassVarDecList(decv, null);
                last = result;
            } else {
                last.tail = new TypeClassVarDecList(decv, null);
                last = last.tail;
            }
        }

        return result;
    }
}