package components;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import dbmanagement.DBManagementGDA;

public abstract class BaseProfileView extends JPanel {
    protected JPanel contentPanel;
    protected JLabel titleLabel;
    protected JButton editButton;
    protected int userId;
    
    // Database connection parameters
    protected Connection getConnection() throws SQLException {
        return DBManagementGDA.getConnection();
    }

    
    public BaseProfileView(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        initializeComponents();
        loadUserData();
    }
    
    protected void initializeComponents() {
        // Main content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        editButton = new JButton("Modify");
        editButton.addActionListener(e -> handleEdit());
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(editButton, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
//    protected Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//    }
    
    protected void closeResources(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected abstract void loadUserData();
    protected abstract void handleEdit();
}

