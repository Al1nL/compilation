package ast;

// A node representing a list of declarations (program)
public class AstProgram extends AstExp { 
    public AstDecList decs; // list of all declarations in the program

    public AstProgram(AstDecList decs) {
        this.decs = decs;
    }
}
