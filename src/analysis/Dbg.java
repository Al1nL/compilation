package analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Dbg {

    public static boolean ON = true;
    public static PrintWriter out;

    static {
        try {
            File dir = new File("output");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new RuntimeException("Failed to create output directory");
                }
            }
            out = new PrintWriter(new FileWriter("output/dfa_debug.txt"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void p(String s) {
        if (!ON) {
            return;
        }
        out.println(s);
        out.flush();
    }

    public static void close() {
        out.flush();
        out.close();
    }
}
