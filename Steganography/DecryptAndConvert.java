import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DecryptAndConvert {

    // Returns the full path to the saved decoded message
    public static String decode(ArrayList<String> binaryValues, ArrayList<String> shiftValue) {
        String decryptedText = decrypt(binaryValues, shiftValue);
        File outputFile = new File("decoded_message.txt");

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(decryptedText);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outputFile.getAbsolutePath();
    }

    // Returns just the decrypted string (no file writing)
    public static String decrypt(ArrayList<String> binaryValues, ArrayList<String> shiftValue) {
        int shift = findShiftValue(shiftValue);
        String encrypted = getEncryptedString(binaryValues);
        StringBuilder result = new StringBuilder();

        for (char ch : encrypted.toCharArray()) {
            if (ch == 10) {
                result.append(ch);
            } else if (ch >= 32 && ch <= 126) {
                int originalChar = ((ch - 32 - shift + 95) % 95) + 32;
                result.append((char) originalChar);
            }
        }

        return result.toString();
    }

    public static int findShiftValue(ArrayList<String> shiftValue) {
        StringBuilder shiftStr = new StringBuilder();
        for (String bin : shiftValue) {
            shiftStr.append((char) Integer.parseInt(bin, 2));
        }
        return Integer.parseInt(shiftStr.toString());
    }

    public static String getEncryptedString(ArrayList<String> binaryValues) {
        StringBuilder encrypted = new StringBuilder();
        for (String bin : binaryValues) {
            encrypted.append((char) Integer.parseInt(bin, 2));
        }
        return encrypted.toString();
    }
}
