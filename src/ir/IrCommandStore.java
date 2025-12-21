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

public class IrCommandStore extends IrCommand
{
	String varName;
	Temp src;
	
	public IrCommandStore(String varName, Temp src)
	{
		this.src      = src;
		this.varName = varName;
	}

	@Override
    public Map<Variable, boolean> computeOutSet(Set<Variable> usedAndUninited, Map<Variable, boolean> prevOutSet){ 
		this.inSet = prevOutSet;
		boolean isInited = true;
        for (Variable var : src.dependencySet) {
            if(!prevOutSet.get(var)){
                usedAndUninited.add(var);
				isInited = false;

            }
        }
        this.outSet = new HashMap<>();
        for (Variable var : prevOutSet.keySet()) {

			if(var.name.equals(varName)){//should check if the variable is the same also scope, for now leaving it like that.
				if(isInited){
					outSet.put(var, true);
				}
				else{
					outSet.put(var, false);
				}

			}
			else{
				outSet.put(var, inSet.get(var));
			}
                        
        }
        return outSet;
	}
}
