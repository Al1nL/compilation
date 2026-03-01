package ir;

import java.util.Set;
import mips.MipsGenerator;
import temp.Temp;

public class IrCommandEpilogue extends IrCommand {

    private final String functionName;

    public IrCommandEpilogue(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    // Epilogue doesn't use or define temps
    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().emitEpilogue(functionName);
    }
}