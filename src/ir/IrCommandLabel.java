/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import java.util.Set;
import mips.MipsGenerator;
import temp.Temp;

public class IrCommandLabel extends IrCommand
{
	 String labelName;
	
	public IrCommandLabel(String labelName)
	{
		this.labelName = labelName;
	}
	public String getLabelName() {
    return labelName;
	}
	
	// jump doesn't or use define temps
	
	@Override
	public Set<Temp> computeInSet(Set<Temp> out) {
		return generalComputeInSet(out);
	}


	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().label(labelName);
	}
}
