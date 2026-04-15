package mips;

import analysis.Dbg;
import ir.IrCommand;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import temp.*;

public class MipsGenerator {

    private static final int WORD_SIZE = 4;
    private PrintWriter fileWriter;

    // ---- OUTPUT BUFFERS ----
    private StringWriter dataBuffer = new StringWriter();
    private StringWriter textBuffer = new StringWriter();
    private StringWriter globalInitBuffer = new StringWriter();

    private PrintWriter dataSec = new PrintWriter(dataBuffer);
    private PrintWriter textSec = new PrintWriter(textBuffer);
    private PrintWriter globalInitSec = new PrintWriter(globalInitBuffer);

    // Used to detect if we are inside a function
    private boolean insideFunction = false;

    // Called by IR builder before emitting a function prologue
    public void startFunction() {
        insideFunction = true;
    }
    public void endFunction() {
        insideFunction = false;
    }

    // Class bookkeeping
    private Map<String, Integer> classFieldCount = new HashMap<>();
    private Map<String, List<String>> classMethods = new HashMap<>();
    private Map<String, String> stringPool = new HashMap<>();
    private int stringCounter = 0;

    /* ===== FILE FINALIZATION ===== */

    public void finalizeFile() {
        // 1. .data
        fileWriter.print(".data\n");
        dataSec.flush();
        fileWriter.print(dataBuffer.toString());

        // 2. .text
        fileWriter.print(".text\n");

        // Emit runtime helpers (already in textSec)
        textSec.flush();
        fileWriter.print(textBuffer.toString());

        // 3. Emit global initializer function BEFORE main()
        fileWriter.print("_globals_init:\n");
        fileWriter.print("\tsubu $sp, $sp, 4\n");
        fileWriter.print("\tsw $ra, 0($sp)\n");
        globalInitSec.flush();
        fileWriter.print(globalInitBuffer.toString());
        fileWriter.print("\tlw $ra, 0($sp)\n");
        fileWriter.print("\taddu $sp, $sp, 4\n");
        fileWriter.print("\tjr $ra\n\n");

        // 4. Emit main trampoline
        fileWriter.print("main:\n");
        fileWriter.print("\tjal _globals_init\n");
        fileWriter.print("\tjal user_main\n");
        fileWriter.print("\tli $v0,10\n");
        fileWriter.print("\tsyscall\n");

        fileWriter.close();
    }

    /* ===== PRINTING ===== */

    public void printInt(Temp t) {
        textSec.format("\tmove $a0,%s\n", t);
        textSec.format("\tli $v0,1\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $a0,32\n");
        textSec.format("\tli $v0,11\n");
        textSec.format("\tsyscall\n");
    }

    public void printString(Temp t) {
        textSec.format("\tmove $a0,%s\n", t);
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
    }

    /* ===== GLOBALS ===== */

    public void allocate(String varName) {
        dataSec.format("global_%s: .word 0\n", varName);
    }

    public void loadGlobal(Temp dst, String varName) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tlw %s,global_%s\n", dst, varName);
    }

    public void storeGlobal(String varName, Temp src) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tsw %s,global_%s\n", src, varName);
    }

    /* ===== LOCALS ===== */

    public void loadLocal(Temp dst, int offset) {
        textSec.format("\tlw %s, %d($fp)\n", dst, offset);
    }

    public void storeLocal(int offset, Temp src) {
        textSec.format("\tsw %s, %d($fp)\n", src, offset);
    }

    public void li(Temp t, int value) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tli %s,%d\n", t, value);
    }

    /* ===== FIELD / ARRAY / POINTER ===== */

    public void addFieldOffset(Temp dst, Temp base, int fieldOffset) {
        textSec.format("\tli $s0,%d\n", (fieldOffset + 1) * 4);
        textSec.format("\tadd %s,%s,$s0\n", dst, base);
    }

    /**
     * Pointer arithmetic for array element address = skip size word + index*wordSize
     * Also performs: null check on base, bounds check on idx vs array length.
     */
    public void addArrayOffset(Temp dst, Temp base, Temp idx) {
        String okNull   = IrCommand.getFreshLabel("array_null_ok");
        String okLow    = IrCommand.getFreshLabel("array_low_ok");
        String okHigh   = IrCommand.getFreshLabel("array_high_ok");

        // Null check on array pointer
        textSec.format("\tbne %s,$zero,%s\n", base, okNull);
        textSec.format("\tla $a0,string_invalid_ptr_dref\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okNull);

        // Lower-bound check: idx >= 0
        textSec.format("\tbge %s,$zero,%s\n", idx, okLow);
        textSec.format("\tla $a0,string_access_violation\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okLow);

        // Upper-bound check: idx < length (stored at base[0])
        textSec.format("\tlw $s0,0(%s)\n", base);       // $s0 = array length
        textSec.format("\tblt %s,$s0,%s\n", idx, okHigh);
        textSec.format("\tla $a0,string_access_violation\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okHigh);

        // Compute element address: base + (idx+1)*4
        textSec.format("\tsll $s0,%s,2\n", idx);
        textSec.format("\taddi $s0,$s0,4\n");
        textSec.format("\tadd %s,%s,$s0\n", dst, base);
    }

    /**
     * Emits an inline null-pointer check on ptr.
     * If ptr == 0: print "Invalid Pointer Dereference" and exit.
     */
    public void checkNullPtr(Temp ptr) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        String okLabel = IrCommand.getFreshLabel("null_check_ok");
        out.format("\tbne %s,$zero,%s\n", ptr, okLabel);
        out.format("\tla $a0,string_invalid_ptr_dref\n");
        out.format("\tli $v0,4\n");
        out.format("\tsyscall\n");
        out.format("\tli $v0,10\n");
        out.format("\tsyscall\n");
        out.format("%s:\n", okLabel);
    }

    /* Dereferences a pointer: dst = Memory[ptr] */
    public void loadFromPointer(Temp dst, Temp ptr, int offset) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        String okLabel = IrCommand.getFreshLabel("reference_ok");
        out.format("\tbne %s,$zero,%s\n", ptr, okLabel);
        out.format("\tla $a0,string_invalid_ptr_dref\n");
        out.format("\tli $v0,4\n");
        out.format("\tsyscall\n");
        out.format("\tli $v0,10\n");
        out.format("\tsyscall\n");
        out.format("%s:\n", okLabel);
        out.format("\tlw %s,%d(%s)\n", dst, offset, ptr);
    }

    /* Stores a value through a pointer: Memory[ptr] = src */
    public void storeToPointer(Temp src, Temp ptr, int offset) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        String okLabel = IrCommand.getFreshLabel("reference_ok");
        out.format("\tbne %s,$zero,%s\n", ptr, okLabel);
        out.format("\tla $a0,string_invalid_ptr_dref\n");
        out.format("\tli $v0,4\n");
        out.format("\tsyscall\n");
        out.format("\tli $v0,10\n");
        out.format("\tsyscall\n");
        out.format("%s:\n", okLabel);
        out.format("\tsw %s,%d(%s)\n", src, offset, ptr);
    }

    /* Object/pointer equality: dst = (t1 == t2) ? 1 : 0 */
    public void eqPointers(Temp dst, Temp t1, Temp t2) {
        String eqLabel   = IrCommand.getFreshLabel("ptr_eq_yes");
        String doneLabel = IrCommand.getFreshLabel("ptr_eq_done");
        textSec.format("\tbeq %s,%s,%s\n", t1, t2, eqLabel);
        textSec.format("\tli %s,0\n", dst);
        textSec.format("\tj %s\n", doneLabel);
        textSec.format("%s:\n", eqLabel);
        textSec.format("\tli %s,1\n", dst);
        textSec.format("%s:\n", doneLabel);
    }

    /* ===== ARITHMETIC ===== */

    /**
     * Clamps dst to the range [-32768, 32767]. Uses $s0 as scratch.
     * Called after every arithmetic op.
     */
    private void saturate(Temp dst) {
        // saturate is only called from add/sub/mul/div which are already guarded,
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        String clampMin = IrCommand.getFreshLabel("sat_clampMin");
        String clampMax = IrCommand.getFreshLabel("sat_clampMax");
        String satDone  = IrCommand.getFreshLabel("sat_done");
        out.format("\tli $s0,32767\n");
        out.format("\tbgt %s,$s0,%s\n", dst, clampMax);
        out.format("\tli $s0,-32768\n");
        out.format("\tblt %s,$s0,%s\n", dst, clampMin);
        out.format("\tj %s\n", satDone);
        out.format("%s:\n", clampMax);
        out.format("\tli %s,32767\n", dst);
        out.format("\tj %s\n", satDone);
        out.format("%s:\n", clampMin);
        out.format("\tli %s,-32768\n", dst);
        out.format("%s:\n", satDone);
    }

    /* Integer add with saturation clamping */
    public void add(Temp dst, Temp oprnd1, Temp oprnd2) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tadd %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /* Integer subtract with saturation clamping */
    public void sub(Temp dst, Temp oprnd1, Temp oprnd2) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tsub %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /* Integer multiply with saturation clamping */
    public void mul(Temp dst, Temp oprnd1, Temp oprnd2) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tmul %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /**
     * Integer floor division with saturation clamping.
     * Checks for division by zero.
     */
    public void div(Temp dst, Temp oprnd1, Temp oprnd2) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        String okLabel = IrCommand.getFreshLabel("div_ok");
        out.format("\tbne %s,$zero,%s\n", oprnd2, okLabel);
        out.format("\tla $a0,string_illegal_div_by_0\n");
        out.format("\tli $v0,4\n");
        out.format("\tsyscall\n");
        out.format("\tli $v0,10\n");
        out.format("\tsyscall\n");
        out.format("%s:\n", okLabel);
        out.format("\tdiv %s,%s\n", oprnd1, oprnd2);
        // XOR BEFORE mflo: dst may alias oprnd1/oprnd2, so read signs while they are still valid
        out.format("\txor $s0,%s,%s\n", oprnd1, oprnd2); // $s0 MSB set iff signs differ
        out.format("\tmflo %s\n", dst);                   // truncated quotient into dst
        out.format("\tmfhi $s1\n");                        // remainder into $s1 
        // Floor correction: if remainder != 0 AND signs differ, floor = trunc - 1
        String floorDone = IrCommand.getFreshLabel("floor_done");
        out.format("\tbeq $s1,$zero,%s\n", floorDone);   // remainder==0: exact, skip
        out.format("\tbge $s0,$zero,%s\n", floorDone);   // same sign: truncation==floor, skip
        out.format("\taddiu %s,%s,-1\n", dst, dst);      // different signs: floor = trunc - 1
        out.format("%s:\n", floorDone);
        saturate(dst);
    }

    /* ===== STRINGS ===== */

    public void constString(Temp t, String value) {
        String strLabel;
        
        // Deduplicate strings so we don't fill .data with copies of the same word
        if (stringPool.containsKey(value)) {
            strLabel = stringPool.get(value);
        } else {
            strLabel = "str_" + (stringCounter++);
            stringPool.put(value, strLabel);
            dataSec.format("%s: .asciiz %s\n", strLabel, value);
        }

        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tla %s,%s\n", t, strLabel);
    }

    /**
     * String concatenation: dst = t1 + t2 (heap-allocated).
    */
    public void addStrings(Temp dst, Temp t1, Temp t2) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;

        out.format("\tsubu $sp,$sp,16\n");
        out.format("\tsw $ra,12($sp)\n");
        // $sp+8 is reserved for len(t1), filled below
        out.format("\tsw %s,4($sp)\n", t1);
        out.format("\tsw %s,0($sp)\n", t2);

        // strlen(t1) → $v0; spill result onto stack immediately
        out.format("\tlw $a0,4($sp)\n");
        out.format("\tjal __strlen\n");
        out.format("\tsw $v0,8($sp)\n");        // len(t1) safely on stack

        // strlen(t2) → $v0
        out.format("\tlw $a0,0($sp)\n");
        out.format("\tjal __strlen\n");

        // sbrk(len(t1) + len(t2) + 1) — reload len(t1) from stack
        out.format("\tlw $a0,8($sp)\n");        // len(t1)
        out.format("\tadd $a0,$a0,$v0\n");      // + len(t2)
        out.format("\taddi $a0,$a0,1\n");       // + null terminator
        out.format("\tli $v0,9\n");
        out.format("\tsyscall\n");

        // Store new buffer address into dst NOW, before any further jal clobbers $v0
        out.format("\tmove %s,$v0\n", dst);

        // strcpy(t1 → dst): fills buffer, $v0 = pointer past null byte
        out.format("\tlw $a0,4($sp)\n");
        out.format("\tmove $a1,%s\n", dst);
        out.format("\tjal __strcpy\n");

        // strcpy(t2 → $v0): appends second string right after first
        out.format("\tlw $a0,0($sp)\n");
        out.format("\tmove $a1,$v0\n");
        out.format("\tjal __strcpy\n");

        // Restore frame — dst already holds the concatenated string pointer
        out.format("\tlw $ra,12($sp)\n");
        out.format("\taddu $sp,$sp,16\n");
    }

    /**
     * String content equality: dst = (strcmp(t1,t2)==0) ? 1 : 0
     */
    public void eqStrings(Temp dst, Temp t1, Temp t2) {
        // save $ra
        textSec.format("\tsubu $sp,$sp,4\n");
        textSec.format("\tsw $ra,0($sp)\n");

        textSec.format("\tmove $a0,%s\n", t1);
        textSec.format("\tmove $a1,%s\n", t2);
        textSec.format("\tjal __strcmp\n");   // $v0 = 0 if equal

        // restore $ra
        textSec.format("\tlw $ra,0($sp)\n");
        textSec.format("\taddu $sp,$sp,4\n");

        // dst = ($v0 == 0) ? 1 : 0
        String eqLabel   = IrCommand.getFreshLabel("str_eq_yes");
        String doneLabel = IrCommand.getFreshLabel("str_eq_done");
        textSec.format("\tbeq $v0,$zero,%s\n", eqLabel);
        textSec.format("\tli %s,0\n", dst);
        textSec.format("\tj %s\n", doneLabel);
        textSec.format("%s:\n", eqLabel);
        textSec.format("\tli %s,1\n", dst);
        textSec.format("%s:\n", doneLabel);
    }

    /* ===== HEAP ALLOCATION ===== */

    // array layout: [length][elem0][elem1]...
    public void allocateArray(Temp dst, Temp size) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        out.format("\tmove $s1,%s\n", size);   // save size before syscall
        out.format("\taddi $a0,%s,1\n", size);
        out.format("\tsll $a0,$a0,2\n");
        out.format("\tli $v0,9\n");
        out.format("\tsyscall\n");
        out.format("\tmove %s,$v0\n", dst);
        out.format("\tsw $s1,0(%s)\n", dst);   // store saved size at base[0]
    }

    /**
     * Allocates heap memory for a class instance.
     */
    public void allocateObject(Temp dst, String type) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        int numFields = classFieldCount.getOrDefault(type, 0);
        int size = (1 + numFields) * WORD_SIZE;
        out.format("\tli $a0,%d\n", size);
        out.format("\tli $v0,9\n");
        out.format("\tsyscall\n");
        out.format("\tmove %s,$v0\n", dst);
        if (!classMethods.getOrDefault(type, new java.util.ArrayList<String>()).isEmpty()) {
            out.format("\tla $s0,%s_vtable\n", type);
            out.format("\tsw $s0,0(%s)\n", dst);
        }
    }

    /* ===== CLASSES & VTABLES ===== */

    /**
     * Emits the vtable for a class into the .data section.
     */
    public void declareClass(String className, Map<String, Integer> methodOffsets, int fieldCount, Map<String,String> methodLabels) {
        String[] ordered = new String[methodOffsets.size()];
        for (Map.Entry<String, Integer> e : methodOffsets.entrySet()) {
            ordered[e.getValue()] = e.getKey();
        }

        classMethods.put(className, Arrays.asList(ordered));
        classFieldCount.put(className, fieldCount);
        Dbg.p("Declared class " + className + " with fields=" + fieldCount + " methods=" + methodOffsets);
        if (ordered.length > 0) {
            dataSec.format("%s_vtable:", className);
            for (String m : ordered) {
                dataSec.format(" .word %s\n", methodLabels.get(m));
            }
            dataSec.format("\n");
        }
    }

    /* ===== FUNCTION CALLS ===== */

    // call method of object
    public void callMethod(Temp dst, Temp object, int offset, List<Temp> args) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;

        String okLabel = IrCommand.getFreshLabel("reference_ok");
        out.format("\tbne %s,$zero,%s\n", object, okLabel);
        out.format("\tla $a0,string_invalid_ptr_dref\n");
        out.format("\tli $v0,4\n");
        out.format("\tsyscall\n");
        out.format("\tli $v0,10\n");
        out.format("\tsyscall\n");
        out.format("%s:\n", okLabel);

        // Load the vtable entry into $s0 BEFORE touching the stack
        out.format("\tlw $s0, 0(%s)\n", object);
        out.format("\tlw $s0, %d($s0)\n", offset * 4);

        // Save $ra (needed when called from _globals_init which has no full frame)
        out.print("\tsubu $sp, $sp, 4\n");
        out.print("\tsw $ra, 0($sp)\n");

        // Push args RIGHT-TO-LEFT first (last arg at lowest address)
        for (int i = args.size() - 1; i >= 0; i--) {
            out.print("\tsubu $sp, $sp, 4\n");
            out.format("\tsw %s, 0($sp)\n", args.get(i));
        }
        // Push 'this' (object) LAST so it sits at $fp+8 inside the callee
        out.print("\tsubu $sp, $sp, 4\n");
        out.format("\tsw %s, 0($sp)\n", object);

        out.print("\tjalr $s0\n");
        // Pop all pushed words: object + all args
        out.format("\taddu $sp, $sp, %d\n", (args.size() + 1) * 4);

        // Restore $ra
        out.print("\tlw $ra, 0($sp)\n");
        out.print("\taddu $sp, $sp, 4\n");

        out.format("\tmove %s, $v0\n", dst);
    }

    // call function with label
    public void callFunc(Temp dst, String label, List<Temp> args) {
        PrintWriter out = insideFunction ? textSec : globalInitSec;
        int argsNumber = 0;

        for (int i = args.size() - 1; i >= 0; i--) {
            argsNumber++;
            out.print("\tsubu $sp, $sp, 4\n");
            out.format("\tsw %s, 0($sp)\n", args.get(i));
        }
        out.format("\tjal %s\n", label);
        if (argsNumber > 0) {
            out.format("\taddu $sp, $sp, %d\n", argsNumber * 4);
        }
        out.format("\tmove %s, $v0\n", dst);
    }

    public void returnToCaller(Temp t, String functionName) {
        if (t != null) {
            textSec.format("\tmove $v0, %s\n", t);
        }
        textSec.format("\tj %s_epilogue\n", functionName);
    }

    /* ===== FUNCTION PROLOGUE / EPILOGUE ===== */

    public void emitPrologue(String functionName, int localVarCount) {
        textSec.format("%s:\n", functionName);
        textSec.println("# prologue");

        textSec.println("\tsubu $sp, $sp, 8");
        textSec.println("\tsw $ra, 4($sp)");
        textSec.println("\tsw $fp, 0($sp)");
        textSec.println("\tmove $fp, $sp");

        // DYNAMIC STACK ALLOCATION
        int stackSpace = 40 + (localVarCount * 4);
        textSec.format("\tsubu $sp, $sp, %d\n", stackSpace);

        for (int i = 0; i <= 9; i++) {
            textSec.format("\tsw $t%d, %d($fp)\n", i, -((i + 1) * 4));
        }
        textSec.println("");
    }

    public void emitEpilogue(String functionName) {
        // Default return value = 0/nil — only reached by fall-through (missing return)
        // Real return paths jump directly to the label below, skipping this.
        textSec.println("\tli $v0,0");
        textSec.format("%s_epilogue:\n", functionName);

        // Restore T0-T9 relative to the FP
        for (int i = 0; i <= 9; i++) {
            textSec.format("\tlw $t%d, %d($fp)\n", i, -((i + 1) * 4));
        }

        // Snap the stack pointer back to the frame pointer
        textSec.println("\tmove $sp, $fp");

        // Restore RA and FP from the 8-byte header
        textSec.println("\tlw $ra, 4($sp)");
        textSec.println("\tlw $fp, 0($sp)");

        // Pop the 8-byte header and return
        textSec.println("\taddi $sp, $sp, 8");
        textSec.println("\tjr $ra");
    }

    /* ===== LABELS & BRANCHES ===== */

    public void label(String inlabel) {
        if (inlabel.equals("main")) {
            textSec.println("main:");
            textSec.println("\tjal user_main");
            textSec.println("\tli $v0, 10");
            textSec.println("\tsyscall");
        } else {
            textSec.format("%s:\n", inlabel);
        }
    }

    public void jump(String inlabel) {
        textSec.format("\tj %s\n", inlabel);
    }

    public void blt(Temp oprnd1, Temp oprnd2, String label) {
        textSec.format("\tblt %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void bge(Temp oprnd1, Temp oprnd2, String label) {
        textSec.format("\tbge %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void bne(Temp oprnd1, Temp oprnd2, String label) {
        textSec.format("\tbne %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void beq(Temp oprnd1, Temp oprnd2, String label) {
        textSec.format("\tbeq %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void beqz(Temp oprnd1, String label) {
        textSec.format("\tbeq %s,$zero,%s\n", oprnd1, label);
    }

    /* ===== SINGLETON ===== */

    private static MipsGenerator instance = null;

    protected MipsGenerator() {
    }

    public static void init(String outputFilePath) {
        if (instance == null) {
            instance = new MipsGenerator();
            try {
                instance.fileWriter = new PrintWriter(outputFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance.writePreamble();
        }
    }

    public static MipsGenerator getInstance() {
        return instance;
    }

    /* ===== RUNTIME PREAMBLE ===== */

    private void writePreamble() {
        // Error strings go into .data buffer
        dataSec.print("string_access_violation: .asciiz \"Access Violation\"\n");
        dataSec.print("string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
        dataSec.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");

        textSec.print("__strlen:\n");
        textSec.print("\tmove $s0,$a0\n");
        textSec.print("__strlen_loop:\n");
        textSec.print("\tlb $s1,0($s0)\n");
        textSec.print("\tbeq $s1,$zero,__strlen_done\n");
        textSec.print("\taddi $s0,$s0,1\n");
        textSec.print("\tj __strlen_loop\n");
        textSec.print("__strlen_done:\n");
        textSec.print("\tsubu $v0,$s0,$a0\n");
        textSec.print("\tjr $ra\n");

        textSec.print("__strcpy:\n");
        textSec.print("__strcpy_loop:\n");
        textSec.print("\tlb $s0,0($a0)\n");
        textSec.print("\tbeq $s0,$zero,__strcpy_done\n");
        textSec.print("\tsb $s0,0($a1)\n");
        textSec.print("\taddi $a0,$a0,1\n");
        textSec.print("\taddi $a1,$a1,1\n");
        textSec.print("\tj __strcpy_loop\n");
        textSec.print("__strcpy_done:\n");
        textSec.print("\tsb $zero,0($a1)\n");
        textSec.print("\tmove $v0,$a1\n");
        textSec.print("\tjr $ra\n");

        textSec.print("__strcmp:\n");
        textSec.print("__strcmp_loop:\n");
        textSec.print("\tlb $s0,0($a0)\n");
        textSec.print("\tlb $s1,0($a1)\n");
        textSec.print("\tbne $s0,$s1,__strcmp_ne\n");
        textSec.print("\tbeq $s0,$zero,__strcmp_eq\n");
        textSec.print("\taddi $a0,$a0,1\n");
        textSec.print("\taddi $a1,$a1,1\n");
        textSec.print("\tj __strcmp_loop\n");
        textSec.print("__strcmp_eq:\n");
        textSec.print("\tli $v0,0\n");
        textSec.print("\tjr $ra\n");
        textSec.print("__strcmp_ne:\n");
        textSec.print("\tli $v0,1\n");
        textSec.print("\tjr $ra\n");
    }
}