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
		return prevOutSet;
	}
	

	//general function to check using initialized and doing nothing else.
    public Map<Variable, boolean> generalComputeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet, Temp t) {
        this.inSet = prevOutSet;
        for (Variable var : t.dependencySet) {
            if(!prevOutSet.get(var)){
                usedAndUninited.add(var);

            }
        }
        this.outSet = new HashMap<>();
        for (Variable var : prevOutSet.keySet()) {
            
            outSet.put(var, inSet.get(var));
        }
        return outSet;

        
    }
}
