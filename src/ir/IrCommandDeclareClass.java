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

public class IrCommandDeclareClass extends IrCommand
{
    public String className;
    public String parentName;
    public AstDecList fields;

    public IrCommandDeclareClass(
        String className,
        String parentName,
        AstDecList fields)
    {
        this.className  = className;
        this.parentName = parentName;
        this.fields     = fields;
    }
}
