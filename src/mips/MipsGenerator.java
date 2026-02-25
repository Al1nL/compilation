package mips;

import ir.IrCommand;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import temp.*;

public class MipsGenerator {

    private static final int WORD_SIZE = 4;
    private PrintWriter fileWriter;

    // Track number of fields and vtable per class */
    private Map<String, Integer> classFieldCount = new HashMap<>();
    private Map<String, List<String>> classMethods = new HashMap<>();

    /**
     * Emits the exit syscall and closes the output file. Must be called once
     * after all IR commands are done.
     */
    public void finalizeFile() {
        fileWriter.print("\tli $v0,10\n");
        fileWriter.print("\tsyscall\n");
        fileWriter.close();
    }

    public void printInt(Temp t) {
        fileWriter.format("\tmove $a0,%s\n", t);
        fileWriter.format("\tli $v0,1\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tli $a0,32\n");
        fileWriter.format("\tli $v0,11\n");
        fileWriter.format("\tsyscall\n");
    }

//	public Temp addressLocalVar(int serialLocalVarNum)
//	{
//		Temp t  = TempFactory.getInstance().getFreshTemp();
//		int idx = t.getSerialNumber();
//
//		fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,-serialLocalVarNum*WORD_SIZE);
//
//		return t;
//	}

    /* Global variable: emit .data label initialized to 0 */
    public void allocate(String varName) {
        fileWriter.format(".data\n");
        fileWriter.format("\tglobal_%s: .word 0\n", varName);
    }

    public void load(Temp dst, String varName) {
        fileWriter.format("\tlw %s,global_%s\n", dst, varName);
    }

    public void store(String varName, Temp src) {
        fileWriter.format("\tsw %s,global_%s\n", src, varName);
    }

    public void li(Temp t, int value) {
        fileWriter.format("\tli %s,%d\n", t, value);
    }

    /**
     * Pointer arithmetic for array element address: dst = base + (idx+1)*4
     */
    public void addOffset(Temp dst, Temp base, Temp idx) {
        fileWriter.format("\taddi $s0,%s,1\n", idx);       // $s0 = idx+1 - skip length slot
        fileWriter.format("\tsll $s0,$s0,2\n");             // $s0 = (idx+1)*4
        fileWriter.format("\tadd %s,%s,$s0\n", dst, base); // dst = base + offset
    }

    /* Dereferences a pointer: dst = Memory[ptr] */
    public void loadFromPointer(Temp dst, Temp ptr) {
        fileWriter.format("\tlw %s,0(%s)\n", dst, ptr);
    }

    /**
     * Clamps dst to the range [-32768, 32767]. Uses $s0 as scratch. Called
     * after every arithmetic op
     */
    private void saturate(Temp dst) {
        String clampMin = IrCommand.getFreshLabel("sat_clampMin");
        String clampMax = IrCommand.getFreshLabel("sat_clampMax");
        String satDone = IrCommand.getFreshLabel("sat_done");
        fileWriter.format("\tli $s0,32767\n");
        fileWriter.format("\tbgt %s,$s0,%s\n", dst, clampMax);
        fileWriter.format("\tli $s0,-32768\n");
        fileWriter.format("\tblt %s,$s0,%s\n", dst, clampMin);
        fileWriter.format("\tj %s\n", satDone);
        fileWriter.format("%s:\n", clampMax);
        fileWriter.format("\tli %s,32767\n", dst);
        fileWriter.format("\tj %s\n", satDone);
        fileWriter.format("%s:\n", clampMin);
        fileWriter.format("\tli %s,-32768\n", dst);
        fileWriter.format("%s:\n", satDone);
    }

    /* Integer add with saturation clamping */
    public void add(Temp dst, Temp oprnd1, Temp oprnd2) {
        fileWriter.format("\tadd %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /* Integer subtract with saturation clamping */
    public void sub(Temp dst, Temp oprnd1, Temp oprnd2) {
        fileWriter.format("\tsub %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /* Integer multiply with saturation clamping */
    public void mul(Temp dst, Temp oprnd1, Temp oprnd2) {
        fileWriter.format("\tmul %s,%s,%s\n", dst, oprnd1, oprnd2);
        saturate(dst);
    }

    /**
     * Integer floor division with saturation clamping. Checks for division by
     * zero. if oprnd2 == 0, prints error message and exits. Otherwise divides
     * and moves quotient from LO register into dst via mflo.
     */
    public void div(Temp dst, Temp oprnd1, Temp oprnd2) {
        String okLabel = IrCommand.getFreshLabel("div_ok");
        fileWriter.format("\tbne %s,$zero,%s\n", oprnd2, okLabel);
        fileWriter.format("\tla $a0,string_illegal_div_by_0\n");
        fileWriter.format("\tli $v0,4\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tli $v0,10\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("%s:\n", okLabel);
        fileWriter.format("\tdiv %s,%s\n", oprnd1, oprnd2);
        fileWriter.format("\tmflo %s\n", dst);
        saturate(dst);
    }

    public void constString(Temp t, String value) {
        String strLabel = String.format("str_%s", t).replace("$", "");
        fileWriter.format(".data\n");
        fileWriter.format("\t%s: .asciiz \"%s\"\n", strLabel, value);
        fileWriter.format(".text\n");
        fileWriter.format("\tla %s,%s\n", t, strLabel);
    }

    // array layout: [length][elem0][elem1]...                    
    public void allocateArray(Temp dst, Temp size) {
        fileWriter.format("\taddi $a0,%s,1\n", size);    // +1 for length slot
        fileWriter.format("\tsll $a0,$a0,2\n");           // * 4 bytes per word
        fileWriter.format("\tli $v0,9\n");                // allocate on heap
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tmove %s,$v0\n", dst);        // dst = base pointer
        fileWriter.format("\tsw %s,0(%s)\n", size, dst);  // store length at [0]
    }

    /**
     * Emits the vtable for a class into the .data section. Layout:
     * className_vtable: .word method0 method1 ... Also records field count and
     * method list for later used by allocateObject and virtualCall.
     */
    public void declareClass(String className, Map<String, Integer> methodOffsets, int fieldCount) {
        // invert map: index -> methodName, to get ordered list
        String[] ordered = new String[methodOffsets.size()];
        for (java.util.Map.Entry<String, Integer> e : methodOffsets.entrySet()) {
            ordered[e.getValue()] = e.getKey();
        }

        classMethods.put(className, Arrays.asList(ordered));
        classFieldCount.put(className, fieldCount);

        // emit vtable in .data
        if (ordered.length > 0) {
            fileWriter.format(".data\n");
            fileWriter.format("%s_vtable:", className);
            for (String m : ordered) {
                fileWriter.format(" .word %s", m);
            }
            fileWriter.format("\n");
        }
    }

    /**
     * Allocates heap memory for a class instance: (1 vtable ptr + numFields) *
     * 4 bytes via sbrk. Stores vtable pointer at word [0] of the object.
     */
    public void allocateObject(Temp dst, String type) {

        int numFields = classFieldCount.getOrDefault(type, 0);
        int size = (1 + numFields) * WORD_SIZE; // word[0]=vtable, rest=fields
        fileWriter.format("\tli $a0,%d\n", size);
        fileWriter.format("\tli $v0,9\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tmove %s,$v0\n", dst);
        // store vtable pointer at offset 0 if its class has any methods
        if (!classMethods.getOrDefault(type, new java.util.ArrayList<String>()).isEmpty()) {
            fileWriter.format("\tla $s0,%s_vtable\n", type);
            fileWriter.format("\tsw $s0,0(%s)\n", dst);
        }
    }

    public void addStrings(Temp dst, Temp t1, Temp t2) {
        // Save $ra
        fileWriter.format("\tsubu $sp,$sp,4\n");
        fileWriter.format("\tsw $ra,0($sp)\n");

        // $s0 = strlen(t1)
        fileWriter.format("\tmove $a0,%s\n", t1);
        fileWriter.format("\tjal __strlen\n");
        fileWriter.format("\tmove $s0,$v0\n");

        // $s1 = strlen(t2)
        fileWriter.format("\tmove $a0,%s\n", t2);
        fileWriter.format("\tjal __strlen\n");
        fileWriter.format("\tmove $s1,$v0\n");

        // allocate len(t1)+len(t2)+1 bytes via sbrk
        fileWriter.format("\tadd $a0,$s0,$s1\n");
        fileWriter.format("\taddi $a0,$a0,1\n");
        fileWriter.format("\tli $v0,9\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tmove %s,$v0\n", dst);

        // copy t1 into buffer, $v0 returns write pointer past t1
        fileWriter.format("\tmove $a0,%s\n", t1);
        fileWriter.format("\tmove $a1,%s\n", dst);
        fileWriter.format("\tjal __strcpy\n");

        // copy t2 starting where t1 ended, __strcpy writes null terminator
        fileWriter.format("\tmove $a0,%s\n", t2);
        fileWriter.format("\tmove $a1,$v0\n");
        fileWriter.format("\tjal __strcpy\n");

        // Restore $ra
        fileWriter.format("\tlw $ra,0($sp)\n");
        fileWriter.format("\taddu $sp,$sp,4\n");
    }

    public void label(String inlabel) {
        if (inlabel.equals("main")) {
            fileWriter.format(".text\n");
            fileWriter.format("%s:\n", inlabel);
        } else {
            fileWriter.format("%s:\n", inlabel);
        }
    }

    public void jump(String inlabel) {
        fileWriter.format("\tj %s\n", inlabel);
    }

    public void blt(Temp oprnd1, Temp oprnd2, String label) {
        fileWriter.format("\tblt %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void bge(Temp oprnd1, Temp oprnd2, String label) {
        fileWriter.format("\tbge %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void bne(Temp oprnd1, Temp oprnd2, String label) {
        fileWriter.format("\tbne %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void beq(Temp oprnd1, Temp oprnd2, String label) {
        fileWriter.format("\tbeq %s,%s,%s\n", oprnd1, oprnd2, label);
    }

    public void beqz(Temp oprnd1, String label) {
        fileWriter.format("\tbeq %s,$zero,%s\n", oprnd1, label);
    }

    /* USUAL SINGLETON IMPLEMENTATION ... */
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

            // Write the preamble once during initialization
            instance.writePreamble();
        }
    }

    public static MipsGenerator getInstance() {
        return instance;
    }

    private void writePreamble() {
        /* Preamble: error strings + runtime helper functions */
        instance.fileWriter.print(".data\n");
        instance.fileWriter.print("string_access_violation: .asciiz \"Access Violation\"\n");
        instance.fileWriter.print("string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
        instance.fileWriter.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
        instance.fileWriter.print(".text\n");

        /* __strlen: $a0=string ptr -> $v0=length. Uses $s0,$s1 as scratch */
        instance.fileWriter.print("__strlen:\n");
        instance.fileWriter.print("\tmove $s0,$a0\n");
        instance.fileWriter.print("__strlen_loop:\n");
        instance.fileWriter.print("\tlb $s1,0($s0)\n");
        instance.fileWriter.print("\tbeq $s1,$zero,__strlen_done\n");
        instance.fileWriter.print("\taddi $s0,$s0,1\n");
        instance.fileWriter.print("\tj __strlen_loop\n");
        instance.fileWriter.print("__strlen_done:\n");
        instance.fileWriter.print("\tsubu $v0,$s0,$a0\n");
        instance.fileWriter.print("\tjr $ra\n");

        /**
         * __strcpy: $a0=src, $a1=dst -> copies bytes until null, writes null
         * terminator, returns updated dst in $v0. Uses $s0 as scratch.
         */
        instance.fileWriter.print("__strcpy:\n");
        instance.fileWriter.print("__strcpy_loop:\n");
        instance.fileWriter.print("\tlb $s0,0($a0)\n");
        instance.fileWriter.print("\tbeq $s0,$zero,__strcpy_done\n");
        instance.fileWriter.print("\tsb $s0,0($a1)\n");
        instance.fileWriter.print("\taddi $a0,$a0,1\n");
        instance.fileWriter.print("\taddi $a1,$a1,1\n");
        instance.fileWriter.print("\tj __strcpy_loop\n");
        instance.fileWriter.print("__strcpy_done:\n");
        instance.fileWriter.print("\tsb $zero,0($a1)\n");
        instance.fileWriter.print("\tmove $v0,$a1\n");
        instance.fileWriter.print("\tjr $ra\n");
    }
}
