import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageEncode {

    String msg;
    int shift;
    File original_file;
    String encrypted_msg;
    String binary_msg;
    File encoded_file;

    public ImageEncode(String message, int cipher_shift, File fi) {
        msg = message;
        shift = cipher_shift;
        original_file = fi;
        encrypted_msg = encrypt();
        binary_msg = convert_to_binary_string();
        System.out.println(binary_msg);
    }

    // Encrypt the message using Caesar Cipher
    public String encrypt() {
        StringBuilder result = new StringBuilder();
        for (char ch : msg.toCharArray()) {
            if (ch == 10) { // Preserve newline
                result.append(ch);
            } else if (ch >= 32 && ch <= 126) { // Shift within the printable ASCII range
                int newChar = ((ch - 32 + shift) % 95) + 32;
                result.append((char) newChar);
            }
        }
        return result.toString();
    }

    // Convert encrypted message to binary string
    public String convert_to_binary_string() {
        StringBuilder binaryOutput = new StringBuilder();

        for (char ch : encrypted_msg.toCharArray()) {
            binaryOutput.append(String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0'));
        }

        binaryOutput.append("00000000"); // End of message marker

        // Append cipher shift value and termination marker
        // Do not forget to convert the shift from integer to String
        // otherwise it will take the numeric value instead of the character's ASCII

        String shift_string = Integer.toString(shift);

        for (char ch : shift_string.toCharArray()) {
            binaryOutput.append(String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0'));
        }

        binaryOutput.append("00000000"); // End of shift value marker
        return binaryOutput.toString();
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
