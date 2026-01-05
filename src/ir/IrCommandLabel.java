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

public class IrCommandLabel extends IrCommand
{
	protected String labelName;
	
	public IrCommandLabel(String labelName)
	{
		this.labelName = labelName;
	}
	public String getLabelName() {
    return labelName;
	}
}
