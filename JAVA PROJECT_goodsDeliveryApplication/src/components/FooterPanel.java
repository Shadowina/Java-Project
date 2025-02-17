package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Year;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FooterPanel extends JPanel {
    private final JLabel copyrightLabel;
    private final JPanel linksPanel;
    private final JLabel[] links;
    private final String[] linkTexts = {"About Us", "Contact", "Terms", "Privacy Policy"};

    public FooterPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(51, 153, 255));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Initialize components
        copyrightLabel = createCopyrightLabel();
        linksPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        linksPanel.setOpaque(false);

        // Create and add links
        links = new JLabel[linkTexts.length];
        setupLinks();

        // Add components to panel
        add(copyrightLabel, BorderLayout.WEST);
        add(linksPanel, BorderLayout.EAST);
    }

    private JLabel createCopyrightLabel() {
        JLabel label = new JLabel("© " + Year.now().getValue() + " GDA. All rights reserved.");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    private void setupLinks() {
        for (int i = 0; i < linkTexts.length; i++) {
            links[i] = createLinkLabel(linkTexts[i]);
            linksPanel.add(links[i]);

            // Add separator except for the last link
            if (i < linkTexts.length - 1) {
                JLabel separator = new JLabel("|");
                separator.setForeground(Color.WHITE);
                separator.setFont(new Font("Arial", Font.PLAIN, 12));
                linksPanel.add(separator);
            }
        }
    }

    private JLabel createLinkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 12));

        // Add hover effect
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                label.setForeground(new Color(230, 230, 230));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                label.setForeground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleLinkClick(text);
            }
        });

        return label;
    }

    private void handleLinkClick(String linkText) {
        SwingUtilities.invokeLater(() -> {
            String message = switch (linkText) {
                case "About Us" -> "Learn more about GDA and our mission.";
                case "Contact" -> "Get in touch with our support team.";
                case "Terms" -> "Read our terms of service.";
                case "Privacy Policy" -> "View our privacy policy.";
                default -> "Feature coming soon!";
            };

            JOptionPane.showMessageDialog(
                this,
                message,
                linkText,
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    // Method to update the copyright year if needed
    public void updateCopyrightYear() {
        copyrightLabel.setText("© " + Year.now().getValue() + " GDA. All rights reserved.");
    }

    // Method to add a new link if needed
    public void addLink(String linkText) {
        JLabel newLink = createLinkLabel(linkText);

        // Add separator first
        JLabel separator = new JLabel("|");
        separator.setForeground(Color.WHITE);
        separator.setFont(new Font("Arial", Font.PLAIN, 12));
        linksPanel.add(separator);

        // Add new link
        linksPanel.add(newLink);
        revalidate();
        repaint();
    }
}