import java.io.*;

public class ConvertNetworkFile {
    public static void main(String[] args) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();

        File curdir = new File(".");
        int numfiles = 0;
        for (File file : curdir.listFiles()) {
            if (file.getName().endsWith(".txt")) {
                numfiles++;
            }
        }
        PrintWriter writer = new PrintWriter(new File("netpacks.txt"));
        writer.write(String.valueOf(numfiles));
        writer.write("\n");
        for (File file : curdir.listFiles()) {
            int counter = 0;
            int numLines = 1;
            if (file.getName().endsWith(".txt") && !file.getName().matches("netpacks.txt")) {
                System.out.println("File name" + file.getName());
                BufferedReader br = new BufferedReader(new FileReader(file));
                while (br.ready()) {
                    String temp = br.readLine();
                    String[] numbers = temp.split(" ");
                    for (int i = 1; i < numbers.length; i++) {
                        if (numbers[i].length() == 2) {
                            stringBuffer.append(numbers[i].trim());
                            System.out.println(String.format("Processing value %s", numbers[i].trim()));
                            System.out.println(counter);
                            stringBuffer.append(' ');
                            counter++;
                            if (counter == 4) {
                                counter = 0;
                                numLines++;
                                stringBuffer.append('\n');
                            }
                        }
                    }
                }
                writer.write(String.valueOf(numLines-2));
                writer.write("\n");
                writer.write(stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
            }
        }
        writer.flush();
        writer.close();
    }
}
