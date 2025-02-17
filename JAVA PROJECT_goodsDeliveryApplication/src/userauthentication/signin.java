package userauthentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import dbmanagement.DBManagementGDA;
import main.GDAApplication;
import utils.PasswordHasher;



public class signin extends JFrame implements ActionListener {
    private JButton loginButton, backButton;
    private JTextField userEmailField;
    private JPasswordField userPasswordField;
    private JLabel userEmailLabel, userPasswordLabel, messageLabel, signinLabel;
    private JLabel createAccountLink, showPasswordLink, orLabel;
    private JPanel mainPanel, loginPanel, buttonPanel;
    private JCheckBox rememberMeCheckbox;
    private Preferences prefs;
    private static final String PREF_EMAIL = "remembered_email";
    private static final String PREF_PASSWORD = "remembered_password";
    public static int loggedInUserId;

    private static final String SELECT_USER =
        "SELECT user_id, user_name, role, user_password FROM user WHERE user_email = ?";

    public signin() {
        this.setLayout(new BorderLayout());
        this.setTitle("Login");

        // Create main panel for centering
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 255));

        // Create the sign in label with enhanced styling
        signinLabel = new JLabel("Sign In");
        signinLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        signinLabel.setForeground(new Color(75, 0, 130));
        signinLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signinLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        // Create the login panel
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setPreferredSize(new Dimension(350, 400));
        loginPanel.setBackground(new Color(147, 112, 219));
        // Create rounded border
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(75, 0, 130), 2) {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(lineColor);
                    g2d.drawRoundRect(x, y, width - 1, height - 1, 20, 20);
                    g2d.dispose();
                }
            },
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Initialize components with styling
        userEmailLabel = createLabel("Email:");
        userPasswordLabel = createLabel("Password:");
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        userEmailField = createTextField();
        userPasswordField = new JPasswordField();
        styleTextField(userPasswordField);

        // Create buttons
        loginButton = createStyledButton("Sign in");
        backButton = createStyledButton("Back");

        // Create links
        showPasswordLink = createStyledLink("Show Password");
        createAccountLink = createStyledLink("Create Account");
        orLabel = new JLabel("or");
        orLabel.setForeground(Color.WHITE);
        orLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add components to login panel using GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridwidth = 2;

        addComponent(loginPanel, userEmailLabel, gbc, 0, 0, 1);
        addComponent(loginPanel, userEmailField, gbc, 1, 0, 1);
        addComponent(loginPanel, userPasswordLabel, gbc, 2, 0, 1);
        addComponent(loginPanel, userPasswordField, gbc, 3, 0, 1);

        // Add show password link (right-aligned)
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 15, 0);
        addComponent(loginPanel, showPasswordLink, gbc, 4, 0, 1);

        // Create button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        // Add button panel
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 15, 0);
        addComponent(loginPanel, buttonPanel, gbc, 6, 0, 2);

        // Add "or Create Account" section
        JPanel createAccountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        createAccountPanel.setOpaque(false);
        createAccountPanel.add(orLabel);
        createAccountPanel.add(createAccountLink);

        gbc.insets = new Insets(0, 0, 10, 0);
        addComponent(loginPanel, createAccountPanel, gbc, 7, 0, 2);

        // Add message label
        addComponent(loginPanel, messageLabel, gbc, 8, 0, 2);

        // Initialize preferences
        prefs = Preferences.userNodeForPackage(signin.class);

        // Create remember me checkbox
        rememberMeCheckbox = new JCheckBox("Remember Me");
        rememberMeCheckbox.setFont(new Font("Arial", Font.PLAIN, 12));
        rememberMeCheckbox.setForeground(Color.WHITE);
        rememberMeCheckbox.setOpaque(false);

        // Add remember me checkbox to login panel
        gbc.insets = new Insets(5, 0, 15, 0);
        addComponent(loginPanel, rememberMeCheckbox, gbc, 5, 0, 2);

        // Check for saved credentials
        String savedEmail = prefs.get(PREF_EMAIL, "");
        String savedPassword = prefs.get(PREF_PASSWORD, "");

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            userEmailField.setText(savedEmail);
            userPasswordField.setText(savedPassword);
            rememberMeCheckbox.setSelected(true);
        }

        // Add panels to frame
        mainPanel.add(loginPanel);
        this.add(signinLabel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);

        // Add event listeners
        loginButton.addActionListener(this);
        backButton.addActionListener(this);
        addShowPasswordListener();
        addCreateAccountListener();

        // Set up the frame
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(600, 650);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0,113,188));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(106, 90, 205));
            }
            @Override
			public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0,113,188));
            }
        });

        return button;
    }

    private JLabel createStyledLink(String text) {
        JLabel link = new JLabel(text);
        link.setFont(new Font("Arial", Font.PLAIN, 14));
        link.setForeground(Color.WHITE);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return link;
    }

    private void addComponent(JPanel panel, Component comp, GridBagConstraints gbc, int y, int x, int width) {
        gbc.gridy = y;
        gbc.gridx = x;
        gbc.gridwidth = width;
        panel.add(comp, gbc);
    }

    private void addShowPasswordListener() {
        showPasswordLink.addMouseListener(new MouseAdapter() {
            private boolean passwordVisible = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                passwordVisible = !passwordVisible;
                userPasswordField.setEchoChar(passwordVisible ? (char) 0 : '•');
                showPasswordLink.setText(passwordVisible ? "Hide Password" : "Show Password");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                showPasswordLink.setText("<html><u>" + showPasswordLink.getText() + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                showPasswordLink.setText(passwordVisible ? "Hide Password" : "Show Password");
            }
        });
    }

    private void addCreateAccountListener() {
        createAccountLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                new signup();
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                createAccountLink.setText("<html><u>Create Account</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createAccountLink.setText("Create Account");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String email = userEmailField.getText();
            String password = new String(userPasswordField.getPassword());

            // Debug print
            System.out.println("Attempting login with email: " + email);

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                messageLabel.setForeground(Color.RED);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DBManagementGDA.URL,
                                                           DBManagementGDA.USER,
                                                           DBManagementGDA.PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(SELECT_USER)) {

                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Debug print
                        System.out.println("User found in database");
                        handleLoginSuccess(rs, password);
                    } else {
                        System.out.println("No user found with email: " + email);
                        handleLoginFailure();
                    }
                }
            } catch (SQLException ex) {
                System.err.println("SQL Error: " + ex.getMessage());
                handleDatabaseError(ex);
            }
        } else if (e.getSource() == backButton) {
            setVisible(false);
            new welcomePage();
            dispose();
        }
    }

    private void handleLoginSuccess(ResultSet rs, String password) throws SQLException {
        String storedHash = rs.getString("user_password");
        String role = rs.getString("role").trim().toUpperCase();
        int userId = rs.getInt("user_id");

        // Debug prints
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Role: " + role);
        System.out.println("User ID: " + userId);

        boolean passwordMatch = PasswordHasher.verifyPassword(password, storedHash);
        System.out.println("Password match: " + passwordMatch);

        if (passwordMatch) {
            loggedInUserId = userId;
            handleRememberMe(password);
            redirectBasedOnRole(role, userId);
        } else {
            System.out.println("Password verification failed");
            handleLoginFailure();
        }
    }

    private void handleRememberMe(String password) {
        if (rememberMeCheckbox.isSelected()) {
            prefs.put(PREF_EMAIL, userEmailField.getText());
            prefs.put(PREF_PASSWORD, password);
        } else {
            prefs.remove(PREF_EMAIL);
            prefs.remove(PREF_PASSWORD);
        }
    }

    private void redirectBasedOnRole(String role, int userId) {
        dispose(); // Close the login window
        System.out.println("Redirecting user ID: " + userId + " with role: " + role);
        
        switch (role.toLowerCase()) {
            case "customer" -> {
                try (Connection conn = DBManagementGDA.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT customer_id FROM customer WHERE cst_user_id = ?")) {
                    
                    pstmt.setInt(1, userId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int customerId = rs.getInt("customer_id");
                            System.out.println("Found customer ID: " + customerId + " for user ID: " + userId);
                            GDAApplication.launchCustomerDashboard(customerId);
                        } else {
                            System.err.println("No customer found for user ID: " + userId);
                            JOptionPane.showMessageDialog(null,
                                "Customer account not found",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Database error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            case "scheduler" -> GDAApplication.launchSchedulerView(userId);
            case "driver" -> GDAApplication.launchDriverView(userId);
            default -> {
                System.err.println("Unknown role: " + role);
                JOptionPane.showMessageDialog(null,
                    "Unknown role: " + role,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLoginFailure() {
        messageLabel.setText("Invalid email or password");
        messageLabel.setForeground(Color.RED);
    }

    private void handleDatabaseError(SQLException ex) {
        messageLabel.setText("Database error: " + ex.getMessage());
        messageLabel.setForeground(Color.RED);
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new signin();
        });
    }
}

