package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import dbmanagement.DBManagementGDA;

public class AssignDriverView extends JFrame {
    private JTable driverTable;
    private DefaultTableModel tableModel;
    private List<Integer> selectedOrderIds;
    private ManageOrdersView parentView;
    private int schedulerId;
    private String selectedDate;

    public AssignDriverView(List<Integer> selectedOrderIds, ManageOrdersView parentView, int userId, String selectedDate) {
        this.selectedOrderIds = selectedOrderIds;
        this.parentView = parentView;
        this.selectedDate = selectedDate;
        
        // Get scheduler_id from user_id
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT scheduler_id FROM scheduler WHERE sch_user_id = ?")) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.schedulerId = rs.getInt("scheduler_id");
                    System.out.println("Found scheduler ID: " + this.schedulerId + " for user ID: " + userId);
                } else {
                    throw new SQLException("No scheduler found for user ID: " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error getting scheduler ID: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("Assign to Driver");
        setLayout(new BorderLayout());

        // Create main panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        // Set frame properties
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Add window listener to show parent when this window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                parentView.setVisible(true);
            }
        });
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel with Back Button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        // Back Button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            parentView.setVisible(true);
            this.dispose();
        });
        titlePanel.add(backButton, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel("Select Driver to Assign Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Create table
        createDriverTable();
        JScrollPane scrollPane = new JScrollPane(driverTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton assignButton = new JButton("Assign Orders");
        assignButton.setFont(new Font("Arial", Font.PLAIN, 14));
        assignButton.addActionListener(e -> {
            // Find the selected driver
            int selectedRow = driverTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a driver", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            int driverId = (int) driverTable.getValueAt(selectedRow, 1);
            assignOrdersToDriver(driverId);
        });

        buttonPanel.add(assignButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void createDriverTable() {
        String[] columns = {
            "Select",
            "Driver ID",
            "Driver Name",
            "Truck Registration",
            "Truck Capacity",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        driverTable = new JTable(tableModel);
        driverTable.setFont(new Font("Arial", Font.PLAIN, 12));
        driverTable.setRowHeight(30);
        driverTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        driverTable.getTableHeader().setBackground(new Color(240, 240, 240));
        driverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driverTable.setShowGrid(true);
        driverTable.setGridColor(new Color(230, 230, 230));

        // Center align all columns except checkbox
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < driverTable.getColumnCount(); i++) {
            driverTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        TableColumnModel columnModel = driverTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // Checkbox
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setPreferredWidth(70);   // Driver ID
        columnModel.getColumn(2).setPreferredWidth(150);  // Driver Name
        columnModel.getColumn(3).setPreferredWidth(120);  // Truck Registration
        columnModel.getColumn(4).setPreferredWidth(100);  // Truck Capacity
        columnModel.getColumn(5).setPreferredWidth(100);  // Status

        loadDriverData();
    }

    private void loadDriverData() {
        try {
            Connection conn = DriverManager.getConnection(DBManagementGDA.URL,
                                                       DBManagementGDA.USER,
                                                       DBManagementGDA.PASSWORD);

            String sql = "SELECT driver_id, dr_full_name, truck_registration, truck_capacity, " +
                        "driver_status " +
                        "FROM driver " +
                        "ORDER BY dr_full_name";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    false,  // Checkbox
                    rs.getInt("driver_id"),
                    rs.getString("dr_full_name"),
                    rs.getString("truck_registration"),
                    rs.getDouble("truck_capacity") + " kg",
                    rs.getString("driver_status")
                };
                tableModel.addRow(row);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading driver data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignOrdersToDriver(int driverId) {
        // First check if total weight exceeds truck capacity
        if (!validateTruckCapacity(driverId)) {
            return;
        }

        try (Connection conn = DBManagementGDA.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            
            // Create new mission
            int missionId = createNewMission(conn, driverId);
            if (missionId == -1) {
                conn.rollback();
                return;
            }

            // Assign orders to mission
            assignOrdersToMission(conn, missionId);
            
            conn.commit();
            JOptionPane.showMessageDialog(this,
                "Orders successfully assigned to driver!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Close this view and refresh the ManageOrdersView
            this.dispose();
            if (parentView != null) {
                parentView.refreshAfterAssignment();
                parentView.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error assigning orders: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateTruckCapacity(int driverId) {
        try (Connection conn = DBManagementGDA.getConnection()) {
            // Get truck capacity for the selected driver
            String capacityQuery = "SELECT truck_capacity FROM driver WHERE driver_id = ?";
            double truckCapacity = 0.0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(capacityQuery)) {
                pstmt.setInt(1, driverId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    truckCapacity = rs.getDouble("truck_capacity");
                }
            }

            // Get total weight of selected orders
            StringBuilder orderIdList = new StringBuilder();
            for (int i = 0; i < selectedOrderIds.size(); i++) {
                orderIdList.append("?");
                if (i < selectedOrderIds.size() - 1) {
                    orderIdList.append(",");
                }
            }

            String weightQuery = "SELECT SUM(oi.quantity_kg) as total_weight " +
                               "FROM order_item oi " +
                               "WHERE oi.ordIt_ord_id IN (" + orderIdList.toString() + ")";

            double totalWeight = 0.0;
            try (PreparedStatement pstmt = conn.prepareStatement(weightQuery)) {
                for (int i = 0; i < selectedOrderIds.size(); i++) {
                    pstmt.setInt(i + 1, selectedOrderIds.get(i));
                }
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    totalWeight = rs.getDouble("total_weight");
                }
            }

            // Compare and show message if exceeds capacity
            if (totalWeight > truckCapacity) {
                JOptionPane.showMessageDialog(this,
                    String.format("Total order weight (%.1f kg) exceeds truck capacity (%.1f kg).\n" +
                                "Please select a different driver or reduce the number of orders.",
                                totalWeight, truckCapacity),
                    "Capacity Exceeded",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error validating truck capacity: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void updateOrderStatus(Connection conn) throws SQLException {
        String updateSql = "UPDATE `order` SET order_status = 'Assigned' WHERE order_id IN (" +
                          String.join(",", Collections.nCopies(selectedOrderIds.size(), "?")) + ")";
                          
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            for (int i = 0; i < selectedOrderIds.size(); i++) {
                pstmt.setInt(i + 1, selectedOrderIds.get(i));
            }
            pstmt.executeUpdate();
        }
    }

    private int getCurrentSchedulerId() {
        return this.schedulerId;
    }

    private void assignOrdersToMission(Connection conn, int missionId) throws SQLException {
        String sql = "INSERT INTO mission_order (mord_mission_id, mord_order_id, sequence_number) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < selectedOrderIds.size(); i++) {
                pstmt.setInt(1, missionId);
                pstmt.setInt(2, selectedOrderIds.get(i));
                pstmt.setInt(3, i + 1);  // Sequence number starting from 1
                pstmt.executeUpdate();
            }
        }
    }

    private int createMission(Connection conn, int driverId, String missionDate) throws SQLException {
        String sql = "INSERT INTO mission (msn_dr_id, msn_sch_id, mission_date, mission_status) VALUES (?, ?, ?, 'Pending')";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, driverId);
            pstmt.setInt(2, schedulerId);
            pstmt.setString(3, missionDate);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create mission, no ID obtained.");
        }
    }

    private int createNewMission(Connection conn, int driverId) throws SQLException {
        String sql = "INSERT INTO mission (msn_dr_id, msn_sch_id, mission_date, mission_status) VALUES (?, ?, ?, 'Pending')";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, driverId);
            pstmt.setInt(2, schedulerId);
            pstmt.setString(3, selectedDate);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create mission, no ID obtained.");
        }
    }
}