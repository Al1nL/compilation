package mips;

import analysis.Dbg;
import ir.IrCommand;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import temp.*;

public class MipsGenerator {

    private static final int WORD_SIZE = 4;
    private PrintWriter fileWriter;

    // Separate buffers for .data and .text sections
    private StringWriter dataBuffer = new StringWriter();
    private StringWriter textBuffer = new StringWriter();
    private PrintWriter  dataSec    = new PrintWriter(dataBuffer);
    private PrintWriter  textSec    = new PrintWriter(textBuffer);

    // Track number of fields and vtable per class
    private Map<String, Integer> classFieldCount = new HashMap<>();
    private Map<String, List<String>> classMethods = new HashMap<>();

    /**
     * Flushes the accumulated .data and .text buffers to the output file,
     * emits the exit syscall, and closes the file.
     * Must be called once after all IR commands are done.
     */
    public void finalizeFile() {
        // 1. Write entire .data section
        fileWriter.print(".data\n");
        dataSec.flush();
        fileWriter.print(dataBuffer.toString());

        // 2. Write entire .text section
        fileWriter.print(".text\n");
        textSec.flush();
        fileWriter.print(textBuffer.toString());

        // 3. Exit syscall
        fileWriter.print("\tli $v0,10\n");
        fileWriter.print("\tsyscall\n");
        fileWriter.close();
    }

    public void printInt(Temp t) {
        textSec.format("\tmove $a0,%s\n", t);
        textSec.format("\tli $v0,1\n");
        textSec.format("\tsyscall\n");
        // Print space character (ASCII 32) after each number
        textSec.format("\tli $a0,32\n");
        textSec.format("\tli $v0,11\n");
        textSec.format("\tsyscall\n");
    }

    public void printString(Temp t) {
        textSec.format("\tmove $a0,%s\n", t);
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
    }

    /* Global variable: emit .data label initialized to 0 */
    public void allocate(String varName) {
        dataSec.format("global_%s: .word 0\n", varName);
    }

    public void loadGlobal(Temp dst, String varName) {
        textSec.format("\tlw %s,global_%s\n", dst, varName);
    }

    public void loadLocal(Temp dst, int offset) {
        textSec.format("\tlw %s, %d($fp)\n", dst, offset);
    }

    public void storeGlobal(String varName, Temp src) {
        textSec.format("\tsw %s,global_%s\n", src, varName);
    }

    public void storeLocal(int offset, Temp src) {
        textSec.format("\tsw %s, %d($fp)\n", src, offset);
    }

    public void li(Temp t, int value) {
        textSec.format("\tli %s,%d\n", t, value);
    }

    /**
     * Pointer arithmetic for array element address = skip size word + index*wordSize
     */
    public void addOffset(Temp dst, Temp base, Temp idx) {
    textSec.format("\tsll $s0,%s,2\n", idx);  // index * 4
    textSec.format("\taddi $s0,$s0,4\n");     // + 4 bytes to skip length word
    textSec.format("\tadd %s,%s,$s0\n", dst, base);
}

    /* Dereferences a pointer: dst = Memory[ptr] */
    public void loadFromPointer(Temp dst, Temp ptr, int offset) {
        String okLabel = IrCommand.getFreshLabel("reference_ok");
        textSec.format("\tbne %s,$zero,%s\n", ptr, okLabel);
        textSec.format("\tla $a0,string_invalid_ptr_dref\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okLabel);
        textSec.format("\tlw %s,%d(%s)\n", dst, offset, ptr);
    }

    /* Stores a value through a pointer: Memory[ptr] = src */
    public void storeToPointer(Temp src, Temp ptr, int offset) {
        String okLabel = IrCommand.getFreshLabel("reference_ok");
        textSec.format("\tbne %s,$zero,%s\n", ptr, okLabel);
        textSec.format("\tla $a0,string_invalid_ptr_dref\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okLabel);
        textSec.format("\tsw %s,%d(%s)\n", src, offset, ptr);
    }
    /**
     * Clamps dst to the range [-32768, 32767]. Uses $s0 as scratch.
     * Called after every arithmetic op.
     */
    private void saturate(Temp dst) {
        String clampMin = IrCommand.getFreshLabel("sat_clampMin");
        String clampMax = IrCommand.getFreshLabel("sat_clampMax");
        String satDone  = IrCommand.getFreshLabel("sat_done");
        textSec.format("\tli $s0,32767\n");
        textSec.format("\tbgt %s,$s0,%s\n", dst, clampMax);
        textSec.format("\tli $s0,-32768\n");
        textSec.format("\tblt %s,$s0,%s\n", dst, clampMin);
        textSec.format("\tj %s\n", satDone);
        textSec.format("%s:\n", clampMax);
        textSec.format("\tli %s,32767\n", dst);
        textSec.format("\tj %s\n", satDone);
        textSec.format("%s:\n", clampMin);
        textSec.format("\tli %s,-32768\n", dst);
        textSec.format("%s:\n", satDone);
    }

    /* Integer add with saturation clamping */
    public void add(Temp dst, Temp oprnd1, Temp oprnd2) {
        textSec.format("\tadd %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /* Integer subtract with saturation clamping */
    public void sub(Temp dst, Temp oprnd1, Temp oprnd2) {
        textSec.format("\tsub %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /* Integer multiply with saturation clamping */
    public void mul(Temp dst, Temp oprnd1, Temp oprnd2) {
        textSec.format("\tmul %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /**
     * Integer floor division with saturation clamping.
     * Checks for division by zero.
     */
    public void div(Temp dst, Temp oprnd1, Temp oprnd2) {
        String okLabel = IrCommand.getFreshLabel("div_ok");
        textSec.format("\tbne %s,$zero,%s\n", oprnd2, okLabel);
        textSec.format("\tla $a0,string_illegal_div_by_0\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okLabel);
        textSec.format("\tdiv %s,%s\n", oprnd1, oprnd2);
        textSec.format("\tmflo %s\n", dst);
        saturate(dst);
    }

    public void constString(Temp t, String value) {
        String strLabel = String.format("str_%s", t).replace("$", "");
        // string literal goes into .data buffer
        dataSec.format("%s: .asciiz \"%s\"\n", strLabel, value);
        // load-address instruction goes into .text buffer
        textSec.format("\tla %s,%s\n", t, strLabel);
    }

    // array layout: [length][elem0][elem1]...
    public void allocateArray(Temp dst, Temp size) {
        textSec.format("\tmove $s1,%s\n", size);   // save size before syscall
        textSec.format("\taddi $a0,%s,1\n", size);
        textSec.format("\tsll $a0,$a0,2\n");
        textSec.format("\tli $v0,9\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tmove %s,$v0\n", dst);
        textSec.format("\tsw $s1,0(%s)\n", dst);   // store saved size, not dst
    }

    // call method of object
    public void callMethod(Temp dst, Temp object, int offset, List<Temp> args) {
        int argsNumber = 1;

        String okLabel = IrCommand.getFreshLabel("reference_ok");
        textSec.format("\tbne %s,$zero,%s\n", object, okLabel);
        textSec.format("\tla $a0,string_invalid_ptr_dref\n");
        textSec.format("\tli $v0,4\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tli $v0,10\n");
        textSec.format("\tsyscall\n");
        textSec.format("%s:\n", okLabel);

        textSec.format("\tlw $s0, 0(%s)\n", object);
        textSec.format("\tlw $s0, %d($s0)\n", offset);

        textSec.print("\tsubu $sp, $sp, 4\n");
        textSec.format("\tsw %s, 0($sp)\n", object);

        for (int i = args.size() - 1; i >= 0; i--) {
            argsNumber++;
            textSec.print("\tsubu $sp, $sp, 4\n");
            textSec.format("\tsw %s, 0($sp)\n", args.get(i));
        }
        textSec.print("\tjalr $s0\n");
        textSec.format("\taddu $sp, $sp, %d\n", argsNumber * 4);
        textSec.format("\tmove %s, $v0\n", dst);
    }

    // call function with label
    public void callFunc(Temp dst, String label, List<Temp> args) {
        int argsNumber = 0;

        for (int i = args.size() - 1; i >= 0; i--) {
            argsNumber++;
            textSec.print("\tsubu $sp, $sp, 4\n");
            textSec.format("\tsw %s, 0($sp)\n", args.get(i));
        }
        textSec.format("\tjal %s\n", label);
        if (argsNumber > 0) {
            textSec.format("\taddu $sp, $sp, %d\n", argsNumber * 4);
        }
        textSec.format("\tmove %s, $v0\n", dst);
    }

    public void returnToCaller(Temp t, String functionName) {
        textSec.format("\tmove $v0, %s\n", t);
        textSec.format("\tj %s_epilogue\n", functionName);
    }

    /**
     * Emits the vtable for a class into the .data section.
     * Layout: className_vtable: .word method0 method1 ...
     */
    public void declareClass(String className, Map<String, Integer> methodOffsets, int fieldCount) {
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
                dataSec.format(" .word %s", m);
            }
            dataSec.format("\n");
        }
    }

    /**
     * Allocates heap memory for a class instance.
     */
    public void allocateObject(Temp dst, String type) {
        int numFields = classFieldCount.getOrDefault(type, 0);
        int size = (1 + numFields) * WORD_SIZE;
        textSec.format("\tli $a0,%d\n", size);
        textSec.format("\tli $v0,9\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tmove %s,$v0\n", dst);
        if (!classMethods.getOrDefault(type, new java.util.ArrayList<String>()).isEmpty()) {
            textSec.format("\tla $s0,%s_vtable\n", type);
            textSec.format("\tsw $s0,0(%s)\n", dst);
        }
    }

    public void addStrings(Temp dst, Temp t1, Temp t2) {
        textSec.format("\tsubu $sp,$sp,4\n");
        textSec.format("\tsw $ra,0($sp)\n");

        textSec.format("\tmove $a0,%s\n", t1);
        textSec.format("\tjal __strlen\n");
        textSec.format("\tmove $s0,$v0\n");

        textSec.format("\tmove $a0,%s\n", t2);
        textSec.format("\tjal __strlen\n");
        textSec.format("\tmove $s1,$v0\n");

        textSec.format("\tadd $a0,$s0,$s1\n");
        textSec.format("\taddi $a0,$a0,1\n");
        textSec.format("\tli $v0,9\n");
        textSec.format("\tsyscall\n");
        textSec.format("\tmove %s,$v0\n", dst);

        textSec.format("\tmove $a0,%s\n", t1);
        textSec.format("\tmove $a1,%s\n", dst);
        textSec.format("\tjal __strcpy\n");

        textSec.format("\tmove $a0,%s\n", t2);
        textSec.format("\tmove $a1,$v0\n");
        textSec.format("\tjal __strcpy\n");

        textSec.format("\tlw $ra,0($sp)\n");
        textSec.format("\taddu $sp,$sp,4\n");
    }

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
    textSec.format("%s_epilogue:\n", functionName);

    // 1. Restore T0-T9 relative to the FP
    // Since we saved them right after setting FP, they are at -4, -8, etc.
    for (int i = 0; i <= 9; i++) {
        textSec.format("\tlw $t%d, %d($fp)\n", i, -((i + 1) * 4));
    }

    // 2. SNAP the stack pointer back to the frame pointer
    // This wipes away all saved T-regs and any local variables (like those at -44)
    textSec.println("\tmove $sp, $fp");

    // 3. Restore RA and FP from the 8-byte header we saved at the start
    textSec.println("\tlw $ra, 4($sp)");
    textSec.println("\tlw $fp, 0($sp)");

    // 4. Pop the 8-byte header and return
    textSec.println("\taddi $sp, $sp, 8");
    textSec.println("\tjr $ra");
}

    /* USUAL SINGLETON IMPLEMENTATION */
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

    private void writePreamble() {
        // Error strings go into .data buffer
        dataSec.print("string_access_violation: .asciiz \"Access Violation\"\n");
        dataSec.print("string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
        dataSec.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");

        // Runtime helpers go into .text buffer
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
    }

    
}