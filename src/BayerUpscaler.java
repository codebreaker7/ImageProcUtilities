import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BayerUpscaler {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Not enough parameters");
            return;
        }
        BufferedImage bufferedImage = ImageIO.read(new File(args[args.length - 1]));
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth()*2, bufferedImage.getHeight()*2, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                // convert one pixel into two 4 pixels
                int rgb = bufferedImage.getRGB(j, i);
                newImage.setRGB(j*2, i*2, rgb);
                newImage.setRGB(j*2+1, i*2, rgb);
                newImage.setRGB(j*2, i*2+1, rgb);
                newImage.setRGB(j*2+1, i*2+1, rgb);
            }
        }
        ImageIO.write(newImage, "jpg", new File("converted_up.jpg"));
    }
}
