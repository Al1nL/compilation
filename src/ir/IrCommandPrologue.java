package ir;

import java.util.Set;
import mips.MipsGenerator;
import temp.Temp;

public class IrCommandPrologue extends IrCommand {

    private final String functionName;
    private final int localVarCount;

    public IrCommandPrologue(String functionName, int localVarCount) {
        this.functionName = functionName;
        this.localVarCount = localVarCount;
    }

    public String getFunctionName() {
        return functionName;
    }

    // Prologue doesn't use or define temps
    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().startFunction();
        MipsGenerator.getInstance().emitPrologue(functionName, localVarCount);
    }
}