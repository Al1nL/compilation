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
import mips.MipsGenerator;

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

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().label(labelName);
	}
}
