import java.io.*;

public class ConvertTextToBinDiffSeq {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[args.length - 1]));
        OutputStream outputStream = new FileOutputStream("converted.bin");
        String stringBuffer;
        int[] prevValue = new int[3];
        int[] runLength = new int[3];
        prevValue[0] = 256;
        prevValue[1] = 256;
        prevValue[2] = 256;
        boolean isStart = true;
        while (br.ready()) {
            stringBuffer = br.readLine();
            String[] sarr = stringBuffer.split(" ");
            for (int i = 0; i < 3; i++) {
                int val = Integer.parseUnsignedInt(sarr[i], 16);
                if (isStart) {
                    outputStream.write(255);
                    outputStream.write(val);
                    prevValue[i] = val;
                    if (i == 2) isStart = false;
                } else if (prevValue[i] == val){
                    runLength[i]++;
                    if (runLength[i] == 31) {
                        int writeRun = 192;
                        writeRun |= runLength[i];
                        outputStream.write(writeRun);
                        runLength[i] = 0;
                    }
                } else if ((prevValue[i] >> 5) == (val >> 5)) { // if possible to describe using difference - do it
                    if (runLength[i] >= 1 && runLength[i] <= 31){
                        int writeRun = 192;
                        writeRun |= runLength[i];
                        outputStream.write(writeRun);
                    }
                    int diff = val - prevValue[i]; // shift
                    int valToWrite = 128;
                    for (int j = 0; j < 5; j++) {
                        if (((diff >> j) & 0x01) == 1) {
                            valToWrite += (1 << j);
                        }
                    }
                    outputStream.write(valToWrite);
                    prevValue[i] = val;
                    runLength[i] = 0;
                } else {
                    if (runLength[i] >= 1 && runLength[i] <= 31){
                        int writeRun = 192;
                        writeRun |= runLength[i];
                        outputStream.write(writeRun);
                    }
                    outputStream.write(255);
                    outputStream.write(val);
                    prevValue[i] = val;
                    runLength[i] = 0;
                }
                //outputStream.write(val);
            }
        }
        outputStream.flush();
        outputStream.close();
        br.close();
    }

    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
}
