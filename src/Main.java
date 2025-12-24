import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import analysis.*;
import java.util.*;
import variable.Variable;

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
                /**********************/
                /* [8] IR the AST ... */
                /**********************/
                ast.irMe();
                /* Finalize AST GRAPHIZ DOT file */
                AstGraphviz.getInstance().finalizeFile();
                //fileWriter.print("OK");
                System.out.println("finished semantics, moving to ir");



            /* ---------------------------------
             * Build CFG
             * --------------------------------- */
            CFGBuilder builder = new CFGBuilder();
            List<IrCommand> ir = Ir.getInstance().getCommands();
            List<CFGNode> cfg = builder.build(ir);

            /* ---------------------------------
             * Run data-flow analysis
             * --------------------------------- */
            Set<Variable> errors = DataFlowAnalyzer.analyze(cfg,builder.getAllVariables());

            /* ---------------------------------
             * Print required output
             * --------------------------------- */
            if (errors.isEmpty()) {
                fileWriter.println("!OK");
            } else {
                errors.stream()
                      .map(v -> v.name)
                      .distinct()
                      .sorted()
                      .forEach(fileWriter::println);
            }
            System.out.println("finished ir");

            } catch (Error le) {
                // lexical error
                fileWriter.print("ERROR");
            } catch (Exception e) {
                // syntax\semantic error with location
                fileWriter.print(e.getMessage());
                e.printStackTrace();
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
