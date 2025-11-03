
import java.io.*;
import java.io.PrintWriter;

import java_cup.runtime.Symbol;

public class Main {

    static final String[] TOKEN_NAMES = {
        "EOF", "PLUS", "MINUS", "TIMES", "DIVIDE", "LPAREN", "RPAREN",
        "LBRACK", "RBRACK", "LBRACE", "RBRACE", "ASSIGN", "EQ", "LT", "GT",
        "COMMA", "DOT", "SEMICOLON", "ARRAY", "CLASS", "RETURN", "WHILE",
        "IF", "ELSE", "NEW", "EXTENDS", "NIL", "TYPE_INT", "TYPE_STRING",
        "TYPE_VOID", "INT", "STRING", "ID"
    };

    static public void main(String argv[]) {
        Lexer l;
        Symbol s;
        FileReader fileReader;
        PrintWriter fileWriter;
        String inputFileName = argv[0];
        String outputFileName = argv[1];
        try {
                            /* [2] Initialize a file writer */

            fileWriter = new PrintWriter(outputFileName);

            try {
                /**
                 * *****************************
                 */
                /* [1] Initialize a file reader */
                /**
                 * *****************************
                 */
                fileReader = new FileReader(inputFileName);

                /* [3] Initialize a new lexer */
                l = new Lexer(fileReader);

                /* [4] Read next token */

                s = l.next_token();

                /* [5] Main reading tokens loop */
                while (s.sym != TokenNames.EOF) {
                    /* Print to file */
                    fileWriter.print(TOKEN_NAMES[s.sym]);
                    if (s.sym >= 30) // INT\ID\STRING
                    {
                        fileWriter.print("(" + s.value + ")");
                    }
                    fileWriter.print(l.getPos());

                    /* Read next token */
                    s = l.next_token();
                    if (s.sym != TokenNames.EOF) {
                        fileWriter.print("\n");
                    }
                }

                /* Close lexer input file */
                l.yyclose();

                /* Close output file */
                fileWriter.close();

            } catch (Error e) {
                fileWriter.close();
                try (PrintWriter errorWriter = new PrintWriter(outputFileName)) { // this truncates the file
                    errorWriter.print("ERROR");
                } 
                catch (Exception x) {
                    System.err.println("Couldn't open output file1");
                }
                // debug
                // fileWriter.print("error:"+e.getMessage());
                // fileWriter.close();
            }
        } catch (Exception e) {
            System.err.println("Couldn't open output file");
        }
    }
}
