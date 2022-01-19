import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Bmp2ArrayConverter {
    public static void main(String[] args) throws IOException {
        //System.out.println(args[0]);
        BufferedImage bufferedImage = ImageIO.read(new File(".//plus.png"));
        PrintWriter writer = new PrintWriter(new File("output_plus.h"));
//        writer.write("uint16_t image_array[] = {\n");
//        for (int i = 0; i < bufferedImage.getHeight(); i++) {
//            for (int j = 0; j < bufferedImage.getWidth(); j++) {
//                int rgb = bufferedImage.getRGB(j, i);
//                int r = (rgb >> 18) & 0b11111;
//                int g = (rgb >> 11) & 0b111111;
//                int b = rgb & 0b11111;
//                int newval = (r << 11) | (g << 5) | b;
//                writer.format("0x%04x, ", newval);
//            }
//            writer.write("\n");
//        }
//        writer.write("};\n");
        //writer.write("uint16_t image_array[] = {\n");
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                int rgb = bufferedImage.getRGB(j, i);
                int r = (rgb >> 16) & 0b11111111;
                int g = (rgb >> 8) & 0b11111111;
                int b = rgb & 0b11111111;
                int newval = (r+g+b)/3;
                //writer.format("0b%08, \n", newval);
                writer.write(String.format("\"%8s\",\n", Integer.toBinaryString(newval)).replace(' ', '0'));
            }
            //writer.write("\n");
        }
        writer.write("};\n");
        writer.flush();
        writer.close();
    }
}
