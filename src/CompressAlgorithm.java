import java.io.*;
import java.util.*;

public class CompressAlgorithm {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[args.length - 2]));
        PrintWriter writer = new PrintWriter(new File("textstats.txt"));
        PrintWriter writerEnt = new PrintWriter(new File("compress.txt"));
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
        ArrayList< ArrayList<SequenceInfo>> compressList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            compressList.add(new ArrayList<>());
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
                        compressList.get(jcol).add(new SequenceInfo(prevValue[jcol], repeatCounter[jcol], positionTracker[jcol] - repeatCounter[jcol]));
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
        // append the last value
        for (int i = 0; i < 3; i++) {
            compressList.get(i).add(new SequenceInfo(prevValue[i], repeatCounter[i], positionTracker[i] - repeatCounter[i]));
        }
        int totalIniSize = 0;
        int totalNumBytes = 0;
        int overallBytes = 0;
        // write the compression entry values
        for (int listNum = 0; listNum < 3; listNum++) {
            boolean isStart = true;
            int lastPos = 0;
            int initialSize = 0;
            int numBytes = 0;
            int totalBytes = 0;
            for (int i = 0; i < compressList.get(listNum).size(); i++) {
                totalBytes += compressList.get(listNum).get(i).len;
                if (compressList.get(listNum).get(i).val == 128 && compressList.get(listNum).get(i).len > 4) {
                    numBytes += 4;
                    if (isStart) {
                        writerEnt.write(String.format("%d - %d\n", compressList.get(listNum).get(i).pos, compressList.get(listNum).get(i).len));
                        lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                        initialSize += compressList.get(listNum).get(i).len;
                        isStart = false;
                    } else {
                        writerEnt.write(String.format("%d - %d\n", compressList.get(listNum).get(i).pos - lastPos, compressList.get(listNum).get(i).len));
                        lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;// calculate new value
                        initialSize += compressList.get(listNum).get(i).len;
                    }
                }
            }
            writerEnt.write(String.format("Initial size of 128 - %d, compressed to %d, total size before - %d, size after - %d, ratio - %.2f\n", initialSize, numBytes, totalBytes, totalBytes - initialSize + numBytes, (float) totalBytes / (totalBytes - initialSize + numBytes)));
            totalIniSize += initialSize;
            totalNumBytes += numBytes;
            overallBytes += totalBytes;
            writerEnt.flush();
        }
        writerEnt.write(String.format("Initial size of 128 - %d, compressed to %d, total size before - %d, size after - %d, ratio - %.2f\n", totalIniSize, totalNumBytes, overallBytes, overallBytes - totalIniSize + totalNumBytes, (float) overallBytes / (overallBytes - totalIniSize + totalNumBytes)));

        // write rest blocks in as a file
        OutputStream binFile = new FileOutputStream("residues.bin");
        for (int listNum = 0; listNum < 3; listNum++) {
            for (int i = 0; i < compressList.get(listNum).size(); i++) {
                if (compressList.get(listNum).get(i).val != 128) {
                    for (int j = 0; j < compressList.get(listNum).get(i).len; j++) {
                        binFile.write(compressList.get(listNum).get(i).val);
                    }
                } else if (compressList.get(listNum).get(i).val == 128 && compressList.get(listNum).get(i).len <= 4) {
                    for (int j = 0; j < compressList.get(listNum).get(i).len; j++) {
                        binFile.write(compressList.get(listNum).get(i).val);
                    }
                }
            }
        }
        binFile.flush();
        binFile.close();
        // compress the result file using Huffman encoding
        File inputFile = new File("residues.bin");
        FrequencyTable frequencyTable = HuffmanCompress.getFrequencies(inputFile);
        frequencyTable.increment(256);
        CodeTree codeTree = frequencyTable.buildCodeTree();
        CanonicalCode canonCode = new CanonicalCode(codeTree, frequencyTable.getSymbolLimit());
        codeTree = canonCode.toCodeTree();
        try (InputStream inputStream = new FileInputStream(new File("residues.bin"))) {
            try (BitOutputStream outputStream = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(new File("residues_huff.bin"))))) {
                HuffmanCompress.writeCodeLengthTable(outputStream, canonCode);
                HuffmanCompress.compress(codeTree, inputStream, outputStream);
            }
        }
        RandomAccessFile compressResFile = new RandomAccessFile("residues_huff.bin", "r");
        long huffmanRes = compressResFile.length();
        compressResFile.close();
        writerEnt.write(String.format("Size of the residues - %d, size of compressed file -  %d\n", overallBytes - totalIniSize, huffmanRes));
        writerEnt.write(String.format("Total size before - %d, total size after - %d, ratio - %.2f\n", overallBytes, huffmanRes + totalNumBytes, (float)overallBytes/(huffmanRes+totalNumBytes)));
        writerEnt.close();
        // stat for column
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
