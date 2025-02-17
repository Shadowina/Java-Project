package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import dbmanagement.DBManagementGDA;

public class ManageOrdersView extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private List<Integer> selectedOrderIds;
    private SchedulerView parentView;
    private int userId;
    private static final int buttonColumnIndex = 8;
    private List<Integer> cartIds;
    private JLabel totalWeightLabel;  // Add class field

    public ManageOrdersView(SchedulerView parentView, int userId, List<Integer> selectedOrderIds) {
        this.parentView = parentView;
        this.userId = userId;
        this.selectedOrderIds = selectedOrderIds;
        this.cartIds = new ArrayList<>();

        setTitle("Manage Selected Orders");
        setLayout(new BorderLayout());

        // Create main panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        // Set frame properties
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Add window listener to handle closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleBackButton();
            }
        });
    }

    private void handleBackButton() {
        // Refresh and show the parent SchedulerView
        if (parentView != null) {
            parentView.refreshTable();
            parentView.setVisible(true);
        }
        // Close this view
        this.dispose();
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
        backButton.addActionListener(e -> handleBackButton());
        titlePanel.add(backButton, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel("Manage Selected Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Total Weight Label
        totalWeightLabel = new JLabel("Total Weight: 0.0 kg");
        totalWeightLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalWeightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        titlePanel.add(totalWeightLabel, BorderLayout.EAST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Create table
        createOrderTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton assignDriverButton = new JButton("Assign Driver");
        assignDriverButton.setFont(new Font("Arial", Font.PLAIN, 14));
        assignDriverButton.addActionListener(e -> assignToDriver());

        JButton removeButton = new JButton("Remove Orders");
        removeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        removeButton.addActionListener(e -> removeSelectedOrders());

        buttonPanel.add(assignDriverButton);
        buttonPanel.add(removeButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void createOrderTable() {
        String[] columns = {
            "Select",  // Checkbox column
            "Order ID",
            "Delivery Date",
            "Customer Name",
            "Delivery Address",
            "Status",
            "Quantity",
            "Total Amount",
            "Move"  // Column for up/down buttons
        };

        // Make the table editable for checkbox and move columns
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == buttonColumnIndex;  // Make checkbox and button columns editable
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;  // This makes the checkbox appear
                }
                return super.getColumnClass(column);
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.setRowHeight(35);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set up column widths
        TableColumnModel columnModel = orderTable.getColumnModel();
        
        // Set checkbox column width
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(0).setMaxWidth(50);
        
        // Set up the move column
        columnModel.getColumn(buttonColumnIndex).setCellRenderer(new ButtonRenderer());
        columnModel.getColumn(buttonColumnIndex).setCellEditor(new ButtonEditor(new JCheckBox(), orderTable, this));
        columnModel.getColumn(buttonColumnIndex).setPreferredWidth(80);
        columnModel.getColumn(buttonColumnIndex).setMinWidth(80);
        columnModel.getColumn(buttonColumnIndex).setMaxWidth(80);

        loadSelectedOrders();
    }

    private void loadSelectedOrders() {
        if (selectedOrderIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No orders were selected to manage.",
                "No Orders",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Build the IN clause for the SQL query
        StringBuilder orderIdList = new StringBuilder();
        for (int i = 0; i < selectedOrderIds.size(); i++) {
            orderIdList.append("?");
            if (i < selectedOrderIds.size() - 1) {
                orderIdList.append(",");
            }
        }

        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT o.order_id, o.delivery_date, o.delivery_address, o.order_status, " +
                 "c.cst_full_name, " +
                 "SUM(oi.quantity_kg) as total_quantity, " +
                 "SUM(oi.quantity_kg * p.price_per_kg) as total_amount " +
                 "FROM `order` o " +
                 "JOIN customer c ON o.order_cst_id = c.customer_id " +
                 "JOIN order_item oi ON o.order_id = oi.ordIt_ord_id " +
                 "JOIN product p ON oi.ordIt_prd_id = p.product_id " +
                 "WHERE o.order_id IN (" + orderIdList.toString() + ") " +
                 "GROUP BY o.order_id, o.delivery_date, o.delivery_address, o.order_status, c.cst_full_name " +
                 "ORDER BY o.delivery_date ASC")) {
             
            // Set the order IDs in the prepared statement
            for (int i = 0; i < selectedOrderIds.size(); i++) {
                pstmt.setInt(i + 1, selectedOrderIds.get(i));
            }
             
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Object[] row = {
                    Boolean.FALSE,  // Initialize checkbox as unchecked
                    orderId,
                    formatDate(rs.getDate("delivery_date")),
                    rs.getString("cst_full_name"),
                    rs.getString("delivery_address"),
                    rs.getString("order_status"),
                    String.format("%.1f kg", rs.getDouble("total_quantity")), // Keep the "kg"
                    String.format("$ %.2f", rs.getDouble("total_amount")),
                    "Move"  // Move buttons
                };
                tableModel.addRow(row);
                cartIds.add(orderId);  // Add to our tracking list
            }
            
            // Update total weight after loading orders
            updateTotalWeight();
            
            System.out.println("Loaded " + tableModel.getRowCount() + " orders");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading order data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getOrderDeliveryDate(int orderId) {
        String query = "SELECT delivery_date FROM `order` WHERE order_id = ?";
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, orderId); // Set the order_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Date deliveryDate = rs.getDate("delivery_date");
                    return deliveryDate != null ? deliveryDate.toString() : null; // Return the delivery date as a string
                } else {
                    JOptionPane.showMessageDialog(null, "Order not found for the given ID.", "No Order Found", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching delivery date: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return null; // Return null if the date couldn't be retrieved
    }

    private void assignToDriver() {
        List<Integer> selectedIds = getSelectedOrderIds();
        System.out.println("Selected order IDs: " + selectedIds);
        
        if (!selectedIds.isEmpty()) {
            int firstId = selectedIds.get(0);
            dispose();
            new AssignDriverView(selectedIds, this, userId, getOrderDeliveryDate(firstId)).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select at least one order to assign.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshAfterAssignment() {
        // Clear the current table
        tableModel.setRowCount(0);
        cartIds.clear();

        try (Connection conn = DBManagementGDA.getConnection()) {
            // Only get orders that are still unassigned
            StringBuilder orderIdList = new StringBuilder();
            for (int i = 0; i < selectedOrderIds.size(); i++) {
                orderIdList.append("?");
                if (i < selectedOrderIds.size() - 1) {
                    orderIdList.append(",");
                }
            }

            String sql = "SELECT o.order_id, o.delivery_date, o.delivery_address, o.order_status, " +
                        "c.cst_full_name, " +
                        "SUM(oi.quantity_kg) as total_quantity, " +
                        "SUM(oi.quantity_kg * p.price_per_kg) as total_amount " +
                        "FROM `order` o " +
                        "JOIN customer c ON o.order_cst_id = c.customer_id " +
                        "JOIN order_item oi ON o.order_id = oi.ordIt_ord_id " +
                        "JOIN product p ON oi.ordIt_prd_id = p.product_id " +
                        "WHERE o.order_id IN (" + orderIdList.toString() + ") " +
                        "AND o.order_id NOT IN (SELECT mord_order_id FROM mission_order) " +  // Only get unassigned orders
                        "GROUP BY o.order_id, o.delivery_date, o.delivery_address, o.order_status, c.cst_full_name";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // Set the order IDs in the prepared statement
            for (int i = 0; i < selectedOrderIds.size(); i++) {
                pstmt.setInt(i + 1, selectedOrderIds.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Object[] row = {
                    Boolean.FALSE,
                    orderId,
                    formatDate(rs.getDate("delivery_date")),
                    rs.getString("cst_full_name"),
                    rs.getString("delivery_address"),
                    rs.getString("order_status"),
                    String.format("%.1f kg", rs.getDouble("total_quantity")), // Keep the "kg"
                    String.format("$ %.2f", rs.getDouble("total_amount")),
                    "Move"
                };
                tableModel.addRow(row);
                cartIds.add(orderId);
            }

            // Update total weight after refresh
            updateTotalWeight();

            // If no orders remain, close this view and return to scheduler
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "All selected orders have been assigned.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                parentView.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error refreshing order data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private void removeSelectedOrders() {
        List<Integer> ordersToRemove = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
            if (isSelected) {
                Integer orderId = (Integer) tableModel.getValueAt(i, 1);
                ordersToRemove.add(orderId);
            }
        }

        if (ordersToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one order to remove.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(DBManagementGDA.URL,
                                                       DBManagementGDA.USER,
                                                       DBManagementGDA.PASSWORD);
            conn.setAutoCommit(false);

            try {
                // Remove from mission_order
                StringBuilder orderIds = new StringBuilder();
                for (int i = 0; i < ordersToRemove.size(); i++) {
                    orderIds.append("?");
                    if (i < ordersToRemove.size() - 1) {
                        orderIds.append(",");
                    }
                }

                String deleteMissionOrderSql = "DELETE FROM mission_order WHERE mord_order_id IN (" + orderIds.toString() + ")";
                PreparedStatement deleteMissionOrderStmt = conn.prepareStatement(deleteMissionOrderSql);
                for (int i = 0; i < ordersToRemove.size(); i++) {
                    deleteMissionOrderStmt.setInt(i + 1, ordersToRemove.get(i));
                }
                deleteMissionOrderStmt.executeUpdate();

                // Update order status back to unassigned
                String updateOrderSql = "UPDATE `order` SET order_status = 'Unassigned' WHERE order_id = ?";
                PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderSql);

                for (Integer orderId : ordersToRemove) {
                    updateOrderStmt.setInt(1, orderId);
                    updateOrderStmt.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this,
                    "Orders successfully removed from assignment",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Refresh views
                parentView.refreshTable();
                this.dispose();
                parentView.setVisible(true);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error removing orders: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    // Custom button renderer for up/down arrows
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton upButton;
        private final JButton downButton;

        public ButtonRenderer() {
            setLayout(new GridLayout(1, 2, 2, 0));
            upButton = new JButton("↑");
            downButton = new JButton("↓");
            
            styleButton(upButton);
            styleButton(downButton);
            
            add(upButton);
            add(downButton);
            setBackground(Color.WHITE);
        }

        private void styleButton(JButton button) {
            button.setForeground(Color.BLACK);
            button.setBackground(new Color(240, 240, 240));
            button.setBorder(BorderFactory.createRaisedBevelBorder());
            button.setFocusPainted(false);
            button.setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    // Custom button editor for up/down arrows
    class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton upButton;
        private final JButton downButton;
        private int currentRow;
        private final JTable table;

        public ButtonEditor(JCheckBox checkBox, JTable table, ManageOrdersView parent) {
            super(checkBox);
            this.table = table;
            
            panel = new JPanel(new GridLayout(1, 2, 2, 0));
            upButton = new JButton("↑");
            downButton = new JButton("↓");
            
            styleButton(upButton);
            styleButton(downButton);
            
            upButton.addActionListener(e -> {
                System.out.println("Up button clicked for row: " + currentRow);
                moveRow(-1);
                fireEditingStopped();
            });
            
            downButton.addActionListener(e -> {
                System.out.println("Down button clicked for row: " + currentRow);
                moveRow(1);
                fireEditingStopped();
            });
            
            panel.add(upButton);
            panel.add(downButton);
            panel.setBackground(Color.WHITE);
        }

        private void styleButton(JButton button) {
            button.setForeground(Color.BLACK);
            button.setBackground(new Color(240, 240, 240));
            button.setBorder(BorderFactory.createRaisedBevelBorder());
            button.setFocusPainted(false);
            button.setOpaque(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            System.out.println("Editor activated for row: " + row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Move";
        }
    }

    private void moveRow(int direction) {
        try {
            System.out.println("\nStarting moveRow operation:");
            System.out.println("Direction: " + direction);
            
            int selectedRow = orderTable.getSelectedRow();
            System.out.println("Selected row index: " + selectedRow);
            
            if (selectedRow < 0) {
                System.out.println("No row selected");
                return;
            }

            int targetRow = selectedRow + direction;
            System.out.println("Target row index: " + targetRow);
            
            if (targetRow < 0 || targetRow >= tableModel.getRowCount()) {
                System.out.println("Target row out of bounds");
                return;
            }

            // Simply swap rows in the table model
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Object temp = tableModel.getValueAt(selectedRow, i);
                tableModel.setValueAt(tableModel.getValueAt(targetRow, i), selectedRow, i);
                tableModel.setValueAt(temp, targetRow, i);
            }

            // Also swap the order IDs in our tracking list
            if (!selectedOrderIds.isEmpty()) {
                int tempId = selectedOrderIds.get(selectedRow);
                selectedOrderIds.set(selectedRow, selectedOrderIds.get(targetRow));
                selectedOrderIds.set(targetRow, tempId);
            }

            // Update selection to follow the moved row
            orderTable.setRowSelectionInterval(targetRow, targetRow);
            
            System.out.println("Successfully swapped rows " + selectedRow + " and " + targetRow);
        } catch (Exception e) {
            System.err.println("Error swapping rows: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error swapping rows: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    
    // Add getter for parent view
    public SchedulerView getParentView() {
        return parentView;
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        loadSelectedOrders();
    }

    // Add getter for selected order IDs
    public List<Integer> getSelectedOrderIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
            if (isSelected) {
                Integer orderId = (Integer) tableModel.getValueAt(i, 1);
                selectedIds.add(orderId);
            }
        }
        System.out.println("Getting selected order IDs: " + selectedIds);
        return selectedIds;
    }

    private void updateTotalWeight() {
        double totalWeight = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String quantityStr = (String) tableModel.getValueAt(i, 6); // Quantity column
            if (quantityStr != null) {
                // Extract number from "X.X kg" format
                try {
                    String[] parts = quantityStr.split(" ");
                    if (parts.length > 0) {
                        totalWeight += Double.parseDouble(parts[0]);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing quantity: " + quantityStr);
                }
            }
        }
        totalWeightLabel.setText(String.format("Total Weight: %.1f kg", totalWeight));
    }
}
