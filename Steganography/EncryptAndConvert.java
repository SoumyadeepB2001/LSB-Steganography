public class EncryptAndConvert {
    
    // Encrypt the message using Caesar Cipher
    public static String encrypt(String msg, int shift) {
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
    public static String msg_to_binary_string(String msg, int shift) {

        String encrypted_msg = encrypt(msg, shift);

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
}
