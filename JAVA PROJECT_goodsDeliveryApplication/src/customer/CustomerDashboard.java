package customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import components.FooterPanel;
import components.HeaderPanel;

public class CustomerDashboard extends JFrame {
    private final int customerId;
    private final HeaderPanel headerPanel;
    private final FooterPanel footerPanel;
    private final JPanel mainPanel;
    private final JPanel dashboardPanel;
    

    public CustomerDashboard(int customerId) {
        this.customerId = customerId;
        this.headerPanel = new HeaderPanel();
        this.footerPanel = new FooterPanel();
        this.mainPanel = new JPanel(new BorderLayout());
        this.dashboardPanel = new JPanel(new GridBagLayout());

        setupUI();
        setupDashboardButtons();
        configureFrame();
        setVisible(true);
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Main content area styling
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Dashboard panel styling
        dashboardPanel.setBackground(Color.WHITE);

        // Add welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Your Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(dashboardPanel, BorderLayout.CENTER);

        // Assemble the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupDashboardButtons() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create and add dashboard buttons
        addDashboardButton("View Products", 0, 0, gbc, e -> openProductView());
        addDashboardButton("My Cart", 0, 1, gbc, e -> openCartView());
        addDashboardButton("Order History", 1, 0, gbc, e -> openOrderHistory());
        addDashboardButton("My Profile", 1, 1, gbc, e -> openProfile());
    }

    private void addDashboardButton(String text, int row, int col,
                                  GridBagConstraints gbc, java.awt.event.ActionListener listener) {
        JButton button = createStyledButton(text);
        button.addActionListener(listener);

        gbc.gridx = col;
        gbc.gridy = row;
        dashboardPanel.add(button, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 100));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(51, 153, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204));
            }

            @Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 153, 255));
            }
        });

        return button;
    }

    private void configureFrame() {
        setTitle("Customer Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    // Navigation methods
    private void openProductView() {
        dispose();
        new ProductView(customerId).setVisible(true);
    }

    private void openCartView() {
        dispose(); // Close the current dashboard
        new CartView(customerId).setVisible(true);
    }

    private void openOrderHistory() {
        // TODO: Implement order history navigation
        JOptionPane.showMessageDialog(this, "Opening Order History...");
    }

    private void openProfile() {
        dispose(); // Close the dashboard
        JFrame frame = new JFrame("Customer Profile");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null); // Center on screen
        
        CustomerProfileView profileView = new CustomerProfileView(customerId);
        frame.add(profileView);
        frame.setVisible(true);

        // Add window listener to handle when profile is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // Reopen the dashboard when profile is closed
                new CustomerDashboard(customerId).setVisible(true);
            }
        });
    }
}