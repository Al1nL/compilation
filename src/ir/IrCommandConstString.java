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
import java.util.HashSet;
import java.util.Set;
import temp.*;

public class IrCommandConstString extends IrCommand
{
	Temp t;
	String value;
	
	public IrCommandConstString(Temp t, String value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
	public Set<Temp> getDefTemps() {
		Set<Temp> def = new HashSet<>();
		if (t != null) def.add(t);
		return def;
	}

	@Override
	public Set<Temp> computeInSet(Set<Temp> out) {
		return generalComputeInSet(out);
	}

    @Override
    public void mipsMe() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
