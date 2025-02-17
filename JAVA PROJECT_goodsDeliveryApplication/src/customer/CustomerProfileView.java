package customer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import components.BaseProfileView;

public class CustomerProfileView extends BaseProfileView {
    private JLabel nameLabel, emailLabel, phoneLabel;
    private JTextField nameField, emailField, phoneField;
    private boolean isEditing = false;
    private JButton changePasswordButton;

    public CustomerProfileView(int userId) {
        super(userId);
        
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        // Set a modern look for the content panel
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Style the title
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        
        // Style the edit button
        editButton.setBackground(new Color(70, 130, 180)); // Steel blue
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        // Initialize and style fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);

        // Style the text fields
        styleTextField(nameField);
        styleTextField(emailField);
        styleTextField(phoneField);

        // Create form with styled components
        addStyledFormRow("Full Name:", nameField);
        addStyledFormRow("Email:", emailField);
        addStyledFormRow("Phone:", phoneField);

        // Add some vertical spacing between rows
        addVerticalSpacing(20);

        // Add Change Password button
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBackground(new Color(70, 130, 180));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        // Add button to a new panel below the form
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(changePasswordButton);
        contentPanel.add(buttonPanel);
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void addStyledFormRow(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row.setBackground(new Color(245, 245, 245));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));
        label.setPreferredSize(new Dimension(100, 35));
        
        row.add(label);
        row.add(field);
        contentPanel.add(row);
    }

    private void addVerticalSpacing(int height) {
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(1, height));
        spacer.setBackground(new Color(245, 245, 245));
        contentPanel.add(spacer);
    }

    @Override
    protected void loadUserData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT c.cst_full_name, u.user_email, u.phone_no " +
                        "FROM customer c " +
                        "JOIN user u ON c.cst_user_id = u.user_id " +
                        "WHERE c.customer_id = ? AND c.cst_status = 'ACTIVE'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("cst_full_name"));
                emailField.setText(rs.getString("user_email"));
                phoneField.setText(rs.getString("phone_no"));
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found or inactive", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customer data: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, ps, rs);
        }
    }

    @Override
    protected void handleEdit() {
        if (!isEditing) {
            // Enter edit mode
            setFieldsEditable(true);
            editButton.setText("Save Changes");
            editButton.setBackground(new Color(46, 139, 87)); // Sea green
        } else {
            // Save changes
            saveChanges();
            setFieldsEditable(false);
            editButton.setText("Modify");
            editButton.setBackground(new Color(51, 153, 255)); 
        }
        isEditing = !isEditing;
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
    }

    private void saveChanges() {
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        PreparedStatement psCustomer = null;
        PreparedStatement psUser = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // First get the cst_user_id for this customer
            String getUserIdSql = "SELECT cst_user_id FROM customer WHERE customer_id = ?";
            PreparedStatement psGetUserId = conn.prepareStatement(getUserIdSql);
            psGetUserId.setInt(1, userId);
            ResultSet rs = psGetUserId.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Customer not found");
            }
            int cstUserId = rs.getInt("cst_user_id");

            // Update customer table
            String sqlCustomer = "UPDATE customer SET cst_full_name=? " +
                               "WHERE customer_id=? AND cst_status='ACTIVE'";
            psCustomer = conn.prepareStatement(sqlCustomer);
            psCustomer.setString(1, nameField.getText().trim());
            psCustomer.setInt(2, userId);
            
            // Update user table
            String sqlUser = "UPDATE user SET user_email=?, phone_no=? " +
                           "WHERE user_id=?";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, emailField.getText().trim());
            psUser.setString(2, phoneField.getText().trim());
            psUser.setInt(3, cstUserId);
            
            int customerRowsAffected = psCustomer.executeUpdate();
            int userRowsAffected = psUser.executeUpdate();
            
            if (customerRowsAffected > 0 && userRowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "No changes were made. Customer not found or inactive.", 
                                            "Warning", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, psCustomer, null);
            if (psUser != null) {
                try {
                    psUser.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty() || 
            emailField.getText().trim().isEmpty() || 
            phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Create password fields
        JPasswordField currentPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        
        // Style the password fields
        styleTextField(currentPasswordField);
        styleTextField(newPasswordField);
        styleTextField(confirmPasswordField);
        
        // Add components to panel
        panel.add(new JLabel("Current Password:"));
        panel.add(currentPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Validate inputs
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All fields are required", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "New passwords do not match", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password in database
            updatePassword(currentPassword, newPassword);
        }
    }

    private void updatePassword(String currentPassword, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = getConnection();
            
            // First verify current password using hashed value
            String verifySQL = "SELECT u.user_password FROM user u " +
                             "JOIN customer c ON u.user_id = c.cst_user_id " +
                             "WHERE c.customer_id = ?";
            
            ps = conn.prepareStatement(verifySQL);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String storedHashedPassword = rs.getString("user_password");
                String hashedCurrentPassword = hashPassword(currentPassword);
                
                // Debug prints
                System.out.println("Stored Hashed Password: " + storedHashedPassword);
                System.out.println("Current Hashed Password: " + hashedCurrentPassword);
                System.out.println("Customer ID: " + userId);
                
                if (!storedHashedPassword.equals(hashedCurrentPassword)) {
                    JOptionPane.showMessageDialog(this, 
                        "Current password is incorrect", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                System.out.println("No user found for customer ID: " + userId);
                return;
            }
            
            // Update password with hashed new password
            String updateSQL = "UPDATE user u " +
                             "JOIN customer c ON u.user_id = c.cst_user_id " +
                             "SET u.user_password = ? " +
                             "WHERE c.customer_id = ?";
            
            ps = conn.prepareStatement(updateSQL);
            ps.setString(1, hashPassword(newPassword));
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Password updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update password", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating password: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, ps, null);
        }
    }

    private String hashPassword(String password) {
        try {
            // Create SHA-256 hash
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            
            // Convert to Base64
            return java.util.Base64.getEncoder().encodeToString(hash);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
