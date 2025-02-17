package scheduler;

import java.awt.BorderLayout;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import components.BaseProfileView;

public class SchedulerProfileView extends BaseProfileView {
    private JTextField nameField, emailField, phoneField, passwordField;
    private boolean isEditing = false;

    public SchedulerProfileView(int userId) {
        super(userId);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        // Set a modern look for the content panel
        contentPanel.setBackground(new Color(240, 240, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout(15, 0));
        titlePanel.setBackground(new Color(240, 240, 245));
        
        // Style the title
        titleLabel.setText("Scheduler Profile");
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Style the edit button
        editButton.setText("Modify");
        editButton.setBackground(new Color(52, 152, 219)); // Blue
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        titlePanel.add(editButton, BorderLayout.EAST);
        
        // Add title panel to content panel
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Initialize fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);

        // Style all text fields
        styleTextField(nameField);
        styleTextField(emailField);
        styleTextField(phoneField);

        // Create form with styled components
        addStyledFormRow("Full Name:", nameField);
        addStyledFormRow("Email:", emailField);
        addStyledFormRow("Phone:", phoneField);

        // Add some vertical spacing between rows
        addVerticalSpacing(15);

        // Set initial edit state
        setFieldsEditable(false);
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
        contentPanel.add(Box.createVerticalStrut(height));
    }

    @Override
    protected void loadUserData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT u.user_name, u.user_email, u.phone_no, " +
                        "s.sch_full_name " +
                        "FROM user u " +
                        "JOIN scheduler s ON u.user_id = s.sch_user_id " +
                        "WHERE s.sch_user_id = ? AND s.sch_status = 'ACTIVE'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("sch_full_name"));
                emailField.setText(rs.getString("user_email"));
                phoneField.setText(rs.getString("phone_no"));
            } else {
                JOptionPane.showMessageDialog(this, "Scheduler not found or inactive", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading scheduler data: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, ps, rs);
        }
    }

    @Override
    protected void handleEdit() {
        if (!isEditing) {
            setFieldsEditable(true);
            editButton.setText("Save Changes");
            editButton.setBackground(new Color(46, 204, 113)); // Green
        } else {
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
    }

    private void saveChanges() {
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psScheduler = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Update user table
            String userSql = "UPDATE user SET user_name=?, user_email=?, phone_no=? WHERE user_id=?";
            psUser = conn.prepareStatement(userSql);
            psUser.setString(1, nameField.getText().trim());
            psUser.setString(2, emailField.getText().trim());
            psUser.setString(3, phoneField.getText().trim());
            psUser.setInt(4, userId);

            // Update scheduler table
            String schedulerSql = "UPDATE scheduler SET sch_full_name=?, sch_email=? " +
                                "WHERE sch_user_id=? AND sch_status='ACTIVE'";
            psScheduler = conn.prepareStatement(schedulerSql);
            psScheduler.setString(1, nameField.getText().trim());
            psScheduler.setString(2, emailField.getText().trim());
            psScheduler.setInt(3, userId);

            int userRowsAffected = psUser.executeUpdate();
            int schedulerRowsAffected = psScheduler.executeUpdate();

            if (userRowsAffected > 0 && schedulerRowsAffected > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "No changes were made. Scheduler not found or inactive.", 
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
            closeResources(conn, psUser, null);
            if (psScheduler != null) {
                try {
                    psScheduler.close();
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

        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", 
                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

//        String phone = phoneField.getText().trim();
//        if (!phone.matches("\\d{10,15}")) {
//            JOptionPane.showMessageDialog(this, "Please enter a valid phone number (10-15 digits)", 
//                                        "Validation Error", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }

        return true;
    }

   
}

