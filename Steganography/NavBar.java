import javax.swing.*;
import java.awt.*;

public class NavBar {
    public static JPanel createNavBar(Steganography stegoApp, String activePanel) {

        JPanel navBar = new JPanel(new GridLayout(1, 4));
        navBar.setBounds(0, 0, 720, 28);

        JButton btnImageEncode = new JButton("Image Encode");
        JButton btnImageDecode = new JButton("Image Decode");
        JButton btnAudioEncode = new JButton("Audio Encode");
        JButton btnAudioDecode = new JButton("Audio Decode");

        btnImageEncode.setBackground(Color.WHITE);
        btnImageDecode.setBackground(Color.WHITE);
        btnAudioEncode.setBackground(Color.WHITE);
        btnAudioDecode.setBackground(Color.WHITE);

        Color activeTabColor = new Color(0, 24, 46);

        if (activePanel.equals("Image Encode")) {
            btnImageEncode.setBackground(activeTabColor);
            btnImageEncode.setEnabled(false);
        } else {
            btnImageEncode.addActionListener(e -> stegoApp.switchPanel(new ImageEncodePanel(stegoApp)));
        }

        if (activePanel.equals("Image Decode")) {
            btnImageDecode.setBackground(activeTabColor);
            btnImageDecode.setEnabled(false);
        } else {
            btnImageDecode.addActionListener(e -> stegoApp.switchPanel(new ImageDecodePanel(stegoApp)));
        }

        if (activePanel.equals("Audio Encode")) {
            btnAudioEncode.setBackground(activeTabColor);
            btnAudioEncode.setEnabled(false);
        } else {
            btnAudioEncode.addActionListener(e -> stegoApp.switchPanel(new AudioEncodePanel(stegoApp)));
        }

        if (activePanel.equals("Audio Decode")) {
            btnAudioDecode.setBackground(activeTabColor);
            btnAudioDecode.setEnabled(false);
        } else {
            btnAudioDecode.addActionListener(e -> stegoApp.switchPanel(new AudioDecodePanel(stegoApp)));
        }

        navBar.add(btnImageEncode);
        navBar.add(btnImageDecode);
        navBar.add(btnAudioEncode);
        navBar.add(btnAudioDecode);

        return navBar;
    }
}
