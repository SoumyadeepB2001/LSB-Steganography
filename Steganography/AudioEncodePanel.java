import javax.swing.*;
import java.awt.*;

class AudioEncodePanel extends JPanel {
    public AudioEncodePanel(Steganography stegoApp) {
        setLayout(new BorderLayout());

        JPanel navBar = NavBar.createNavBar(stegoApp, "Audio Encode");
        add(navBar, BorderLayout.NORTH); // Navbar at the top

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPanel.setBackground(Color.LIGHT_GRAY);
        contentPanel.add(new JLabel("Audio Encoding Panel"));
        add(contentPanel, BorderLayout.CENTER); // Rest of the content
    }
}
