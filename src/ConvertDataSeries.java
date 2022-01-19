import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ConvertDataSeries {
    static int WIDTH = 1280;
    static int HEIGHT = 964;
    public static void main(String[] args) throws IOException {
        String inputfile = "camera.dat";
        InputStream fis = new FileInputStream(inputfile);

        BufferedImage bimage = new BufferedImage(WIDTH/2, HEIGHT/2, BufferedImage.TYPE_INT_RGB);
        int r1, r2, g11, g12, g21, g22, b1, b2;
        byte[] buf = new byte[4935680]; // buffer for one image
        fis.read(buf);

        for (int j = 0; j < HEIGHT / 2; j++) {
            for (int i = 0; i < WIDTH / 2; i++) {
                r1 = buf[((WIDTH * j * 2) + (i * 2 + 1)) * 4];
                r2 = buf[((WIDTH * j * 2) + (i * 2 + 1)) * 4 + 1];
                int r = (((r2 << 8) & 0x0f) + r1) >> 4;
                g11 = buf[((WIDTH * j * 2) + (i * 2)) * 4];
                g12 = buf[((WIDTH * j * 2) + (i * 2)) * 4 + 1];
                g21 = buf[((WIDTH * (j * 2 + 1)) + (i * 2 + 1)) * 4];
                g22 = buf[((WIDTH * (j * 2 + 1)) + (i * 2 + 1)) * 4 + 1];
                int g1 = (((g12 << 8) & 0x0f) + g11) >> 4;
                int g2 = (((g22 << 8) & 0x0f) + g21) >> 4;
                b1 = buf[((WIDTH * (j * 2 + 1)) + (i * 2)) * 4];
                b2 = buf[((WIDTH * (j * 2 + 1)) + (i * 2)) * 4 + 1];
                int b = (((b2 << 8) & 0x0f) + b1) >> 4;
                int g = (g1 + g2) / 2;
                bimage.setRGB(i, j, (r << 16) + (g << 8) + b);
            }
        }
        ImageIO.write(bimage, "bmp", new File("converted.bmp"));
    }
}
