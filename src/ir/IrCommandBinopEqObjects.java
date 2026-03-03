package ir;

import mips.MipsGenerator;
import temp.Temp;

/* Compares two object pointers for identity (same object in memory) */
public class IrCommandBinopEqObjects extends IrCommand {

    Temp dst;
    Temp t1;
    Temp t2;

    public IrCommandBinopEqObjects(Temp dst, Temp t1, Temp t2) {
        this.dst = dst;
        this.t1  = t1;
        this.t2  = t2;
    }

    @Override
    public void mipsMe() {
        MipsGenerator.getInstance().eqPointers(dst, t1, t2);
    }
}