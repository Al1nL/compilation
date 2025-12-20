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

public class IrCommandParam extends IrCommand {

    private final String type;
    private final String name;
    private final Temp temp;

    public IrCommandParam(String type, String name, Temp temp) {
        this.type = type;
        this.name = name;
        this.temp = temp;
    }
}
