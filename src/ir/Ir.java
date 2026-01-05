/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.*;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class Ir
{

private List<IrCommand> globalInit = new ArrayList<>();
    private List<IrCommand> mainCommands = new ArrayList<>();

    private boolean buildingGlobals = true;

    public void switchToMain() {
        buildingGlobals = false;
    }

    public void AddIrCommand(IrCommand cmd) {
        if (buildingGlobals)
            globalInit.add(cmd);
        else
            mainCommands.add(cmd);
    }

    public List<IrCommand> getCommands() {
        List<IrCommand> all = new ArrayList<>();
        all.addAll(globalInit);
        all.addAll(mainCommands);
        return all;
    }

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static Ir instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected Ir() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static Ir getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new Ir();
		}
		return instance;
	}

}
