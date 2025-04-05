import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageEncode {

    File original_file;
    String binary_msg;
    File encoded_file;

    public ImageEncode(String message, int cipher_shift, File fi) {
        original_file = fi;
        binary_msg = EncryptAndConvert.msg_to_binary_string(message, cipher_shift);
        System.out.println(binary_msg);
    }   

    // Encode the binary message into the LSB of all RGB channels
    public String encode() {
        try {
            BufferedImage input_image = ImageIO.read(original_file);
            int width = input_image.getWidth();
            int height = input_image.getHeight();
            BufferedImage output_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int index = 0; // Track bit position in binary message
            int bitLength = binary_msg.length(); // Total bits to hide

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = input_image.getRGB(x, y);

                    int red = (pixel >> 16) & 255;
                    int green = (pixel >> 8) & 255;
                    int blue = pixel & 255;

                    // Modify LSB of Red, Green, and Blue channels using bitmasking decimal 254
                    // (11111110)
                    if (index < bitLength) {
                        int bit = Character.getNumericValue(binary_msg.charAt(index));
                        red = (red & 254) | bit; // Clear LSB and set new bit
                        index++;
                    }
                    if (index < bitLength) {
                        int bit = Character.getNumericValue(binary_msg.charAt(index));
                        green = (green & 254) | bit; // Clear LSB and set new bit
                        index++;
                    }
                    if (index < bitLength) {
                        int bit = Character.getNumericValue(binary_msg.charAt(index));
                        blue = (blue & 254) | bit; // Clear LSB and set new bit
                        index++;
                    }

                    // Reconstruct the pixel and set it in the new image
                    int new_pixel = (red << 16) | (green << 8) | blue;
                    output_image.setRGB(x, y, new_pixel);
                }
            }

            // Save the encoded image
            encoded_file = new File("encoded_image.png");
            ImageIO.write(output_image, "png", encoded_file);

            return "Image encoded successfully: " + encoded_file.getAbsolutePath();

        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
