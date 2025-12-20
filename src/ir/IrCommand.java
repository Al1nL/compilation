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

public abstract class IrCommand
{
	/*****************/
	/* Label Factory */
	/*****************/
	protected static int labelCounter = 0;
	private Map<Variable, boolean> inSet;
	private Map<Variable, boolean> outSet;

	public    static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s", labelCounter++,msg);
	}

	//usedAndUninited is the set of variables used before initialized
	
	public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet){ 
		return null;
	}
}
