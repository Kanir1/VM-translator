import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main
 */
public class Main {

    public static void main(String[] args) throws IOException {
        File outputFile;
        String outputPath = "";
        String inputFile = args[0];
        File input = new File(inputFile);
        CodeWriter write = null;
        ArrayList<File> vmFiles = new ArrayList<File>();
        if (args.length != 1) {
            System.out.println("No file inserted");
            return;
        } else {
            // Checks if we received only one file.
            if (input.isFile()) {
                String path = input.getAbsolutePath();
                if (!path.substring(path.lastIndexOf('.')).equals(".vm")) {
                    throw new IllegalArgumentException(".vm file is required!");
                }
                vmFiles.add(input);
                outputPath = input.getAbsolutePath().substring(0, input.getAbsolutePath().lastIndexOf(".")) + ".asm";
                outputFile = new File(outputPath);
                try {
                    write = new CodeWriter(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                createFile(vmFiles, write);
            }
            //checks if directory path
            else if (input.isDirectory()) {
                vmFiles = findVMfiles(input);
                if (vmFiles.size() == 0) {
                    throw new IllegalArgumentException("No vm file in this directory");
                }

                // Check if Sys.vm exists
                boolean sysVmExists = vmFiles.stream().anyMatch(file -> file.getName().equals("Sys.vm"));

                outputPath = input.getAbsolutePath() + "/" + input.getName() + ".asm";
                outputFile = new File(outputPath);
                try {
                    write = new CodeWriter(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (sysVmExists) {
                    write.writeInit();
                }

                createFile(vmFiles, write);
                write.setName(outputPath);
            }
        }
        write.close();
    }

    public static void createFile(ArrayList<File> vmFiles, CodeWriter writer) throws IOException {
        for (File f : vmFiles) {
            writer.setName(f.getName());
            Parser parser = new Parser(f);
            int type = -1;
            while (parser.hasMoreLines()) {
                parser.advance();
                type = Parser.commandType();
                switch (type) {
                    case 1:
                        writer.writeArithmetic(parser.arg1());
                        break;
                    case 2:
                    case 3:
                        writer.writePushPop(type, parser.arg1(), parser.arg2());
                        break;
                    case 4:
                        writer.writeLabel(parser.arg1());
                        break;
                    case 5:
                        writer.writeGoto(parser.arg1());
                        break;
                    case 6:
                        writer.writeIf(parser.arg1());
                        break;
                    case 7:
                        writer.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    case 8:
                        writer.writeReturn();
                        break;
                    case 9:
                        writer.writeCall(parser.arg1(), parser.arg2());
                        break;
                    default:
                        // Handle unknown command type
                        break;
                }
            }
        }
    }

    //iterates only over files that end with .vm
    public static ArrayList<File> findVMfiles(File directory) {
        File[] files = directory.listFiles();
        ArrayList<File> result = new ArrayList<File>();
        for (File f : files) {
            if (f.getName().endsWith(".vm")) {
                result.add(f);
            }
        }
        return result;
    }

}
