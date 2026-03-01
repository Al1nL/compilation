package ast;

import java.util.*;
import symboltable.*;
import types.*;
import temp.*;
import ir.*;
import variable.*;

public class AstDecClass extends AstDec {
    
    public final String name;         // class name
    public final String parentName;   // null if no EXTENDS
    public final AstDecList fields;   // linked list of cFields

    private Map<String,Integer> _MethodOffsets; // set during offsetMe
    private int _FieldCount;

    public AstDecClass(String name, String parentName, AstDecList fields) {
        serialNumber = AstNodeSerialNumber.getFresh();

        this.name = name;
        this.parentName = parentName;
        this.fields = fields;
    }

    @Override
    public void printMe() {
        System.out.format("CLASS DEC = %s\n", this.name);

        if (parentName != null) {
            System.out.print("===== classDec -> CLASS ID EXTENDS ID LBRACE cField { cField } RBRACE\n");
        } else {
            System.out.print("===== classDec -> CLASS ID LBRACE cField { cField } RBRACE\n");
        }

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
                System.err.println("Class " + name + " parent not exists " + parentName);
                report();
            }
            parentTypeClass = (TypeClass) parentType;
        }

        // Create empty class type first
        TypeClass t = new TypeClass(parentTypeClass, this.name, null, false);

        // ENTER class name
        SymbolTable.getInstance().enter(this.name, t);

        // BEGIN SCOPE
        SymbolTable.getInstance().beginScope();

        // PROCESS FIELDS
        this.processFields(parentTypeClass , t);

        System.err.println("====== CLASS " + this.name + " END ======\n");
        t.isinitilized = true;
        /* End Scope */
        SymbolTable.getInstance().endScope();

        return t;
    }

    public Type semantMe(Type expectedReturnType) {
        return semantMe();
    }

    public void processFields(TypeClass parent, TypeClass curr) {
        TypeClassVarDecList result = null;
        TypeClassVarDecList last = null;
        HashSet<String> addedNames = new HashSet<>();
    
        // FIRST LOOP: register all names, resolve vars immediately
        for (AstDecList it = fields; it != null; it = it.tail) {
            AstDec dec = it.head;
            String name = null;
            Type t = null;
    
            if (dec instanceof AstDecVar) {
                AstDecVar varDec = (AstDecVar) dec;
                name = varDec.name;
                t = varDec.semantMe(); // safe to compute immediately
            } else if (dec instanceof AstDecFunc) {
                AstDecFunc funcDec = (AstDecFunc) dec;
                name = funcDec.name;
                t = (TypeFunction) funcDec.semantMe(); // placeholder
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
                    }
                   
                    if (!(same instanceof TypeFunction) || !((TypeFunction) same).compareFunctions((TypeFunction)t)) {
                        dec.report();
                    }
                    
                }
            }

            if (addedNames.contains(name)) {
                System.out.format(">> ERROR class cannot define multiple fields with the same name %s in the same class %d\n",
                        name, lineNumber);
                dec.report();
                continue;
            }
    
            addedNames.add(name);
    
            TypeClassVarDec decv = new TypeClassVarDec(t, name);
            if (result == null) {
                result = new TypeClassVarDecList(decv, null);
                last = result;
            } else {
                last.tail = new TypeClassVarDecList(decv, null);
                last = last.tail;
            }
            curr.dataMembers = result;
        }
    
    }
    

    public Temp irMe(){
        /**************************************/
        /* [1] Begin class IR generation      */
        /**************************************/
        Ir.
            getInstance().
            AddIrCommand(new IrCommandDeclareClass(
                name,
                parentName,
                fields,
                _MethodOffsets != null ? _MethodOffsets : new HashMap<>(),
                _FieldCount
            ));

        return null;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
		curClass = name;
        Map<String, Integer> newFields;
        Map<String, Integer> newMethods;
        if(parentName!=null){
            newFields = new HashMap<String, Integer>(classFieldOffsets.get(parentName));
            newMethods = new HashMap<String, Integer>(classMethodOffsets.get(parentName));
        }
        else{
            newFields = new HashMap<String, Integer>();
            newMethods = new HashMap<String, Integer>();
        }
        classFieldOffsets.put(curClass, newFields);
        classMethodOffsets.put(curClass, newMethods);
               
        if(fields!=null){
            fields.offsetMe(offsets, classFieldOffsets.get(curClass).size(), curClass, classFieldOffsets, classMethodOffsets, methodLabels);

        }
        
        _MethodOffsets = new HashMap<>(classMethodOffsets.get(curClass));
        _FieldCount = classFieldOffsets.get(curClass).size();
        
        return 0;
        
        
	}
    public void debugOffset(){
		fields.debugOffset();
	}
    
}