import java.io.*;
import java.util.*;

public class ConvertTextDataCols {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[args.length - 2]));
        PrintWriter writer = new PrintWriter(new File("textstats.txt"));
        String tempBuffer;
        StringBuffer[] stringBuffer = new StringBuffer[3];
        for (int i = 0; i < 3; i++) {
            stringBuffer[i] = new StringBuffer();
        }
        ArrayList<Map<Integer, Integer>> statDictList = new ArrayList<>();
        ArrayList<Map<Integer, Integer>> sequenceStatList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            statDictList.add(new HashMap<>());
            sequenceStatList.add(new HashMap<>());
        }
        int[] prevValue = new int[]{-1, -1, -1};
        int NUM_OF_REPEATS = Integer.parseInt(args[args.length - 1]);
        int[] repeatCounter = new int[]{0, 0, 0};
        int[] positionTracker = new int[]{1, 1, 1};
        while (br.ready()) {
            tempBuffer = br.readLine();
            int jcol = 0;
            for (String s : tempBuffer.split(" ")) {
                int val = Integer.parseUnsignedInt(s, 16);
                if (statDictList.get(jcol).containsKey(val)) {
                    statDictList.get(jcol).replace(val, statDictList.get(jcol).get(val) + 1);
                } else {
                    statDictList.get(jcol).put(val, 1);
                }
                if (val == prevValue[jcol]) {
                    repeatCounter[jcol]++;
                } else {
                    if (repeatCounter[jcol] >= NUM_OF_REPEATS) {
                        stringBuffer[jcol].append("The value ");
                        stringBuffer[jcol].append(prevValue[jcol]);
                        stringBuffer[jcol].append(" occurs ");
                        stringBuffer[jcol].append(repeatCounter[jcol]);
                        stringBuffer[jcol].append(" times starting from position ");
                        stringBuffer[jcol].append(positionTracker[jcol] - repeatCounter[jcol]);
                        stringBuffer[jcol].append('\n');
                        if (sequenceStatList.get(jcol).containsKey(prevValue[jcol])) {
                            sequenceStatList.get(jcol).replace(prevValue[jcol], sequenceStatList.get(jcol).get(prevValue[jcol]) + 1);
                        } else {
                            sequenceStatList.get(jcol).put(prevValue[jcol], 1);
                        }
                    }
                    repeatCounter[jcol] = 1;
                }
                prevValue[jcol] = val;
                positionTracker[jcol]++;
                jcol++;
            }
        }
        for (int i = 0; i < 3; i++) {
            writer.write(String.format("Information for %d column:\n", i));
            List<Map.Entry<Integer, Integer>> entryList = new LinkedList<>(statDictList.get(i).entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            Map<Integer, Integer> sorted = new LinkedHashMap<>();
            for (Map.Entry<Integer, Integer> ent :
                    entryList) {
                sorted.put(ent.getKey(), ent.getValue());
            }
            for (Map.Entry<Integer, Integer> inst :
                    sorted.entrySet()) {
                writer.write(String.format("%02X ", inst.getKey()));
                writer.write(" - ");
                writer.write(inst.getValue().toString());
                writer.write("\n");
            }

        }
        writer.flush();
        for (int i = 0; i < 3; i++) {
            writer.write(String.format("***************** Sequence info for %d column *************\n", i));
            writer.write("Number of sequence per value: \n");
            for (Map.Entry<Integer, Integer> ent:
                 sequenceStatList.get(i).entrySet()) {
                writer.write(String.format("%d - %d\n", ent.getKey(), ent.getValue()));
            }
            writer.write(stringBuffer[i].toString());
        }
        writer.flush();
        writer.close();
    }
}
