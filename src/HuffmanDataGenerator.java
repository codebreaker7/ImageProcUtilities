import java.io.*;
import java.util.List;
import java.util.Random;

public class HuffmanDataGenerator {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("distribution.txt"));
        int tempInt;
        int[] farr = new int[256];
        for (int i = 0; i < 256; i++) {
            String line = reader.readLine();
            String[] vals = line.split(" ");
            int index = Integer.parseUnsignedInt(vals[0]);
            int freq = Integer.parseUnsignedInt(vals[1]);
            if (freq < 5) freq = 5;
            farr[index] = freq;
        }
        FrequencyTable frequencyTable = new FrequencyTable(farr);
        System.out.println(frequencyTable.toString());
        CodeTree codeTree = frequencyTable.buildCodeTree();
        CanonicalCode canonCode = new CanonicalCode(codeTree, frequencyTable.getSymbolLimit());
        codeTree = canonCode.toCodeTree();
        for (int i = 0; i < 256; i++) {
            System.out.print(i);
            System.out.print("-");
            List<Integer> list = codeTree.getCode(i);
            for (Integer j: list) {
                System.out.print(j);
            }
            System.out.println();
        }
        //System.out.println(codeTree.toString());
    }

}
