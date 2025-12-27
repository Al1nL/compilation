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
import variable.Variable;
import java.util.*;
public class IrCommandLoad extends IrCommand
{
	Temp dst;
    public final Variable var;
	
	public IrCommandLoad(Temp dst,Variable var)
	{
		this.dst = dst;
		this.var = var;
	}

	@Override
    public Map<Variable, Boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, Boolean> prevOutSet){ 
    	
		Map<Variable, Boolean> outSet = new HashMap<>(prevOutSet);

		if (this.var != null) {
			boolean isInitialized = prevOutSet.getOrDefault(this.var, false);
			if (!isInitialized) {
				usedAndUninited.add(this.var);
			}
		}

		if (this.dst != null) {
			outSet.put(this.var, true);
		}

		return outSet;
	}
}
