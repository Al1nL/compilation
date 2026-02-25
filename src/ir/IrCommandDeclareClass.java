/***********/
/* PACKAGE */
/***********/
package ir;

import ast.*;
import java.util.Set;
import temp.*;

public class IrCommandDeclareClass extends IrCommand
{
    public String className;
    public String parentName;
    public AstDecList fields;

    public IrCommandDeclareClass(
        String className,
        String parentName,
        AstDecList fields)
    {
        this.className  = className;
        this.parentName = parentName;
        this.fields     = fields;
    }
    // Class declaration doesn't use or define temps
    
    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    @Override
    public void mipsMe() {
    }

}
