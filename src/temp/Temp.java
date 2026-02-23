/***********/
/* PACKAGE */
/***********/
package temp;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.*;
import variable.Variable;
/*******************/
/* PROJECT IMPORTS */
/*******************/

public class Temp
{
	private int serial=0;
	public Set<Variable> dependencySet; //set of all high level variables the temp is depended on.
	private String physicalReg = null; // ADD THIS

	public Temp(int serial)
	{
		this.serial = serial;
		dependencySet = new HashSet<>();
	}
	
	public int getSerialNumber()
	{
		return serial;
	}

	public void setPhysicalReg(String reg) { this.physicalReg = reg; }
    public String getPhysicalReg() { return physicalReg; }
    public boolean isPhysical() { return physicalReg != null; }

	@Override
	public String toString() {
		return physicalReg != null ? physicalReg : "t" + serial;
	}
}
