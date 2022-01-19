import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ImageConverter {
    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(args[args.length-1]));
        System.out.println(args[args.length-1]);
        PrintWriter writer = new PrintWriter(new File("imgtext.txt"));
        int rgb;
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                rgb = bufferedImage.getRGB(j, i);
                //writer.write(String.format("%06X ", rgb & 0xffffff));
                writer.write(String.format("%d", (rgb >> 16) & 0xff));
                writer.write(" ");
                writer.write(String.format("%d", (rgb >> 8) & 0xff));
                writer.write(" ");
                writer.write(String.format("%d", rgb & 0xff));
                writer.write("\n");
            }
            //writer.write("\n");
        }
        writer.write("\n");
        writer.flush();
        writer.close();
    }
}
