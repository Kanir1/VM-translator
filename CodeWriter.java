import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter out;
    private int jump; 
    public String fileName; 
    private int labelflag = 0;
    

    private String functionLabel;

    public CodeWriter(File outputpath) throws IOException {
        this.jump = 0;
        try {
            FileWriter writer = new FileWriter(outputpath);
            this.out = new BufferedWriter(writer);
        } catch (FileNotFoundException e) {
            System.out.println("Error in file path");
        }
    }

    public void setName(String fileName) {
        if (fileName.indexOf('.') != -1) {
            this.fileName = fileName.substring(0, fileName.indexOf('.'));
        } else {
            this.fileName = fileName;
        }
    }

    public void writeArithmetic(String instruction) throws IOException {
        String template = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n";
        switch (instruction) {
            case "add":
                out.write(template + "M=M+D\n");
                break;
            case "sub":
                out.write(template + "M=M-D\n");
                break;
            case "neg":
                out.write("D=0\n@SP\nA=M-1\nM=D-M\n");
                break;
            case "eq":
                out.write(arithmetic("JNE"));
                jump++;
                break;
            case "gt":
                out.write(arithmetic("JLE"));
                jump++;
                break;
            case "lt":
                out.write(arithmetic("JGE"));
                jump++;
                break;
            case "and":
                out.write(template + "M=M&D\n");
                break;
            case "or":
                out.write(template + "M=M|D\n");
                break;
            case "not":
                out.write("@SP\nA=M-1\nM=!M\n");
                break;
            default:
                throw new IllegalArgumentException("Call writeArithmetic() for a non-arithmetic command");
        }
    }
    
    private String arithmetic(String type) {
        return "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n"
                + "@FALSE" + jump + "\n" + "D;" + type
                + "\n" + "@SP\n" + "A=M-1\n" + "M=-1\n"
                + "@CONTINUE" + jump + "\n" + "0;JMP\n"
                + "(FALSE" + jump + ")\n"
                + "@SP\n" + "A=M-1\n" + "M=0\n"
                + "(CONTINUE" + jump + ")\n";
    }

    public void writePushPop(int command, String segment, int index) throws IOException {
        if (command == 2) {
            if (segment.equals("constant")) {
                out.write("@" + index + "\n" + "D=A\n");
            }

            else if (segment.equals("argument")) {
                out.write("@ARG\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n" + "@addr" + index + "\n"
                        + "A=M\n" + "D=M\n");
            }

            else if (segment.equals("local")) {
                out.write("@LCL\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n" + "@addr" + index + "\n"
                        + "A=M\n" + "D=M\n");
            }

            else if (segment.equals("that")) {
                out.write("@THAT\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n" + "@addr" + index + "\n"
                        + "A=M\n" + "D=M\n");
            }

            else if (segment.equals("this")) {
                out.write("@THIS\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n" + "@addr" + index + "\n"
                        + "A=M\n" + "D=M\n");
            }

            else if (segment.equals("pointer") && index == 0) {
                out.write("@THIS\n" + "D=M\n");
            }

            else if (segment.equals("temp")) {
                out.write("@5\n" + "D=A\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n" + "@addr" + index + "\n"
                        + "A=M\n" + "D=M\n");
            }

            else if (segment.equals("static")) {
                out.write("@" + fileName + "." + index + "\n" + "D=M\n");
            }

            else if (segment.equals("pointer") && index == 1) {
                out.write("@THAT\n" + "D=M\n");
            }

            out.write("@SP\n" + "A=M\n" + "M=D\n");
            out.write("@SP\n" + "M=M+1\n");
        }
        else if (command == 3) {

            if (segment.equals("local")) {
                out.write("@LCL\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n");
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
                out.write("@addr" + index + "\n" + "A=M\n");
            }

            else if (segment.equals("argument")) {
                out.write("@ARG\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n");
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
                out.write("@addr" + index + "\n" + "A=M\n");
            }

            else if (segment.equals("this")) {
                out.write("@THIS\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n");
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
                out.write("@addr" + index + "\n" + "A=M\n");
            }

            else if (segment.equals("that")) {
                out.write("@THAT\n" + "D=M\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n");
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
                out.write("@addr" + index + "\n" + "A=M\n");
            }

            else if (segment.equals("temp")) {
                out.write("@temp\n" + "@5\n" + "D=A\n");
                out.write("@" + index + "\n" + "D=D+A\n" + "@addr" + index + "\n" + "M=D\n");
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
                out.write("@addr" + index + "\n" + "A=M\n");
            }

            else if (segment.equals("pointer") && index == 0) {
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n" + "@THIS\n");

            }

            else if (segment.equals("pointer") && index == 1) {
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n" + "@THAT\n");
            }

            else if (segment.equals("static")) {
                out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
                out.write("@" + fileName + "." + index + "\n");
            }
            out.write("M=D\n");
        } else {
            throw new IllegalArgumentException("not a push or pop command");
        }
    }

    public void writeLabel(String label) {
        try {
            out.write("(" + label + ")\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeGoto(String label) {
        try {
            out.write("@" + label + "\n" + "0;JMP\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeIf(String label) {
        try {
            out.write("@SP\n" + "AM=M-1\n" + "D=M\n");
            out.write("@" + label + "\n" + "D;JNE\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFunction(String functionName, int nVars) {
        this.functionLabel = functionName;
        writeLabel(functionName); 
        for (int i = 0; i < nVars; i++) {
            try {
                out.write("@LCL\n" + "D=M\n" + "@" + i + "\n" + "A=D+A\n" + "M=0\n");
                out.write("@SP\n" + "M=M+1\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeCall(String functionName, int nArgs) {
        try {
            out.write("@" + functionLabel + "$ret." + labelflag + "\n" + "D=A\n");
            out.write("@SP\n" + "A=M\n" + "M=D\n");
            out.write("@SP\n" + "M=M+1\n");
            String[] segment = new String[] { "LCL", "ARG", "THIS", "THAT" };
            for (String seg : segment) {
                out.write("@" + seg + "\n" + "D=M\n");
                out.write("@SP\n" + "A=M\n" + "M=D\n");
                out.write("@SP\n" + "M=M+1\n");
            }
            out.write("@SP\n" + "D=M\n" + "@5\n" + "D=D-A\n" + "@" + nArgs + "\n" + "D=D-A\n" + "@ARG\n" + "M=D\n");
            out.write("@SP\n" + "D=M\n" + "@LCL\n" + "M=D\n");
            writeGoto(functionName);
            writeLabel(functionLabel + "$ret." + labelflag);
            labelflag++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReturn() {
        try {
            out.write("@LCL\nD=M\n@endFrame\nM=D\n");

            out.write("@endFrame\nD=M\n@5\nA=D-A\nD=M\n@retAddr\nM=D\n");

            out.write("@SP\nAM=M-1\nD=M\n");
            out.write("@ARG\nA=M\nM=D\n");
            out.write("@ARG\nD=M+1\n@SP\nM=D\n");
            String[] segments = { "THAT", "THIS", "ARG", "LCL" };
            for (String segment : segments) {
                out.write("@endFrame\nAM=M-1\nD=M\n@" + segment + "\nM=D\n");
            }

            out.write("@retAddr\nA=M\n0;JMP\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * closes the output file
     */
    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInit() {
        try {
            out.write("@256\n" + "D=A\n" + "@SP\n" + "M=D\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeCall("Sys.init", 0);

    }

}
