package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import userauthentication.SignOut;

public class HeaderPanel extends JPanel {
    private final JPanel leftPanel;
    private final JPanel rightPanel;
    private final JLabel logoLabel;
    private final JLabel searchLabel;
    private final JLabel cartLabel;
    private final JLabel accountLabel;
    private final JPopupMenu accountMenu;
    private final JMenuItem signOutItem;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(51, 153, 255));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Initialize components
        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        logoLabel = createLogoLabel();
        searchLabel = createHeaderLabel("Search", "src/images/search_icon.png");
        cartLabel = createHeaderLabel("Cart", "src/images/cart_icon.png");
        accountLabel = createHeaderLabel("Account", "src/images/account_icon.png");

        // Initialize account menu
        accountMenu = new JPopupMenu();
        signOutItem = new JMenuItem("Sign Out");

        setupComponents();
        setupListeners();
    }

    private void setupComponents() {
        // Configure panels
        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);

        // Add components to panels
        leftPanel.add(logoLabel);

        rightPanel.add(searchLabel);
        rightPanel.add(cartLabel);
        rightPanel.add(accountLabel);

        // Add panels to header
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Setup account menu
        accountMenu.add(signOutItem);
    }

    private void setupListeners() {
        // Search functionality
        searchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showFeatureMessage("Search");
            }
        });

        // Cart functionality
        cartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showFeatureMessage("Cart");
            }
        });

        // Account menu functionality
        accountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                accountMenu.show(accountLabel, 0, accountLabel.getHeight());
            }
        });

        // Sign out functionality
        signOutItem.addActionListener(e -> {
            SignOut.getInstance().performSignOut(this);
        });
    }

    private JLabel createLogoLabel() {
        JLabel label = new JLabel("ezzyDelivery");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        return label;
    }

    private JLabel createHeaderLabel(String text, String iconPath) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));

        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
            label.setHorizontalTextPosition(SwingConstants.RIGHT);
            label.setIconTextGap(5);
        } catch (Exception e) {
            System.err.println("Error loading icon for " + text + ": " + e.getMessage());
        }

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
        });

        return label;
    }

    private void showFeatureMessage(String feature) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                this,
                feature + " feature coming soon!",
                feature,
                JOptionPane.INFORMATION_MESSAGE
            )
        );
    }

    // Method to update cart count if needed
    public void updateCartCount(int count) {
        if (count > 0) {
            cartLabel.setText("Cart (" + count + ")");
        } else {
            cartLabel.setText("Cart");
        }
    }

    // Method to handle navigation
    public void navigateTo(String destination) {
        switch (destination) {
            case "CART" -> {
                // Implement cart navigation
                showFeatureMessage("Cart");
            }
            case "SEARCH" -> {
                // Implement search navigation
                showFeatureMessage("Search");
            }
            case "ACCOUNT" -> {
                // Show account menu
                accountMenu.show(accountLabel, 0, accountLabel.getHeight());
            }
            default -> System.err.println("Unknown destination: " + destination);
        }
    }
}