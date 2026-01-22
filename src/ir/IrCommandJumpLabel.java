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

	@Override
    public Set<Temp> computeInSet(Set<Temp> out) {
        return generalComputeInSet(out);
    }

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		MipsGenerator.getInstance().jump(labelName);
	}
}
