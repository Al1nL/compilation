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
    public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet) {
        this.inSet = prevOutSet;
        for (Variable var : dst.dependencySet) {
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
