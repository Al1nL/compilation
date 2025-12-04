package ast;

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
        System.err.println("topIndex BEFORE enter: " + SymbolTable.getInstance().topIndex);

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
        System.err.println("topIndex AFTER enter class name: " + SymbolTable.getInstance().topIndex);

        // BEGIN SCOPE
        SymbolTable.getInstance().beginScope();
        System.err.println("topIndex AFTER beginScope: " + SymbolTable.getInstance().topIndex);

        // PROCESS FIELDS
        System.err.println(">>> Processing fields for " + this.name);

        t.dataMembers = this.processFields();
        // TypeList fieldTypes = fields.semantMe();
        System.err.println("topIndex AFTER processing fields: " + SymbolTable.getInstance().topIndex);

        System.err.println("====== CLASS " + this.name + " END ======\n");

        // DO NOT endScope()
         /*****************/
        /* [5] End Scope */
        /*****************/
        SymbolTable.getInstance().endScope();
        
        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public TypeClassVarDecList processFields(){
        TypeClassVarDecList result = null;
		TypeClassVarDecList last = null;

        Type t;
        String name;
		for (AstDecList it = fields; it != null; it = it.tail)
		{
            AstDec dec = it.head;

            if(dec instanceof AstDecVar){
                t = ((AstDecVar) dec).semantMe();
                name = ((AstDecVar) dec).name;

            }else if(dec instanceof AstDecFunc){
                t = ((AstDecFunc) dec).semantMe();
                name = ((AstDecFunc) dec).name;
            }else
                continue;
            
			// Convert Type → TypeClassVarDec
			TypeClassVarDec decv = new TypeClassVarDec(t, name);

			// First element
			if (result == null)
			{
				result = new TypeClassVarDecList(decv, null);
				last = result;
			}
			else
			{
				last.tail = new TypeClassVarDecList(decv, null);
				last = last.tail;
			}
		}

        return result;
    }
}