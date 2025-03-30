import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

class ImageEncodePanel extends JPanel {
    private JTextArea txtMessage;
    private JTextField txtCipher;
    private JLabel lblFileLocation;
    private JLabel lblNewFileLocation;
    private File selectedFile;
    private final Steganography stegoApp;

    public ImageEncodePanel(Steganography stegoApp) {
        this.stegoApp = stegoApp;
        setLayout(null);

        JPanel navBar = NavBar.createNavBar(stegoApp, "Image Encode");
        add(navBar);

        JPanel contentPanel = createContentPanel();
        contentPanel.setBounds(0, 28, 720, 452);
        add(contentPanel);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY);

        JLabel lblMsg = new JLabel("Enter your message below:");
        lblMsg.setBounds(50, 30, 200, 25);
        panel.add(lblMsg);

        txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtMessage);
        scrollPane.setBounds(50, 60, 600, 120);
        panel.add(scrollPane);

        JLabel lblCipher = new JLabel("Enter shift value for Caesar cipher:");
        lblCipher.setBounds(50, 200, 250, 25);
        panel.add(lblCipher);

        txtCipher = new JTextField();
        txtCipher.setBounds(300, 200, 200, 25);
        txtCipher.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        panel.add(txtCipher);

        JButton btnFile = new JButton("Choose image file");
        btnFile.setBounds(50, 250, 200, 30);
        btnFile.addActionListener(e -> selectFile());
        panel.add(btnFile);

        lblFileLocation = new JLabel("Choose a PNG/JPG/JPEG image to encode your message");
        lblFileLocation.setBounds(300, 250, 350, 25);
        panel.add(lblFileLocation);

        JButton btnEncode = new JButton("Encode");
        btnEncode.setBounds(50, 300, 200, 30);
        btnEncode.addActionListener(e -> encodeMessage());
        panel.add(btnEncode);

        lblNewFileLocation = new JLabel();
        lblNewFileLocation.setBounds(300, 300, 350, 30);
        panel.add(lblNewFileLocation);

        return panel;
    }

    private void selectFile() {
        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "png", "jpg",
                "jpeg");
        j.setFileFilter(filter);
        j.setAcceptAllFileFilterUsed(false);
        int r = j.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = j.getSelectedFile();
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            {
                lblFileLocation.setText(selectedFile.getAbsolutePath());
            }

            else {
                JOptionPane.showMessageDialog(null, "Invalid file type! Please select a PNG or JPEG image.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                selectedFile = null;
            }
        }
    }

    private void encodeMessage() {
        String msg = txtMessage.getText();
        int cipher;
        try {
            cipher = Integer.parseInt(txtCipher.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid shift value!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImageEncode encode = new ImageEncode(msg, cipher, selectedFile);
        String encodedFilePath = encode.encode();
        lblNewFileLocation.setText(encodedFilePath);
    }
}
