import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class ImageDecode {

    private File encoded_file;
    private ArrayList<String> binary_values = new ArrayList<>();
    private ArrayList<String> shift_value = new ArrayList<>();

    public ImageDecode(File file) {
        this.encoded_file = file;
    }

    public String decode() {
        try {
            BufferedImage input_image = ImageIO.read(encoded_file);
            int width = input_image.getWidth();
            int height = input_image.getHeight();

            int bitCount = 0;
            String binary_string = "";
            int marker = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = input_image.getRGB(x, y);
                    int red = (pixel >> 16) & 1;
                    int green = (pixel >> 8) & 1;
                    int blue = pixel & 1;

                    binary_string += red;
                    binary_string += green;
                    binary_string += blue;
                    bitCount += 3;

                    if (bitCount >= 8) {
                        String byteStr = binary_string.substring(0, 8);
                        binary_string = binary_string.substring(8);
                        bitCount -= 8;

                        if (byteStr.equals("00000000")) {
                            marker++;
                            if (marker == 2) {
                                System.out.println("Extracted Message Binary: " + binary_values);
                                System.out.println("Shift Value Binary: " + shift_value);

                                return DecryptAndConvert.decode(binary_values, shift_value);
                            }
                        } else {
                            if (marker == 0) {
                                binary_values.add(byteStr);
                            } else if (marker == 1) {
                                shift_value.add(byteStr);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
