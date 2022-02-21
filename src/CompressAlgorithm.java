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
        int numLines = 0;
        while (br.ready()) {
            numLines++;
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
        int[] length128 = new int[3];
        int[] lengthnon128 = new int[3];
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
                    length128[listNum]++;
                    numBytes += 4;
                    if (isStart) {
                        if (compressList.get(listNum).get(i).len < 65536) {
                            writerEnt.write(String.format("%d - %d\n", compressList.get(listNum).get(i).pos, compressList.get(listNum).get(i).len));
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                            initialSize += compressList.get(listNum).get(i).len;
                            isStart = false;
                        } else {
                            int tempLength = compressList.get(listNum).get(i).len;
                            boolean isFirst = true;
                            while (tempLength != 0) {
                                if (isFirst) {
                                    writerEnt.write(String.format("%d - %d\n", compressList.get(listNum).get(i).pos, 65535));
                                    tempLength -= 65535;
                                    isFirst = false;
                                } else {
                                    if (tempLength < 65536) {
                                        writerEnt.write(String.format("%d - %d\n", 0, tempLength));
                                        tempLength -= tempLength;
                                    } else {
                                        writerEnt.write(String.format("%d - %d\n", 0, 65535));
                                        tempLength -= 65535;
                                    }
                                    numBytes += 4;
                                    length128[listNum]++;
                                }
                            }
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                            initialSize += compressList.get(listNum).get(i).len;
                            isStart = false;
                        }
                    } else {
                        if (compressList.get(listNum).get(i).len < 65536) {
                            writerEnt.write(String.format("%d - %d\n", compressList.get(listNum).get(i).pos - lastPos, compressList.get(listNum).get(i).len));
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;// calculate new value
                            initialSize += compressList.get(listNum).get(i).len;
                        } else {
                            int tempLength = compressList.get(listNum).get(i).len;
                            boolean isFirst = true;
                            while (tempLength != 0) {
                                if (isFirst) {
                                    writerEnt.write(String.format("%d - %d\n", compressList.get(listNum).get(i).pos - lastPos, 65535));
                                    tempLength -= 65535;
                                    isFirst = false;
                                } else {
                                    if (tempLength < 65536) {
                                        writerEnt.write(String.format("%d - %d\n", 0, tempLength));
                                        tempLength -= tempLength;
                                    } else {
                                        writerEnt.write(String.format("%d - %d\n", 0, 65535));
                                        tempLength -= 65535;
                                    }
                                    numBytes += 4;
                                    length128[listNum]++;
                                }
                            }
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                            initialSize += compressList.get(listNum).get(i).len;
                        }
                    }
                }
            }
            writerEnt.write(String.format("Initial size of 128 - %d, compressed to %d, total size before - %d, size after - %d, ratio - %.2f\n", initialSize, numBytes, totalBytes, totalBytes - initialSize + numBytes, (float) totalBytes / (totalBytes - initialSize + numBytes)));
            totalIniSize += initialSize;
            totalNumBytes += numBytes;
            overallBytes += totalBytes;
            writerEnt.flush();
            lengthnon128[listNum] = totalBytes - initialSize;
        }
        writerEnt.write(String.format("Initial size of 128 - %d, compressed to %d, total size before - %d, size after - %d, ratio - %.2f\n", totalIniSize, totalNumBytes, overallBytes, overallBytes - totalIniSize + totalNumBytes, (float) overallBytes / (overallBytes - totalIniSize + totalNumBytes)));

        //store stream of 128 as a binary file
        store128(compressList, length128);
        // write rest blocks in as a file
        OutputStream binFile = new FileOutputStream("residues.bin");
        DataOutputStream dataBinOutput = new DataOutputStream(binFile);
        for (int i = 0; i < 3; i++) {
            dataBinOutput.writeInt(lengthnon128[i]);
        }
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

        //start backward operation
        // decompress Huffman file
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream("residues_huff.bin")))) {
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("residues_restored.bin")))) {
                canonCode = HuffmanDecompress.readCodeLengthTable(in);
                CodeTree code = canonCode.toCodeTree();
                HuffmanDecompress.decompress(code, in, out);
            }
        }

        // recover files into lists
        ArrayList<ArrayList<SequenceInfo>> arr128 = new ArrayList<>();
        ArrayList<ArrayList<Byte>> arrResidues = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            arr128.add(new ArrayList<>());
            arrResidues.add(new ArrayList<>());
        }
        // read information about sequences
        read128("compress128.bin", arr128);
        readResidues("residues_restored.bin", arrResidues);
        restoreOriginal("fresy_restored.txt", numLines, arr128, arrResidues);
    }

    public static void store128(ArrayList<ArrayList<SequenceInfo>> compressList, int[] lengths) throws IOException {
        OutputStream outputStream = new FileOutputStream("compress128.bin");
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        // write length of each list to recover them
        dataOutputStream.writeInt(lengths[0]);
        dataOutputStream.writeInt(lengths[1]);
        dataOutputStream.writeInt(lengths[2]);
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
                        if (compressList.get(listNum).get(i).len < 65536) {
                            dataOutputStream.writeShort(compressList.get(listNum).get(i).pos);
                            dataOutputStream.writeShort(compressList.get(listNum).get(i).len);
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                            initialSize += compressList.get(listNum).get(i).len;
                            isStart = false;
                        } else {
                            int tempLength = compressList.get(listNum).get(i).len;
                            boolean isFirst = true;
                            while (tempLength != 0) {
                                if (isFirst) {
                                    dataOutputStream.writeShort(compressList.get(listNum).get(i).pos);
                                    dataOutputStream.writeShort(65535);
                                    tempLength -= 65535;
                                    isFirst = false;
                                } else {
                                    if (tempLength < 65536) {
                                        dataOutputStream.writeShort(0);
                                        dataOutputStream.writeShort(tempLength);
                                        tempLength -= tempLength;
                                    } else {
                                        dataOutputStream.writeShort(0);
                                        dataOutputStream.writeShort(65535);
                                        tempLength -= 65535;
                                    }
                                    numBytes += 4;
                                }
                            }
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                            initialSize += compressList.get(listNum).get(i).len;
                            isStart = false;
                        }
                    } else {
                        if (compressList.get(listNum).get(i).len < 65536) {
                            dataOutputStream.writeShort(compressList.get(listNum).get(i).pos - lastPos);
                            dataOutputStream.writeShort(compressList.get(listNum).get(i).len);
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;// calculate new value
                            initialSize += compressList.get(listNum).get(i).len;
                        } else {
                            int tempLength = compressList.get(listNum).get(i).len;
                            boolean isFirst = true;
                            while (tempLength != 0) {
                                if (isFirst) {
                                    dataOutputStream.writeShort(compressList.get(listNum).get(i).pos - lastPos);
                                    dataOutputStream.writeShort(65535);
                                    tempLength -= 65535;
                                    isFirst = false;
                                } else {
                                    if (tempLength < 65536) {
                                        dataOutputStream.writeShort(0);
                                        dataOutputStream.writeShort(tempLength);
                                        tempLength -= tempLength;
                                    } else {
                                        dataOutputStream.writeShort(0);
                                        dataOutputStream.writeShort(65535);
                                        tempLength -= 65535;
                                    }
                                    numBytes += 4;
                                }
                            }
                            lastPos = compressList.get(listNum).get(i).pos + compressList.get(listNum).get(i).len;
                            initialSize += compressList.get(listNum).get(i).len;
                        }
                    }
                }
            }
        }
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    public static void read128(String filename, ArrayList<ArrayList<SequenceInfo>> numList) throws IOException {
        InputStream inputStream = new FileInputStream(filename);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int[] lengths = new int[3];
        // read the number of elements
        for (int i = 0; i < 3; i++) {
            lengths[i] = dataInputStream.readInt();
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lengths[i]; j++) {
                int pos = (int)dataInputStream.readShort() & 0xffff;
                int len = (int)dataInputStream.readShort() & 0xffff;
                numList.get(i).add(new SequenceInfo(128, len, pos));
                //System.out.println(String.format("%d - %d - %d - %d", i, j, len, pos));
            }
        }
        dataInputStream.close();
        inputStream.close();
    }

    public static void readResidues(String filename, ArrayList<ArrayList<Byte>> resList) throws IOException {
        InputStream inputStream = new FileInputStream(filename);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int[] lengths = new int[3];
        // read the number of elements
        for (int i = 0; i < 3; i++) {
            lengths[i] = dataInputStream.readInt();
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lengths[i]; j++) {
                byte temp = dataInputStream.readByte();
                resList.get(i).add(temp);
            }
        }
        dataInputStream.close();
        inputStream.close();
    }

    public static void restoreOriginal(String filename, int numLines, ArrayList<ArrayList<SequenceInfo>> arr128, ArrayList<ArrayList<Byte>> resList) throws FileNotFoundException {
        OutputStream outputStream = new FileOutputStream(filename);
        PrintWriter writer = new PrintWriter(outputStream);
        boolean isStart = true;
        // procSequence
        boolean[] procSequence = new boolean[3]; procSequence[0] = false;procSequence[1] = false;procSequence[2] = false;
        int[] seqLength = new int[3];
        int[] resMarker = new int[3];
        int[] seqMarker = new int[3];
        int[] curValue = new int[3];
        for (int i = 0; i < 3; i++) {
            if (arr128.get(i).get(0).pos == 1) {
                procSequence[i] = true;
                seqLength[i] = arr128.get(i).get(0).len;
            } else {
                procSequence[i] = false;
                seqLength[i] = arr128.get(i).get(0).pos - 1;
            }
        }
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < 3; j++) {
                if (procSequence[j]) {
                    curValue[j] = 128;
                    seqLength[j]--;
                    if (seqLength[j] == 0) {
                        seqMarker[j]++;
                        if (seqMarker[j] == arr128.get(j).size()) {
                            continue;
                        }
                        if (arr128.get(j).get(seqMarker[j]).pos != 0) {
                            seqLength[j] = arr128.get(j).get(seqMarker[j]).pos;
                            procSequence[j] = false;
                            //System.out.println(String.format("Processing %d - %d - %d", j, arr128.get(j).get(seqMarker[j]).len, arr128.get(j).get(seqMarker[j]).pos));
                        } else {
                            seqLength[j] = arr128.get(j).get(seqMarker[j]).len;
                            procSequence[j] = true;
                            //System.out.println(String.format("Processing %d - %d - %d", j, arr128.get(j).get(seqMarker[j]).len, arr128.get(j).get(seqMarker[j]).pos));
                        }
                    }
                } else {
                    curValue[j] = resList.get(j).get(resMarker[j]) & 0xff;
                    resMarker[j]++;
                    seqLength[j]--;
                    if (seqLength[j] == 0) {
                        procSequence[j] = true;
                        seqLength[j] = arr128.get(j).get(seqMarker[j]).len;
                    }
                }
            }
            writer.write(String.format("%02X %02X %02X\n", curValue[0], curValue[1], curValue[2]));
        }
        writer.flush();
        writer.close();
    }
}
