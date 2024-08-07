import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    private Scanner scan;
    private String current;
    private String args1; 
    private int args2; 
    private static int indexType;
    private static final HashMap<Integer, String> Arithmetic = new HashMap<Integer, String>();
    static {
        Arithmetic.put(1, "add");
        Arithmetic.put(2, "sub");
        Arithmetic.put(3, "neg");
        Arithmetic.put(4, "eq");
        Arithmetic.put(5, "gt");
        Arithmetic.put(6, "lt");
        Arithmetic.put(7, "and");
        Arithmetic.put(8, "or");
        Arithmetic.put(9, "not");
    }
    private static final HashMap<Integer, String> commandType = new HashMap<Integer, String>();
    static {
        commandType.put(1, "C_ARITHMETIC");
        commandType.put(2, "C_PUSH");
        commandType.put(3, "C_POP");
        commandType.put(4, "C_LABEL");
        commandType.put(5, "C_GOTO");
        commandType.put(6, "C_IF");
        commandType.put(7, "C_FUNCTION");
        commandType.put(8, "C_RETURN");
        commandType.put(9, "C_CALL");
    }

    public Parser(File input) {
        try {
            this.scan = new Scanner(input);
            String curFile = "";
            String nextLine = "";
            while (scan.hasNext()) {
                nextLine = scan.nextLine();
                int pos = nextLine.indexOf("//");
                if (pos != -1) {
                    nextLine = nextLine.substring(0, pos);
                }
                nextLine = nextLine.trim();
                if (!nextLine.equals("") && !nextLine.startsWith("/*") && !nextLine.startsWith("*")) {
                    curFile += nextLine + "\n";
                }
            }
            this.scan = new Scanner(curFile.trim());
        }

        catch (FileNotFoundException e) {
            System.out.println("file not found");
        }

    }
    public boolean hasMoreLines() {
        return scan.hasNextLine();
    }

    public void advance() {
        if (hasMoreLines()) {
            this.current = this.scan.nextLine();
            this.args1 = "";
            this.args2 = -1;
            String[] segment = this.current.split(" ");
            if (segment.length > 3) {
                throw new IllegalArgumentException("too many instructions");
            }
            if (Arithmetic.containsValue(segment[0])) {
                indexType = 1;
                args1 = segment[0];
            } else if (segment[0].equals("return")) {
                indexType = 8;
                args1 = segment[0];
            } else {
                args1 = segment[1];
                if (segment[0].equals("push")) {
                    indexType = 2;
                } else if (segment[0].equals("pop")) {
                    indexType = 3;
                } else if (segment[0].equals("label")) {
                    indexType = 4;
                } else if (segment[0].equals("goto")) {
                    indexType = 5;
                } else if (segment[0].equals("if-goto")) {
                    indexType = 6;
                } else if (segment[0].equals("function")) {
                    indexType = 7;
                } else if (segment[0].equals("call")) {
                    indexType = 9;
                } else {
                    throw new IllegalArgumentException("Unknown Command Type");
                }
            }
            if (indexType == 2 || indexType == 3 || indexType == 7 || indexType == 9) {
                try {
                    args2 = Integer.parseInt(segment[2]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("instruction argument is not an integer");
                }
            }

        }
    }

    public static int commandType() {
        if (indexType != -1) {
            return indexType;
        } else {
            throw new IllegalStateException("no instruction");
        }
    }

    public String arg1() {
        if (indexType == 8) {
            throw new IllegalStateException("error in getting arg1");
        }
        return this.args1;
    }

    public int arg2() {
        if (indexType == 2 || indexType == 3 || indexType == 7 || indexType == 9) {
            return this.args2;
        } else {
            throw new IllegalStateException("error in getting arg2");
        }
    }
}