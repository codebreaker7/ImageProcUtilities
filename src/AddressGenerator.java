import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AddressGenerator {
    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
        int ADDR_NUM = 20;
        if (args.length == 1) {
            ADDR_NUM = Integer.parseInt(args[args.length-1]);
        }
        Random random = new Random();

        ArrayList<InetAddress> addresses = new ArrayList<>();
        byte[] ipval = new byte[4];
        for (int i = 0; i < ADDR_NUM; i++) {
            random.nextBytes(ipval);
            InetAddress tempAddress = InetAddress.getByAddress(ipval);
            addresses.add(tempAddress);
        }
        Collections.sort(addresses, new Comparator<InetAddress>() {
            @Override
            public int compare(InetAddress o1, InetAddress o2) {
                for (int i = 0; i < 4; i++) {
                    if (o1.getAddress()[i] == o2.getAddress()[i]) {
                        continue;
                    }
                    else if ( unsignedByteToInt( o1.getAddress()[i]) > unsignedByteToInt( o2.getAddress()[i])) {
                        return 1;
                    } else if (unsignedByteToInt( o1.getAddress()[i]) < unsignedByteToInt( o2.getAddress()[i])) {
                        return -1;
                    }
                }
                return 0;
            }

            private int unsignedByteToInt(byte b) {
                return (int) b & 0xFF;
            }
        });
        PrintWriter writer = new PrintWriter(new File("ipmask.txt"));
        writer.write(String.format("%d\n", ADDR_NUM));
        for (int i = 0; i < addresses.size(); i++) {
            InetAddress temp = addresses.get(i);
            for (int j = 0; j < 4; j++) {
                writer.write("FF ");
            }
            for (int j = 0; j < 4; j++) {
                writer.write(String.format("%02X ", temp.getAddress()[j]));
            }
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }
}
