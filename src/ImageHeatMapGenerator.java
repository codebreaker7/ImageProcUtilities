import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageHeatMapGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("Not enough arguments");;
            return;
        }
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        // the next argument should specify the file to compare with and the last one is for the file under comparison
        InputStream startFile = new FileInputStream(args[2]);
        InputStream compareFile = new FileInputStream(args[3]);
        for (int i = 0; i < 3; i++) {
            BufferedImage heatMapImage = new BufferedImage(width * 10, height * 10, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = heatMapImage.getGraphics();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, heatMapImage.getWidth(), heatMapImage.getHeight());
            graphics.setColor(Color.black);
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    int startVal = startFile.read();
                    int compareVal = compareFile.read();
                    if (startVal == compareVal) {
                        graphics.setColor(Color.white);
                        graphics.fillRect(k*10, j * 10, 10, 10);
                        graphics.setColor(Color.black);
                        graphics.drawString("0", k * 10, (j+1) * 10);
                    } else {
                        int diff = startVal - compareVal;
                        graphics.setColor(Color.yellow);
                        graphics.fillRect(k * 10, j * 10, 10, 10);
                        graphics.setColor(Color.BLACK);
                        graphics.drawString(Integer.toString(diff), k * 10, (j+1) * 10);
                    }
                }
            }
            String filename = String.format("heatMap%d.jpg", i);
            ImageIO.write(heatMapImage, "jpg", new File(filename));
        }
    }
}
