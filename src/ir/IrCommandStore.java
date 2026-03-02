package ir;

import java.util.*;
import mips.MipsGenerator;
import temp.Temp;
import variable.Variable;

public class IrCommandStore extends IrCommand {

    public final Variable var;

    Temp src;

    public IrCommandStore(Variable var, Temp src) {
        this.var = var;
        this.src = src;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (src != null) {
            use.add(src); // The temp being stored

                }return use;
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /* MIPS me !!! */
    public void mipsMe() {

        if (var.isGlobal) {
            MipsGenerator.getInstance().storeGlobal(var.name, src);
        }
        else if(this.allocatedObject!=null){
            MipsGenerator.getInstance().storeToPointer(src, this.allocatedObject.dst, (1+var.offset)*4); //store in object's field
        }
         else {
            int offset = var.offset < 0
                    ? -4 * var.offset + 4       // arguments: above $fp (positive offsets) - unchanged
                    : -(4 * var.offset + 44);   // locals: skip 40 bytes of saved $t registers
            MipsGenerator.getInstance().storeLocal(offset, src);
        }
    }

}
