package ir;

import ast.*;
import java.util.Map;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;

public class IrCommandDeclareClass extends IrCommand
{
    public String className;
    public String parentName;
    public AstDecList fields;
    public Map<String,Integer> methodOffsets;
    public Map<String,String> methodLabels;
    public int fieldCount;

    public IrCommandDeclareClass(
        String className,
        String parentName,
        AstDecList fields, 
        Map<String,Integer> methodOffsets,
        int fieldCount,
        Map<String,String> methodLabels)
    {
        this.className  = className;
        this.parentName = parentName;
        this.fields     = fields;
        this.methodOffsets = methodOffsets;
        this.methodLabels = methodLabels;
        this.fieldCount = fieldCount;
    }
    // Class declaration doesn't use or define temps
    
    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().declareClass(className, methodOffsets, fieldCount, methodLabels);
    }

}
