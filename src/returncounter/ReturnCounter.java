package returncounter;


import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;

public class ReturnCounter {

    private static ReturnCounter instance = null;
    private int count;

    private ReturnCounter() {
        this.count = 0;
    }

    public static ReturnCounter getInstance() {
        if (instance == null) {
            instance = new ReturnCounter();
        }
        return instance;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}