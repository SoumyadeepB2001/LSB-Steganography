import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Decode {

    File encoded_file;
    ArrayList<String> binary_values = new ArrayList<>();
    ArrayList<String> shift_value = new ArrayList<>();

    Decode(File file) {
        encoded_file = file;
    }

    public static void main(String[] args) {

    }

    public String decode() {
        try {
            // Load the image
            BufferedImage input_image = ImageIO.read(encoded_file);

            int width = input_image.getWidth();
            int height = input_image.getHeight();

            int bitCount = 0;
            String binary_string = "";
            int marker = 0; // 0 = message, 1 = shift value

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = input_image.getRGB(x, y);
                    int red = (pixel >> 16) & 1;
                    int green = (pixel >> 8) & 1;
                    int blue = pixel & 1;

                    // Append extracted bits
                    binary_string += red;
                    binary_string += green;
                    binary_string += blue;
                    bitCount += 3;

                    // Process when exactly 8 bits are collected
                    if (bitCount >= 8) {
                        String byteStr = binary_string.substring(0, 8);
                        binary_string = binary_string.substring(8);
                        bitCount -= 8;

                        if (byteStr.equals("00000000")) {
                            marker++; // Switch between storing in binary_values and shift_value
                            if (marker == 2) {
                                System.out.println("Extracted Message Binary: " + binary_values);
                                System.out.println("Shift Value Binary: " + shift_value);
                                return decrypt();
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
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public String decrypt() {
        int shift = find_shift_value(); // Get shift value
        String original_string = get_encrypted_string(); // Get encrypted string

        StringBuilder result = new StringBuilder();

        for (char ch : original_string.toCharArray()) {
            if (ch == 10) { // Preserve newline
                result.append(ch);
            } else if (ch >= 32 && ch <= 126) { // Reverse shift within the printable ASCII range
                int originalChar = ((ch - 32 - shift + 95) % 95) + 32;
                result.append((char) originalChar);
            }
        }

        // Define the file to store the decrypted text
        File outputFile = new File("decoded_message.txt");

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(result.toString()); // Write the decrypted message to the file
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if file writing fails
        }

        return outputFile.getAbsolutePath(); // Return the absolute path of the file
    }

    public int find_shift_value() {
        String final_int_string = "";
        for (int i = 0; i < shift_value.size(); i++) {
            String bin = shift_value.get(i);
            char ch = (char) Integer.parseInt(bin, 2);
            final_int_string += Character.toString(ch);
        }

        return Integer.parseInt(final_int_string);
    }

    public String get_encrypted_string() {
        String final_encrypted_string = "";
        for (int i = 0; i < binary_values.size(); i++) {
            String bin = binary_values.get(i);
            char ch = (char) Integer.parseInt(bin, 2);
            final_encrypted_string += Character.toString(ch);
        }

        return final_encrypted_string;
    }
}