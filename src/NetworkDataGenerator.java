import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class NetworkDataGenerator {
    public static void main(String[] args) throws FileNotFoundException {
        int NUM_DATA = 10;
        if (args.length == 1) {
            NUM_DATA = Integer.parseInt(args[args.length-1]);
        }
        Random random = new Random();

        PrintWriter writer = new PrintWriter(new File("ipmask.txt"));
        writer.write(String.format("%d\n", NUM_DATA));
        for (int i = 0; i < NUM_DATA; i++) {
            for (int j = 0; j < 14; j++) {

                int temp = 0;
                if (j <= 12) {
                    temp = random.nextInt();
                } else {
                    temp = random.nextInt(255);
                    temp <<= 24;
                }
                writer.write(String.format("%08X\n", temp));
            }
        }
        writer.flush();
        writer.close();
    }
}
