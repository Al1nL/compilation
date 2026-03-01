import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import analysis.*;
import java.util.*;
import mips.MipsGenerator;
import regalloc.*;
import temp.Temp;

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

                ast.semantMe();
                /* [8] IR the AST ... */
                System.out.println("finished semantics, moving to offseting");
                ast.offsetMe(null, 0, null, new HashMap<>(), new HashMap<>(), new HashMap<>());
                System.out.println("finished offsetting");

                ast.irMe();

                /* Finalize AST GRAPHIZ DOT file */
                AstGraphviz.getInstance().finalizeFile();

                /* ---------------------------------
                * Build CFG
                * --------------------------------- */
                CFGBuilder builder = new CFGBuilder();
                List<IrCommand> ir = Ir.getInstance().getCommands();
                List<CFGNode> cfg = builder.build(ir);
                System.out.println("finished ir");

                /* ---------------------------------
                * Run data-flow analysis
                * --------------------------------- */
                List<CFGNode> annotatedCfg = Analyzer.analyze(cfg, builder.getAllVariables());
                System.out.println("finished data flow analysis");

                /* ---------------------------------
                * Build Interference Graph 
                * --------------------------------- */
                InterferenceGraph ig = regalloc.InterferenceGraphBuilder.build(annotatedCfg);
                Dbg.p(ig.toString()); // print the graph for debugging
                System.out.println("finished building interference graph");

                /* ---------------------------------
                * Register Allocation
                * --------------------------------- */
                try {
                    Map<Temp, String> allocation = regalloc.RegisterAllocator.allocateRegisters(ig);
                    regalloc.RegisterAllocator.printAllocation(allocation);
                    System.out.println("finished register allocation process");

                    /* ---------------------------------
                    * Substitute temps with allocated registers
                    * --------------------------------- */
                    regalloc.RegisterSubstitution.apply(annotatedCfg, allocation);
                    System.out.println("finished register substitution");
                } 
                catch (RuntimeException re) {
                    fileWriter.print(re.getMessage());
                    fileWriter.close();
                    return;
                }
                /* ---------------------------------
                * Generate MIPS code
                * --------------------------------- */
                MipsGenerator.init(outputFileName);
                for (CFGNode node : annotatedCfg) {
                    node.cmd.mipsMe();
                }
                MipsGenerator.getInstance().finalizeFile();
            }
            catch (Error le) {
                // lexical error
                fileWriter.print("ERROR");
            }
            catch (Exception e) {
                // syntax\semantic error with location
                fileWriter.print(e.getMessage());
            }
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
