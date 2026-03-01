        package ast;

import ir.*;
        import java.util.*;
        import temp.*;
        import types.*;
        import variable.*;
        

        public class AstProgram extends AstNode {

            public AstDecList head;

            public AstProgram(AstDecList head) {
                serialNumber = AstNodeSerialNumber.getFresh();

                this.head = head;
            }

            public void printMe() {

                if (head != null) {
                    head.printMe();
                }

                AstGraphviz.getInstance().logNode(serialNumber, "PROGRAM");

                if (head != null) {
                    AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
                }
            }

            @Override
            public Temp irMe() {
                Ir ir = Ir.getInstance();

                List<AstDecVar> globals = new ArrayList<>();
                List<AstDecFunc> functions = new ArrayList<>();
                List<AstDec> classes = new ArrayList<>();

                // ----- PASS 1: Traverse the list and classify -----
                AstDecList it = this.head;
                while (it != null) {
                    AstDec dec = it.head;

                    if (dec instanceof AstDecVar varDec) {
                        globals.add(varDec);
                    } else if (dec instanceof AstDecFunc funcDec) {
                        functions.add(funcDec);
                    }
                    else if (dec instanceof AstDecClass classDec) {
                        classes.add(classDec);
                    }

                    it = it.tail;
                }

                // proccess class declarations to set up class field and method offsets
                for (AstDec c : classes) {
                    c.irMe(); 
                }

                // ----- PASS 2: Emit globals -----
                for (AstDecVar g : globals) {
                    g.irMe();   // emits ALLOC, maybe STORE if has initializer
                }

                // ----- Switch to function IR -----
                ir.switchToMain();

                // ----- Emit all functions (including main) -----
                for (AstDecFunc f : functions) {
                    f.irMe();   // emits LABEL, body, RETURN etc.
                }
                return null;
            }
        
        @Override
        public Type semantMe() {
            if (head != null) {
                head.semantMe();
            }
            return null;
        }
        public int offsetMe(Map<Variable, Integer> offsets, int curIdx, String curClass, Map<String, Map<String, Integer>> classFieldOffsets, Map<String, Map<String, Integer>> classMethodOffsets, Map<String, Map<String, String>> methodLabels){
		    if (head != null) {
                curIdx = head.offsetMe(offsets, curIdx, curClass, classFieldOffsets, classMethodOffsets, methodLabels);
            }
            return curIdx;
	    }
        public void debugOffset(){
            if (head != null) {
                head.debugOffset();
            }
            
        }
    } 
