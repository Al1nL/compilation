package ast;
import types.*;

public class AstExpString extends AstExp {
    public final String value;

    public AstExpString(String value) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.value = value;
    }

@Override
public void printMe() {
    System.out.println("STRING: " + value);

    // Escape for Graphviz
    String clean = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"");

    AstGraphviz.getInstance().logNode(
        serialNumber,
        String.format("String(%s)", clean)
    );
}

public Type semantMe()
	{
		return TypeString.getInstance();
	}

}
