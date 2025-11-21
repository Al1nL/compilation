package ast;

// A node representing a list of declarations (program)
public class AstProgram extends AstNode {
    public AstDecList decs;

    public AstProgram(AstDecList decs) {
        this.serialNumber = AstNodeSerialNumber.getFresh();

        System.out.print("====================== program -> decs\n");

        this.decs = decs;
    }

    public void printMe() {
        System.out.print("AST NODE PROGRAM\n");

        if (decs != null) decs.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "PROGRAM\n");

        if (decs != null)
            AstGraphviz.getInstance().logEdge(serialNumber, decs.serialNumber);
    }
}

