import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class AudioDecode {

    private File encoded_file;
    private ArrayList<String> binary_values = new ArrayList<>();
    private ArrayList<String> shift_value = new ArrayList<>();

    public AudioDecode(File file) {
        this.encoded_file = file;
    }

    public String decode() {
        try (RandomAccessFile raf = new RandomAccessFile(encoded_file, "r")) {
            int[] formatInfo = findFmtChunk(raf);
            int bitDepth = formatInfo[0];
            int bytesPerSample = bitDepth / 8;
            int dataOffset = findDataChunk(raf);

            if (dataOffset == -1) {
                throw new Exception("No 'data' chunk found in the WAV file.");
            }

            // Read full file into memory
            byte[] fileBytes = new byte[(int) raf.length()];
            raf.seek(0);
            raf.readFully(fileBytes);

            int bitCount = 0;
            String binary_string = "";
            int marker = 0;

            for (int i = dataOffset; i < fileBytes.length; i += bytesPerSample) {
                int lsb = fileBytes[i] & 1;
                binary_string += lsb;
                bitCount++;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int[] findFmtChunk(RandomAccessFile raf) throws Exception {
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
        throw new Exception("WAV fmt chunk not found");
    }

    private int findDataChunk(RandomAccessFile raf) throws Exception {
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

    private int littleEndianToInt(byte[] data, int offset, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result |= (data[offset + i] & 0xFF) << (i * 8);
        }
        return result;
    }
}
