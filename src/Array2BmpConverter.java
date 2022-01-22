import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Array2BmpConverter {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: width height filename");
            return;
        }
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        String buffer;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(args[2]));
        for (int row = 0; row < height; row+=4) {
            for (int col = 0; col < width; col++) {
                for (int i = 0; i < 4; i++) {
                    buffer = bufferedReader.readLine();
                    String[] compStr = buffer.split(" ");
                    int r = Integer.parseInt(compStr[0], 16);
                    int g = Integer.parseInt(compStr[1], 16);
                    int b = Integer.parseInt(compStr[2], 16);
                    int rgb = ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
                    image.setRGB(col, row+i, rgb);
                }
            }
        }
        ImageIO.write(image, "bmp", new File("ycrcb.bmp"));
    }
}
