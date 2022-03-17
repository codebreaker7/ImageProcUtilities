import java.io.*;

public class ConvertTextToBin {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[args.length - 1]));
        OutputStream outputStream = new FileOutputStream("converted.bin");
        String stringBuffer;
        while (br.ready()) {
            stringBuffer = br.readLine();
            for (String s: stringBuffer.split(" ")) {
                int val = Integer.parseUnsignedInt(s, 16);
                outputStream.write(val);
            }
        }
        outputStream.flush();
        outputStream.close();
        br.close();
    }
}
