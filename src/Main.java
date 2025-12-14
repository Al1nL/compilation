import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;

public class Main {

    static public void main(String argv[]) {
        Lexer l;
        Parser p;
        Symbol s;
        AstNode ast;
        FileReader fileReader;
        PrintWriter fileWriter;
        String inputFileName = argv[0];
        String outputFileName = argv[1];

        try {
            /* Initialize a file reader */
            fileReader = new FileReader(inputFileName);

            /* Initialize a file writer */
            fileWriter = new PrintWriter(outputFileName);

            /* Initialize a new lexer */
            l = new Lexer(fileReader);

            /* [4] Initialize a new parser */
            p = new Parser(l);

            /* [5] 3 ... 2 ... 1 ... Parse !!! */
            try {
                ast = (AstNode) p.parse().value;

                /* [6] Print the AST ... */
                // ast.printMe();
                ast.semantMe();
                /* Finalize AST GRAPHIZ DOT file */
                AstGraphviz.getInstance().finalizeFile();
                fileWriter.print("OK");
                System.out.println("finished,Check output file");

            } catch (Error le) {
                // lexical error
                fileWriter.print("ERROR");
            } catch (Exception e) {
                // syntax\semantic error with location
                fileWriter.print(e.getMessage());
                // e.printStackTrace();
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
