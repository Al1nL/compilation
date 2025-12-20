/***********/
/* PACKAGE */
/***********/
package ir;

import ast.*;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

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
