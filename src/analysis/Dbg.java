package analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Dbg {

    public static boolean ON = true;
    public static PrintWriter out;
    public static StringWriter stringWriter;  // This captures the output as a string

    static {
        try {
            File dir = new File("output");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new RuntimeException("Failed to create output directory");
                }
            }
            // Create StringWriter to capture output
            stringWriter = new StringWriter();
            // Create PrintWriter that writes to BOTH file and string
            out = new PrintWriter(new TeeWriter(
                new FileWriter("output/dfa_debug.txt"),
                stringWriter
            ));
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

    // Call this to get all output as a string
    public static String getOutput() {
        out.flush();
        return stringWriter.toString();
    }

    public static void close() {
        out.flush();
        out.close();
    }
    
    // Helper class that writes to two Writers
    private static class TeeWriter extends java.io.Writer {
        private java.io.Writer fileWriter;
        private java.io.Writer stringWriter;
        
        public TeeWriter(java.io.Writer fileWriter, java.io.Writer stringWriter) {
            this.fileWriter = fileWriter;
            this.stringWriter = stringWriter;
        }
        
        @Override
        public void write(char[] cbuf, int off, int len) throws java.io.IOException {
            fileWriter.write(cbuf, off, len);
            stringWriter.write(cbuf, off, len);
        }
        
        @Override
        public void flush() throws java.io.IOException {
            fileWriter.flush();
            stringWriter.flush();
        }
        
        @Override
        public void close() throws java.io.IOException {
            fileWriter.close();
            stringWriter.close();
        }
    }
}