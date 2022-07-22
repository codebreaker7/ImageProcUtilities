import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ImageDifference {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Not enough arguments");
            return;
        }
        BufferedImage bufferedImage1 = ImageIO.read(new File(args[0]));
        BufferedImage bufferedImage2 = ImageIO.read(new File(args[1]));
        PrintWriter pw;
        if (args.length == 3)
            pw = new PrintWriter(new File(args[2]));
        else
            pw = new PrintWriter(new File("image_diff.txt"));
        if (bufferedImage1.getWidth() != bufferedImage2.getWidth() || bufferedImage1.getHeight() != bufferedImage2.getHeight()) {
            System.out.println("ERROR: Dimension mismatch");
            return;
        }
        for (int i = 0; i < bufferedImage1.getHeight(); i++) {
            for (int j = 0; j < bufferedImage1.getWidth(); j++) {
                int rgb1 = bufferedImage1.getRGB(j, i);
                int rgb2 = bufferedImage2.getRGB(j, i);
                if (rgb1 != rgb2) {
                    pw.write(String.format("Error in %d row, %d column\n", i, j));
                    int r1 = (rgb1 >> 16) & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    if (r1 != r2)
                        pw.write(String.format("Mismatch in R component: %d, %d, diff - %d\n", r1, r2, r1 - r2));
                    int g1 = (rgb1 >> 8) & 0xff;
                    int g2 = (rgb2 >> 8) & 0xff;
                    if (g1 != g2)
                        pw.write(String.format("Mismatch in G component: %d, %d, diff - %d\n", g1, g2, g1 - g2));
                    int b1 = (rgb1) & 0xff;
                    int b2 = (rgb2) & 0xff;
                    if (b1 != b2)
                        pw.write(String.format("Mismatch in B component: %d, %d, diff - %d\n", b1, b2, b1 - b2));
                }
            }
        }
        pw.close();
    }
}
