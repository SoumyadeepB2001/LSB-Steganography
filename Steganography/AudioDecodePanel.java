import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class AudioDecodePanel extends JPanel {
    private JLabel lblFileLocation;
    private JLabel lblDecodedFileLocation;
    private JTextArea txtDecodedMessage;
    private File selectedFile;
    private final Steganography stegoApp;

    public AudioDecodePanel(Steganography stegoApp) {
        this.stegoApp = stegoApp;
        setLayout(null);

        JPanel navBar = NavBar.createNavBar(stegoApp, "Audio Decode");
        add(navBar);

        JPanel contentPanel = createContentPanel();
        contentPanel.setBounds(0, 28, 720, 452);
        add(contentPanel);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);

        JButton btnFile = new JButton("Choose .wav file");
        btnFile.setBounds(50, 50, 200, 30);
        btnFile.addActionListener(e -> selectFile());
        panel.add(btnFile);

        lblFileLocation = new JLabel("Choose a WAV audio file to decode");
        lblFileLocation.setBounds(300, 50, 350, 25);
        panel.add(lblFileLocation);

        JButton btnDecode = new JButton("Decode");
        btnDecode.setBounds(50, 100, 200, 30);
        btnDecode.addActionListener(e -> decodeMessage());
        panel.add(btnDecode);

        lblDecodedFileLocation = new JLabel();
        lblDecodedFileLocation.setBounds(300, 100, 350, 30);
        panel.add(lblDecodedFileLocation);

        txtDecodedMessage = new JTextArea();
        txtDecodedMessage.setLineWrap(true);
        txtDecodedMessage.setWrapStyleWord(true);
        txtDecodedMessage.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtDecodedMessage);
        scrollPane.setBounds(50, 150, 600, 150);
        panel.add(scrollPane);

        return panel;
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Audio Files (*.wav)", "wav");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".wav")) {
                lblFileLocation.setText(selectedFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(null, "Invalid file type! Please select a WAV file.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                selectedFile = null;
            }
        }
    }

    private void decodeMessage() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AudioDecode decode = new AudioDecode(selectedFile);
        String decodedFilePath = decode.decode();
        lblDecodedFileLocation.setText(decodedFilePath);
        readTextFile(txtDecodedMessage, decodedFilePath);
    }

    private void readTextFile(JTextArea textArea, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            textArea.setText(content.toString());
        } catch (IOException e) {
            textArea.setText("Error reading file: " + e.getMessage());
        }
    }
}
