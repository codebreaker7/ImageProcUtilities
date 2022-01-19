import java.io.*;
import java.util.Scanner;

public class ConvertTextFile {
    public static void main(String[] args) throws IOException {
        char[] buf = new char[11];
        int off = 0;
        PrintWriter writer = new PrintWriter(new File("formatted_dataout.txt"));
        BufferedReader reader = new BufferedReader(new FileReader("indata.txt"));
        while (reader.read(buf, 0, buf.length)!= -1) {
            off += 11;
            writer.write(buf);
            writer.write("\n");
            reader.read(buf, 0, 1);
        }
        reader.close();
        writer.close();
    }
}
