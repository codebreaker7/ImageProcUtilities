import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ImageStatistics {
    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(args[args.length-1]));
        System.out.println(args[args.length-1]);
        PrintWriter writer = new PrintWriter(new File("imgstats.txt"));
        int rgb;
        Map<Integer, Integer> statDict = new HashMap<Integer, Integer>();
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                rgb = bufferedImage.getRGB(j, i) & 0xffffff;
                if (statDict.containsKey(rgb)) {
                    statDict.replace(rgb, statDict.get(rgb) + 1);
                } else {
                    statDict.put(rgb, 1);
                }
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
            writer.write(String.format("%06X ", i.getKey()));
            writer.write(" - ");
            writer.write(i.getValue().toString());
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }
}
