/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.HashSet;
import java.util.Set;
import mips.MipsGenerator;
import temp.*;

public class IrCommandPrintString extends IrCommand
{
	Temp t;
	
	public IrCommandPrintString(Temp t)
	{
		this.t = t;
	}

	@Override
	public Set<Temp> getUseTemps() {
		Set<Temp> use = new HashSet<>();
		if (t != null) use.add(t);
		return use;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().printString(t);
	}
}
