package ir;

import ast.AstDecVar;
import java.util.*;

public class Ir
{

	private List<IrCommand> globalInit = new ArrayList<>();
    private List<IrCommand> mainCommands = new ArrayList<>();
	public static String curClass;
	public static String currentMethodClass = null; // Tracks which class we are currently generating a method body for (null for free functions)
	public static Map<String, Map<String, String>> methodLabelsMap = new HashMap<>(); 	// Maps className -> methodName -> mangled MIPS label, populated during AstDecClass.irMe
	public static int curField = -1; //field offset
	public static Map<String, Map<Integer, List<IrCommand>>> fieldInitIrCommands = new HashMap<>();
	public static Map<String, List<AstDecVar>> classFieldDecls = new HashMap<>(); // flat list of all AstDecVar nodes (including inherited) per class
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

	/***************/
	/* MIPS me !!! */
	/***************/
	public void mipsMe()
	{
		//complete here because provided code assumed linked list implementation.
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