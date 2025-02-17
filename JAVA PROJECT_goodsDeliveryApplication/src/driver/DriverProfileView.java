package driver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import components.BaseProfileView;

public class DriverProfileView extends BaseProfileView {
    private JLabel nameLabel, emailLabel, phoneLabel, truckRegLabel, capacityLabel;
    private JTextField nameField, emailField, phoneField, truckRegField, capacityField;
    private boolean isEditing = false;
    
    public DriverProfileView(int userId) {
        super(userId);
    }
    
    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        // Set a modern look for the content panel
        contentPanel.setBackground(new Color(240, 240, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Style the title
        titleLabel.setText("Driver Profile");
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        
        // Style the edit button
        editButton.setBackground(new Color(52, 152, 219)); // Blue
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        // Initialize fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        truckRegField = new JTextField(20);
        capacityField = new JTextField(20);

        // Style all text fields
        styleTextField(nameField);
        styleTextField(emailField);
        styleTextField(phoneField);
        styleTextField(truckRegField);
        styleTextField(capacityField);

        // Create form with styled components
        addStyledFormRow("Full Name:", nameField);
        addStyledFormRow("Email:", emailField);
        addStyledFormRow("Phone:", phoneField);
        addStyledFormRow("Truck Registration:", truckRegField);
        addStyledFormRow("Truck Capacity (Kg):", capacityField);

        // Add some vertical spacing between rows
        addVerticalSpacing(15);
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
    }

    private void addStyledFormRow(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row.setBackground(new Color(240, 240, 245));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(44, 62, 80));
        label.setPreferredSize(new Dimension(150, 35));
        
        row.add(label);
        row.add(field);
        contentPanel.add(row);
    }

    private void addVerticalSpacing(int height) {
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(1, height));
        spacer.setBackground(new Color(240, 240, 245));
        contentPanel.add(spacer);
    }

    @Override
    protected void loadUserData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT u.user_name, u.user_email, u.phone_no, " +
                        "d.dr_full_name, d.truck_registration, d.truck_capacity " +
                        "FROM user u " +
                        "JOIN driver d ON u.user_id = d.dr_user_id " +
                        "WHERE d.dr_user_id = ? AND d.driver_status = 'Active'";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                nameField.setText(rs.getString("dr_full_name"));
                emailField.setText(rs.getString("user_email"));
                phoneField.setText(rs.getString("phone_no"));
                truckRegField.setText(rs.getString("truck_registration"));
                capacityField.setText(String.valueOf(rs.getInt("truck_capacity")));
            } else {
                JOptionPane.showMessageDialog(this, "Driver not found or inactive", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading driver data: " + e.getMessage(),
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
            editButton.setBackground(new Color(46, 204, 113)); // Green
        } else {
            // Save changes
            saveChanges();
            setFieldsEditable(false);
            editButton.setText("Modify");
            editButton.setBackground(new Color(52, 152, 219)); // Blue
        }
        isEditing = !isEditing;
    }
    
    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        truckRegField.setEditable(editable);
        capacityField.setEditable(editable);
    }
    
    private void saveChanges() {
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psDriver = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // Update user table
            String userSql = "UPDATE user SET user_email=?, phone_no=? WHERE user_id=?";
            psUser = conn.prepareStatement(userSql);
            psUser.setString(1, emailField.getText().trim());
            psUser.setString(2, phoneField.getText().trim());
            psUser.setInt(3, userId);
            
            // Update driver table
            String driverSql = "UPDATE driver SET dr_full_name=?, truck_registration=?, " +
                             "truck_capacity=? WHERE dr_user_id=? AND driver_status='Active'";
            psDriver = conn.prepareStatement(driverSql);
            psDriver.setString(1, nameField.getText().trim());
            psDriver.setString(2, truckRegField.getText().trim());
            psDriver.setInt(3, Integer.parseInt(capacityField.getText().trim()));
            psDriver.setInt(4, userId);
            
            int userRowsAffected = psUser.executeUpdate();
            int driverRowsAffected = psDriver.executeUpdate();
            
            if (userRowsAffected > 0 && driverRowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "No changes were made. Driver not found or inactive.", 
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for truck capacity",
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, psUser, null);
            if (psDriver != null) {
                try {
                    psDriver.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty() || 
            emailField.getText().trim().isEmpty() || 
            phoneField.getText().trim().isEmpty() ||
            truckRegField.getText().trim().isEmpty() ||
            capacityField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "All fields are required", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "Truck capacity must be a positive number",
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for truck capacity",
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
