package ir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;

public class IrCommandCallFunc extends IrCommand {

    public Temp dst;         // receives return value ($v0)
    public String label;     // function name / label to call
    public ArrayList<Temp> args;

    public IrCommandCallFunc(Temp dst, String label, ArrayList<Temp> args) {
        this.dst   = dst;
        this.label = label;
        this.args  = args;
    }

    @Override
    public Set<Temp> getUseTemps() {
        Set<Temp> use = new HashSet<>();
        if (args != null) {
            for (Temp a : args) {
                if (a != null) use.add(a);
            }
        }
        return use;
    }

    @Override
    public Set<Temp> getDefTemps() {
        Set<Temp> def = new HashSet<>();
        if (dst != null) def.add(dst);
        return def;
    }

    @Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().callFunc(dst, label, args);
    }
}