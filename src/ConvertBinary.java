import java.io.*;

public class ConvertBinary {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("No input file");
            return;
        }
        RandomAccessFile inputFile = new RandomAccessFile(args[args.length-1], "r");
        OutputStream outputStream = new FileOutputStream("output.bin");
        long origin = inputFile.getFilePointer();
        int val;
        while ((val = inputFile.read()) != -1) {
            outputStream.write(val);
            inputFile.skipBytes(2);
        }

        inputFile.seek(origin); inputFile.skipBytes(1);
        while ((val = inputFile.read()) != -1) {
            outputStream.write(val);
            inputFile.skipBytes(2);
        }
        inputFile.seek(origin); inputFile.skipBytes(2);
        while ((val = inputFile.read()) != -1) {
            outputStream.write(val);
            inputFile.skipBytes(2);
        }
    }
}
