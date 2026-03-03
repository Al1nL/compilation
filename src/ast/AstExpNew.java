package ast;

import ir.*;
import java.util.*;
import symboltable.*;
import temp.*;
import types.*;
import variable.*;

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
        IrCommand curIrCommand;
    
        /*********************************************/
        /* [2] Case 1: new TYPE (object allocation) */
        /*********************************************/
        if (sizeExp == null)
        {
            curIrCommand = new IrCommandAllocateObject(dst, type);
            IrCommandAllocateObject alocObjCommand = (IrCommandAllocateObject) curIrCommand;

            // Save outer class/field context
            String savedClass = Ir.curClass;
            int savedField = Ir.curField;

            // Re-run field irMe() with fresh temps for this specific new-expression.
            // We cannot reuse the prototype commands stored in fieldInitIrCommands because
            // they share Temp objects - reusing them causes register allocation collisions.
            Map<Integer, List<IrCommand>> freshFields = new java.util.HashMap<>();
            Map<Integer, List<IrCommand>> savedFieldInit = Ir.fieldInitIrCommands.get(type);
            Ir.fieldInitIrCommands.put(type, freshFields); // redirect to fresh map
            Ir.curClass = type;
            List<AstDecVar> fieldDecls = Ir.classFieldDecls.get(type);
            if (fieldDecls != null) {
                for (AstDecVar varDec : fieldDecls) {
                    varDec.irMe();
                }
            }
            Ir.fieldInitIrCommands.put(type, savedFieldInit); // restore prototype
            Ir.curClass = savedClass;
            Ir.curField = savedField;

            if(savedClass!=null){
                List<IrCommand> commandList = Ir.fieldInitIrCommands.get(savedClass).get(savedField);
                commandList.add(alocObjCommand);
                if(!freshFields.isEmpty()){
                    List<Integer> sortedKeys = new ArrayList<>(freshFields.keySet());
                    Collections.sort(sortedKeys);
                    for (Integer key : sortedKeys) {
                        List<IrCommand> fieldsCommands = freshFields.get(key);
                        for(IrCommand cmnd: fieldsCommands){
                            cmnd.allocatedObject = alocObjCommand;
                            commandList.add(cmnd);
                        }
                    }
                }
            }
            else{
                Ir.getInstance().AddIrCommand(alocObjCommand);
                if(!freshFields.isEmpty()){
                    List<Integer> sortedKeys = new ArrayList<>(freshFields.keySet());
                    Collections.sort(sortedKeys);
                    for (Integer key : sortedKeys) {
                        List<IrCommand> fieldsCommands = freshFields.get(key);
                        for(IrCommand cmnd: fieldsCommands){
                            cmnd.allocatedObject = alocObjCommand;
                            Ir.getInstance().AddIrCommand(cmnd);
                        }
                    }
                }
            }
            
    
            /*******************/
            /* [3] return dst */
            /*******************/
            return dst;
        }
    
        /********************************************/
        /* [4] Case 2: new TYPE[size] (array alloc) */
        /********************************************/
        Temp sizeTemp = sizeExp.irMe();
        curIrCommand = new IrCommandAllocateArray(dst, type, sizeTemp);
        if(Ir.curClass!=null){
            Ir.fieldInitIrCommands.get(Ir.curClass).get(Ir.curField).add(curIrCommand);
        }
        else{
            Ir.getInstance().AddIrCommand(curIrCommand); 
        }
    
        /*******************/
        /* [5] return dst */
        /*******************/
        return dst;
    }

    public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
        if (sizeExp  != null){
            curIdx = sizeExp.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
        }
        return curIdx;
	}

    public void debugOffset(){
		if(sizeExp != null){
            sizeExp.debugOffset();
        }
	}
    
}