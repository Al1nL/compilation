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
	private IrCommand head=null;
	private IrCommandList tail=null;

	/******************/
	/* Add Ir command */
	/******************/
	
public void AddIrCommand(IrCommand cmd) {
    // Check if the command is a load/store of a global variable
    if (isGlobalVarCommand(cmd)) {
        // Insert at beginning
        if (head == null && tail == null) {
            // empty list
            head = cmd;
        } else {
            // Move current head into tail list
            IrCommandList newTail;
            if (tail == null) {
                newTail = new IrCommandList(head, null);
            } else {
                newTail = new IrCommandList(head, tail);
            }
            head = cmd;
            tail = newTail;
        }
    } else {
        // Append at end (original behavior)
        if (head == null && tail == null) {
            head = cmd;
        } else if (head != null && tail == null) {
            tail = new IrCommandList(cmd, null);
        } else {
            IrCommandList it = tail;
            while (it.tail != null) {
                it = it.tail;
            }
            it.tail = new IrCommandList(cmd, null);
        }
    }
}

// Helper method to check if an IrCommand is a store/load of a global variable
private boolean isGlobalVarCommand(IrCommand cmd) {
    if (cmd instanceof IrCommandLoad load) {
        return load.var.isGlobal;
    }
    if (cmd instanceof IrCommandStore store) {
        return store.var.isGlobal;
    }
    return false;
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
	public List<IrCommand> getCommands() {
    List<IrCommand> result = new ArrayList<>();
    if (head != null) {
        result.add(head);
    }

    IrCommandList curr = tail;
    while (curr != null) {
        result.add(curr.head);
        curr = curr.tail;
    }

    return result;
}

}
