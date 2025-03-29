import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Steganography {
    private JFrame frame;
    private File fi;
    private JLayeredPane layeredPane;
    private JPanel encodePanel, decodePanel, sendPanel, navBar;
    private JButton btnEncode, btnDecode, btnSend;
    private Color activeTabColor;

    public Steganography() {
        initComponents(); // Initialize components
        frame.setVisible(true); // Show the frame
        switchPanel(encodePanel, btnEncode); // Set initial view
    }

    private void initComponents() {
        frame = new JFrame("Steganography");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 480);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        activeTabColor = new Color(190, 213, 235); // Active tab color

        // Navigation bar
        navBar = new JPanel(new GridLayout(1, 3));

        btnEncode = new JButton("Encode");
        btnDecode = new JButton("Decode");
        btnSend = new JButton("Send");

        navBar.add(btnEncode);
        navBar.add(btnDecode);
        navBar.add(btnSend);

        // LayeredPane setup
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(720, 480));

        encodePanel = createEncodePanel();
        decodePanel = createDecodePanel();
        sendPanel = createSendPanel();

        // Add panels to the layered pane
        layeredPane.add(encodePanel, Integer.valueOf(1));
        layeredPane.add(decodePanel, Integer.valueOf(2));
        layeredPane.add(sendPanel, Integer.valueOf(3));

        // Attach event handlers using lambdas
        attachEventHandlers();

        // Add components to frame
        frame.add(navBar, BorderLayout.NORTH);
        frame.add(layeredPane, BorderLayout.CENTER);
    }

    private void attachEventHandlers() {
        btnEncode.addActionListener(e -> switchPanel(encodePanel, btnEncode));
        btnDecode.addActionListener(e -> switchPanel(decodePanel, btnDecode));
        btnSend.addActionListener(e -> switchPanel(sendPanel, btnSend));
    }

    private JPanel createEncodePanel() {
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 720, 480);
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY); // Set background color

        JLabel lbl_msg = new JLabel("Enter your message below:");
        lbl_msg.setBounds(50, 30, 200, 25);

        JTextArea txt_msg = new JTextArea();
        txt_msg.setLineWrap(true);
        txt_msg.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txt_msg);
        scrollPane.setBounds(50, 60, 600, 120);

        JLabel lbl_cipher = new JLabel("Enter shift value for Caesar cipher:");
        lbl_cipher.setBounds(50, 200, 250, 25);

        JTextField txt_cipher = new JTextField();
        txt_cipher.setBounds(300, 200, 200, 25);

        txt_cipher.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        JButton btn_file = new JButton("Choose image file");
        btn_file.setBounds(50, 250, 200, 30);

        JLabel file_loc = new JLabel("Choose a PNG/JPG/JPEG image to encode your message");
        file_loc.setBounds(300, 250, 350, 25);

        JButton btn_encode = new JButton("Encode");
        btn_encode.setBounds(50, 300, 200, 30);

        JLabel new_file_loc = new JLabel();
        new_file_loc.setBounds(300, 300, 350, 30);

        panel.add(lbl_msg);
        panel.add(scrollPane);
        panel.add(lbl_cipher);
        panel.add(txt_cipher);
        panel.add(btn_file);
        panel.add(file_loc);
        panel.add(btn_encode);
        panel.add(new_file_loc);

        // File Selection
        btn_file.addActionListener(e -> {
            fi = selectFile();
            if (fi != null) {
                file_loc.setText(fi.getAbsolutePath());
            }
        });

        // Encoding Action
        btn_encode.addActionListener(e -> {
            String msg = txt_msg.getText();
            int cipher;

            try {
                cipher = Integer.parseInt(txt_cipher.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid shift value!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fi == null) {
                JOptionPane.showMessageDialog(null, "Please select a valid file!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String s = encode(msg, cipher, fi);
            new_file_loc.setText(s);
        });

        return panel;
    }

    public File selectFile() {
        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "png", "jpg",
                "jpeg");
        j.setFileFilter(filter);
        j.setAcceptAllFileFilterUsed(false);

        int r = j.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            File file = j.getSelectedFile();
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return file;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid file type! Please select a PNG or JPG image.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    public String encode(String msg, int cipher, File file) {
        Encode encode = new Encode(msg, cipher, file);
        return encode.encode();
    }

    public String decode(File file) {
        Decode decode = new Decode(file);
        return decode.decode();
    }

    private JPanel createDecodePanel() {
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 720, 480);
        panel.setLayout(null);
        panel.setBackground(Color.LIGHT_GRAY); // Set background color

        JButton btn_file = new JButton("Choose image file");
        btn_file.setBounds(50, 50, 200, 30);

        JLabel file_loc = new JLabel("Choose a PNG/JPG/JPEG image to decode");
        file_loc.setBounds(300, 50, 350, 25);

        JButton btn_decode = new JButton("Decode");
        btn_decode.setBounds(50, 100, 200, 30);

        JLabel new_file_loc = new JLabel();
        new_file_loc.setBounds(300, 100, 350, 30);

        JTextArea txt_msg = new JTextArea();
        txt_msg.setLineWrap(true);
        txt_msg.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txt_msg);
        txt_msg.setEditable(false);
        scrollPane.setBounds(50, 150, 600, 150);

        panel.add(btn_file);
        panel.add(file_loc);
        panel.add(btn_decode);
        panel.add(new_file_loc);
        panel.add(scrollPane);

        btn_file.addActionListener(e -> {
            fi = selectFile();
            if (fi != null) {
                file_loc.setText(fi.getAbsolutePath());
            }
        });

        btn_decode.addActionListener(e -> {
            String file_location = decode(fi);
            new_file_loc.setText(file_location);
            readTextFile(txt_msg, file_location);
        });

        return panel;
    }

    private JPanel createSendPanel() {
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 720, 480);
        panel.setBackground(Color.LIGHT_GRAY);
        return panel;
    }

    private void switchPanel(JPanel panelToShow, JButton btnHighlight) {
        encodePanel.setVisible(false);
        decodePanel.setVisible(false);
        sendPanel.setVisible(false);

        btnEncode.setBackground(Color.WHITE);
        btnDecode.setBackground(Color.WHITE);
        btnSend.setBackground(Color.WHITE);

        panelToShow.setVisible(true);
        btnHighlight.setBackground(activeTabColor);
    }

    void readTextFile(JTextArea txt_msg, String file_location)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file_location))) {
            String line;
            StringBuilder content = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            txt_msg.setText(content.toString()); // Set text to JTextArea
        } catch (IOException e) {
            txt_msg.setText("Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Steganography::new);
    }
}