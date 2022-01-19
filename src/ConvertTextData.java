import java.io.*;
import java.util.*;

public class ConvertTextData {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[args.length-1]));
        PrintWriter writer = new PrintWriter(new File("textstats.txt"));
        String tempBuffer;
        StringBuffer stringBuffer = new StringBuffer();
        Map<Integer, Integer> statDict = new HashMap<Integer, Integer>();
        int prevValue = -1;
        int NUM_OF_REPEATS = 3;
        int repeatCounter = 0;
        int positionTracker = 1;
        while(br.ready()) {
            tempBuffer = br.readLine();
            for (String s : tempBuffer.split(" ")) {
                int val = Integer.parseUnsignedInt(s, 16);
                if (statDict.containsKey(val)) {
                    statDict.replace(val, statDict.get(val)+1);
                } else {
                    statDict.put(val, 1);
                }
                if (val == prevValue) {
                    repeatCounter++;
                } else {
                    if (repeatCounter >= NUM_OF_REPEATS) {
                        stringBuffer.append("The value ");
                        stringBuffer.append(prevValue);
                        stringBuffer.append(" occurs ");
                        stringBuffer.append(repeatCounter);
                        stringBuffer.append(" times starting from position ");
                        stringBuffer.append(positionTracker - repeatCounter);
                        stringBuffer.append('\n');
                    }
                    repeatCounter = 1;
                }
                prevValue = val;
                positionTracker++;
            }
        }
        List<Map.Entry<Integer, Integer>> entryList = new LinkedList<>(statDict.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        Map<Integer, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> ent:
                entryList) {
            sorted.put(ent.getKey(), ent.getValue());
        }
        for (Map.Entry<Integer,Integer> i:
                sorted.entrySet()) {
            writer.write(String.format("%02X ", i.getKey()));
            writer.write(" - ");
            writer.write(i.getValue().toString());
            writer.write("\n");
        }
        writer.flush();
        writer.write(stringBuffer.toString());
        writer.close();
    }
}
