import java.io.*;

public class NetworkOutputConverter {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[args.length - 1]));
        int packetCounter = 0;
        OutputStream outputStream = new FileOutputStream(String.format("%d.bin", packetCounter));
        String stringBuffer;
        while (br.ready()) {
            stringBuffer = br.readLine();
            if (stringBuffer.startsWith("end")) {
                packetCounter++;
                outputStream.flush();
                outputStream.close();
                outputStream = new FileOutputStream(String.format("%d.bin", packetCounter));
            } else {
                int val = Integer.parseUnsignedInt(stringBuffer, 16);
                outputStream.write(val);
            }
        }
        outputStream.flush();
        outputStream.close();
    }
}
