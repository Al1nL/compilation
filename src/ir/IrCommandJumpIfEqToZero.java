/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;
import mips.MipsGenerator;
import temp.*;

public class IrCommandJumpIfEqToZero extends IrCommandJumpLabel
{
	Temp t;
	
	public IrCommandJumpIfEqToZero(Temp t, String labelName)
	{
		super(labelName);
		this.t = t;
	}

	@Override
	public Set<Temp> getUseTemps() {
		Set<Temp> use = new HashSet<>();
		if (t != null) use.add(t); // temp being tested
		else System.err.println("IrCommandJumpIfEqToZero: null temp");
		return use;
	}


	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().beqz(t, labelName);
	}

}
