package userauthentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import dbmanagement.DBManagementGDA;
import main.GDAApplication;
import models.User;
import models.User.Role;
import utils.PasswordHasher;

public class signup extends JFrame {
    private JLabel signUpLabel, fullName, userEmail, userPhoneNo, userPassword, roleLabel;
    private JTextField fullNameField, userEmailField, userPhoneNoField, userPasswordField;
    private JRadioButton roleCustomer, roleDriver, roleScheduler;
    private ButtonGroup roleGroup;
    private JButton register, backButton;
    private JPanel inputPanel, rolePanel, mainPanel;
    private String truckRegNo = "";
    private String truckCapacity = "";

    public signup() {
        setupMainWindow();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        finalizeWindow();
    }

    private void setupMainWindow() {
        setLayout(new BorderLayout());
        setTitle("Sign Up Window");
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 255));
    }

    private void initializeComponents() {
        // Initialize labels
        signUpLabel = createSignUpLabel();
        fullName = createLabel("Full Name:");
        userEmail = createLabel("Email:");
        userPhoneNo = createLabel("Phone Number:");
        userPassword = createLabel("Password:");
        roleLabel = createLabel("Select Role:");

        // Initialize text fields
        fullNameField = createTextField();
        userEmailField = createTextField();
        userPhoneNoField = createTextField();
        userPasswordField = createTextField();

        // Initialize role components
        initializeRoleComponents();

        // Initialize register button
        register = createRegisterButton();

        // Initialize back button
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(Color.WHITE);

        // Initialize panels
        inputPanel = createInputPanel();
    }

    private JLabel createSignUpLabel() {
        JLabel label = new JLabel("Sign Up");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return label;
    }

    private void initializeRoleComponents() {
        rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.setOpaque(false);

        roleGroup = new ButtonGroup();
        roleCustomer = createRadioButton("Customer");
        roleDriver = createRadioButton("Driver");
        roleScheduler = createRadioButton("Scheduler");

        roleGroup.add(roleCustomer);
        roleGroup.add(roleDriver);
        roleGroup.add(roleScheduler);

        rolePanel.add(roleCustomer);
        rolePanel.add(roleDriver);
        rolePanel.add(roleScheduler);
    }

    private JButton createRegisterButton() {
        JButton button = new JButton("Register");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
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
                button.setBackground(Color.WHITE);
            }
        });

        button.addActionListener(e -> handleSignUp());

        return button;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(450, 450));
        panel.setBackground(new Color(147, 112, 219));
        panel.setBorder(BorderFactory.createLineBorder(new Color(75, 0, 130), 2));
        return panel;
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.weightx = 1.0;
        gbc.gridx = 0;  // Start from the first column
        gbc.gridy = 0;  // Start from the first row

        // Add components to input panel with proper ordering
        // Full Name
        addComponent(inputPanel, fullName, gbc, 0, 0);
        gbc.gridy++;  // Move to next row
        addComponent(inputPanel, fullNameField, gbc, 1, 0);
        gbc.gridy++;

        // Email
        addComponent(inputPanel, userEmail, gbc, 2, 0);
        gbc.gridy++;
        addComponent(inputPanel, userEmailField, gbc, 3, 0);
        gbc.gridy++;

        // Phone Number
        addComponent(inputPanel, userPhoneNo, gbc, 4, 0);
        gbc.gridy++;
        addComponent(inputPanel, userPhoneNoField, gbc, 5, 0);
        gbc.gridy++;

        // Password
        addComponent(inputPanel, userPassword, gbc, 6, 0);
        gbc.gridy++;
        addComponent(inputPanel, userPasswordField, gbc, 7, 0);
        gbc.gridy++;

        // Role
        addComponent(inputPanel, roleLabel, gbc, 8, 0);
        gbc.gridy++;
        addComponent(inputPanel, rolePanel, gbc, 9, 0);
        gbc.gridy++;

        // Register button
        gbc.insets = new Insets(20, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(register, gbc);

        // Add panels to frame
        mainPanel.add(inputPanel, new GridBagConstraints());
        add(signUpLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void finalizeWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));  // Slightly reduced font size
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(200, 25));  // Adjusted size
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return field;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(new Font("Arial", Font.PLAIN, 13));
        radio.setForeground(Color.BLACK);
        radio.setOpaque(false);
        radio.setFocusPainted(false);

        // Add listener for Driver role
        if (text.equals("Driver")) {
            radio.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    showDriverDetailsDialog();
                }
            });
        }

        return radio;
    }

    private void addComponent(JPanel panel, Component comp, GridBagConstraints gbc, int gridy, int gridx) {
        gbc.gridy = gridy;
        gbc.gridx = gridx;
        panel.add(comp, gbc);
    }

    private void showDriverDetailsDialog() {
        JDialog dialog = new JDialog(this, "Driver Details", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(245, 245, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField regNoField = new JTextField(15);
        JTextField capacityField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Truck Registration Number:"), gbc);
        gbc.gridx = 1;
        dialog.add(regNoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Truck Capacity:"), gbc);
        gbc.gridx = 1;
        dialog.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            if (regNoField.getText().isEmpty() || capacityField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate truck capacity is a number
            try {
                Double.parseDouble(capacityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Truck capacity must be a number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            truckRegNo = regNoField.getText();
            truckCapacity = capacityField.getText();
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleSignUp() {
        // Input validation
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBManagementGDA.URL, 
                                            DBManagementGDA.USER, 
                                            DBManagementGDA.PASSWORD);
            conn.setAutoCommit(false);  // Start transaction

            // First check if email exists
            if (emailExists(userEmailField.getText())) {
                JOptionPane.showMessageDialog(this, 
                    "Email already registered", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Role userRole = determineUserRole();
            if (userRole == null) {
                return;
            }

            // Create user object
            User user = createUser(userRole);
            if (user == null) {
                return;
            }

            // First insert into user table with role
            String userSql = "INSERT INTO user (user_name, user_email, phone_no, user_password, role) VALUES (?, ?, ?, ?, ?)";
            int userId;
            
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getUserName());
                pstmt.setString(2, user.getUserEmail());
                pstmt.setString(3, user.getPhoneNumber());
                pstmt.setString(4, user.getUserPassword());
                pstmt.setString(5, user.getRole().toString());  // Add role to user table
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }

            // Now handle role-specific tables
            boolean roleUpdateSuccess = false;
            switch (userRole) {
                case DRIVER -> {
                    String driverSql = "INSERT INTO driver (dr_user_id, dr_full_name, truck_registration, truck_capacity, driver_status) VALUES (?, ?, ?, ?, 'ACTIVE')";
                    try (PreparedStatement pstmt = conn.prepareStatement(driverSql)) {
                        pstmt.setInt(1, userId);
                        pstmt.setString(2, fullNameField.getText());
                        pstmt.setString(3, truckRegNo);
                        pstmt.setString(4, truckCapacity);
                        roleUpdateSuccess = pstmt.executeUpdate() > 0;
                    }
                }
                case CUSTOMER -> {
                    String customerSql = "INSERT INTO customer (cst_user_id, cst_full_name, cst_status) VALUES (?, ?, 'ACTIVE')";
                    try (PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
                        pstmt.setInt(1, userId);
                        pstmt.setString(2, fullNameField.getText());
                        roleUpdateSuccess = pstmt.executeUpdate() > 0;
                    }
                }
                case SCHEDULER -> {
                    String schedulerSql = "INSERT INTO scheduler (sch_user_id, sch_full_name, sch_email, sch_status) VALUES (?, ?, ?, 'ACTIVE')";
                    try (PreparedStatement pstmt = conn.prepareStatement(schedulerSql)) {
                        pstmt.setInt(1, userId);
                        pstmt.setString(2, fullNameField.getText());
                        pstmt.setString(3, userEmailField.getText());
                        roleUpdateSuccess = pstmt.executeUpdate() > 0;
                    }
                }
            }

            if (!roleUpdateSuccess) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, 
                    "Failed to update role information", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            conn.commit();  // Commit transaction
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            redirectToLogin();

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            handleSQLException(ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validateInputs() {
        if (fullNameField.getText().isEmpty() || userEmailField.getText().isEmpty() ||
            userPhoneNoField.getText().isEmpty() || userPasswordField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String password = userPasswordField.getText();
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 8 characters long",
                "Invalid Password",
                JOptionPane.ERROR_MESSAGE);
            userPasswordField.setText("");
            return false;
        }

        return true;
    }

    private Role determineUserRole() {
        if (roleCustomer.isSelected()) {
			return Role.CUSTOMER;
		}
        if (roleDriver.isSelected()) {
            if (truckRegNo.isEmpty() || truckCapacity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide truck details", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return Role.DRIVER;
        }
        if (roleScheduler.isSelected()) {
			return Role.SCHEDULER;
		}

        JOptionPane.showMessageDialog(this, "Please select a role", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    private User createUser(Role userRole) {
        String hashedPassword = PasswordHasher.hashPassword(userPasswordField.getText());
        if (hashedPassword == null) {
            JOptionPane.showMessageDialog(this, "Error hashing password", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new User(
            fullNameField.getText(),
            userEmailField.getText(),
            userPhoneNoField.getText(),
            hashedPassword,
            userRole
        );
    }

    private void handleSQLException(SQLException ex) {
        if (ex.getMessage().contains("Duplicate entry")) {
            JOptionPane.showMessageDialog(this, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        ex.printStackTrace();
    }

    private void redirectToLogin() {
        SwingUtilities.invokeLater(() -> {
            dispose(); // First dispose the signup window
            GDAApplication.launchLogin(); // Use the centralized login launcher
        });
    }

    private void setupEventHandlers() {
        register.addActionListener(e -> handleSignUp());

        // Add back button handler if needed
        backButton.addActionListener(e -> {
            new welcomePage().setVisible(true);
            dispose();
        });
    }

    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE user_email = ?";
        try (Connection conn = DriverManager.getConnection(DBManagementGDA.URL, DBManagementGDA.USER, DBManagementGDA.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(signup::new);
    }
}
