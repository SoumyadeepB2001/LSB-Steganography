import javax.swing.*;
import java.awt.*;

class AudioDecodePanel extends JPanel {
    public AudioDecodePanel(Steganography stegoApp) {
        setLayout(new BorderLayout());

        JPanel navBar = NavBar.createNavBar(stegoApp, "Audio Decode");
        add(navBar, BorderLayout.NORTH); // Navbar at the top

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPanel.setBackground(Color.LIGHT_GRAY);
        contentPanel.add(new JLabel("Audio Decoding Panel"));
        add(contentPanel, BorderLayout.CENTER); // Rest of the content
    }
}
