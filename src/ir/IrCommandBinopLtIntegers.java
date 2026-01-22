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


public class IrCommandBinopLtIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;

	public IrCommandBinopLtIntegers(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}


	@Override
	public Set<Temp> getUseTemps() {
		Set<Temp> use = new HashSet<>();
		if (t1 != null) use.add(t1);
		if (t2 != null) use.add(t2);
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
	public void mipsMe()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String labelEnd        = getFreshLabel("end");
		String labelAssignOne  = getFreshLabel("AssignOne");
		String labelAssignZero = getFreshLabel("AssignZero");
		
		/******************************************/
		/* [2] if (t1< t2) goto labelAssignOne;  */
		/*     if (t1>=t2) goto labelAssignZero; */
		/******************************************/
		MipsGenerator.getInstance().blt(t1,t2,labelAssignOne);
		MipsGenerator.getInstance().bge(t1,t2,labelAssignZero);

		/************************/
		/* [3] labelAssignOne: */
		/*                      */
		/*         t3 := 1      */
		/*         goto end;    */
		/*                      */
		/************************/
		MipsGenerator.getInstance().label(labelAssignOne);
		MipsGenerator.getInstance().li(dst,1);
		MipsGenerator.getInstance().jump(labelEnd);

		/*************************/
		/* [4] labelAssignZero: */
		/*                       */
		/*         t3 := 1       */
		/*         goto end;     */
		/*                       */
		/*************************/
		MipsGenerator.getInstance().label(labelAssignZero);
		MipsGenerator.getInstance().li(dst,0);
		MipsGenerator.getInstance().jump(labelEnd);

		/******************/
		/* [5] labelEnd: */
		/******************/
		MipsGenerator.getInstance().label(labelEnd);
	}
}
