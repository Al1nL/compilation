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

public class IrCommandJumpLabel extends IrCommand
{
		protected String labelName;

	public IrCommandJumpLabel(String labelName)
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
		MipsGenerator.getInstance().jump(labelName);
	}
}
