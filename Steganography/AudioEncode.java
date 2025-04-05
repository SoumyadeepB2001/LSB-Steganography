import java.io.*;

public class AudioEncode {

    File originalFile;
    File encodedFile;
    String binaryMsg;

    public AudioEncode(String message, int cipherShift, File inputFile) {
        originalFile = inputFile;
        binaryMsg = EncryptAndConvert.msg_to_binary_string(message, cipherShift);
        System.out.println(binaryMsg);
    }

    public String encode() {
        try (RandomAccessFile raf = new RandomAccessFile(originalFile, "r")) {
            int[] formatInfo = findFmtChunk(raf);
            int bitDepth = formatInfo[0];
            int bytesPerSample = bitDepth / 8;
            int dataOffset = findDataChunk(raf); // Points to audio data

            if (dataOffset == -1) {
                throw new IOException("No 'data' chunk found in the WAV file.");
            }

            // Read full file into memory
            byte[] fileBytes = new byte[(int) raf.length()];
            raf.seek(0);
            raf.readFully(fileBytes);

            // Embed binary string into LSBs
            int bitIndex = 0;
            for (int i = dataOffset; i < fileBytes.length && bitIndex < binaryMsg.length(); i += bytesPerSample) {
                fileBytes[i] &= 0xFE; // Clear LSB
                fileBytes[i] |= (binaryMsg.charAt(bitIndex) == '1') ? 1 : 0;
                bitIndex++;
            }

            // Save encoded file
            encodedFile = new File("encoded_audio.wav");
            try (FileOutputStream fos = new FileOutputStream(encodedFile)) {
                fos.write(fileBytes);
            }

            return "Audio encoded successfully: " + encodedFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int[] findFmtChunk(RandomAccessFile raf) throws IOException {
        raf.seek(12); // Skip "RIFF" and "WAVE"
        byte[] chunkHeader = new byte[8];

        while (raf.read(chunkHeader) == 8) {
            String chunkID = new String(chunkHeader, 0, 4);
            int chunkSize = littleEndianToInt(chunkHeader, 4, 4);

            if ("fmt ".equals(chunkID)) {
                byte[] fmtData = new byte[chunkSize];
                raf.readFully(fmtData);
                int bitDepth = ((fmtData[14] & 0xFF) | (fmtData[15] & 0xFF) << 8);
                return new int[]{bitDepth};
            }
            raf.seek(raf.getFilePointer() + chunkSize);
        }
        throw new IOException("WAV fmt chunk not found");
    }

    private static int findDataChunk(RandomAccessFile raf) throws IOException {
        raf.seek(12); // Skip "RIFF" and "WAVE"
        byte[] chunkHeader = new byte[8];

        while (raf.read(chunkHeader) == 8) {
            String chunkID = new String(chunkHeader, 0, 4);
            int chunkSize = littleEndianToInt(chunkHeader, 4, 4);

            if ("data".equals(chunkID)) {
                return (int) raf.getFilePointer();
            }
            raf.seek(raf.getFilePointer() + chunkSize);
        }
        return -1;
    }

    private static int littleEndianToInt(byte[] data, int offset, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result |= (data[offset + i] & 0xFF) << (i * 8);
        }
        return result;
    }
}
