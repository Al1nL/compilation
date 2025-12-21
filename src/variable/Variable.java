/***********/
/* PACKAGE */
/***********/
package variable;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class Variable extends Comparable
{
	public String name;
    public int scope;
    public boolean equals(Variable other){
        return this.name.equals(other.name) && this.scope==other.scope;
    }
	
}