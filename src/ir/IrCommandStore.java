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
        if (src != null) use.add(src); // The temp being stored
        return use;
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }
    /***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{

        if (var.isGlobal){
			MipsGenerator.getInstance().storeGlobal(var.name, src);
		}
		else{
			int offset = var.offset<0? -4*var.offset+4 : -4*var.offset;
			MipsGenerator.getInstance().storeLocal(offset, src);
		}
	}

}
