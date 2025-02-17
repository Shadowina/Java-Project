package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
import javax.swing.table.TableRowSorter;

import dbmanagement.DBManagementGDA;
import scheduler.components.DeliveryStaffView;


public class SchedulerView extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private DeliveryStaffView deliveryStaffView;
    private JButton manageOrdersButton;
    private JButton viewAssignedOrdersButton;
    private int userId;

    public SchedulerView(int userId) {
        this.userId = userId;
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Scheduler Dashboard");
        setLayout(new BorderLayout());

        // Initialize DeliveryStaffView with reference to this
        deliveryStaffView = new DeliveryStaffView(userId, this);

        // Create main container
        JPanel container = new JPanel(new BorderLayout());

        // Create main content panel with the table
        JPanel mainPanel = createMainPanel();

        // Add components to container
        container.add(deliveryStaffView.getMenuPanel(), BorderLayout.WEST);
        container.add(mainPanel, BorderLayout.CENTER);

        add(container);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        createOrderTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        viewAssignedOrdersButton = createButton("View Assigned Orders", e -> openAssignedOrders());
        manageOrdersButton = createButton("Manage Selected Orders", e -> openManageOrdersView());
        //JButton generateDocButton = createButton("Generate Document", e -> generateDocument());

        buttonPanel.add(viewAssignedOrdersButton);
        buttonPanel.add(manageOrdersButton);
        //buttonPanel.add(generateDocButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(listener);
        return button;
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
            "Total Amount"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Boolean.class;  // Checkbox column
                    case 1: return Integer.class;  // Order ID column
                    default: return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox column is editable
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(new Color(240, 240, 240));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setShowGrid(true);
        orderTable.setGridColor(new Color(230, 230, 230));

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        orderTable.setRowSorter(sorter);

        // Custom comparator for date column
        sorter.setComparator(1, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.parse(date1).compareTo(sdf.parse(date2));
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        // Center align all columns except checkbox
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < orderTable.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Checkbox column
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setPreferredWidth(70);  // Order ID
        columnModel.getColumn(2).setPreferredWidth(100); // Date
        columnModel.getColumn(3).setPreferredWidth(170); // Customer Name
        columnModel.getColumn(4).setPreferredWidth(230); // Address
        columnModel.getColumn(5).setPreferredWidth(80); // Status
        columnModel.getColumn(6).setPreferredWidth(80); // Quantity
        columnModel.getColumn(7).setPreferredWidth(100); // Total Amount

        loadOrderData();
    }

    private void loadOrderData() {
        try {
            Connection conn = DriverManager.getConnection(DBManagementGDA.URL, DBManagementGDA.USER, DBManagementGDA.PASSWORD);

            String sql = "SELECT o.order_id, o.delivery_date, o.delivery_address, o.order_status, " +
                        "c.cst_full_name, " +
                        "SUM(oi.quantity_kg) as total_quantity, " +
                        "SUM(oi.quantity_kg * p.price_per_kg) as total_amount " +
                        "FROM `order` o " +
                        "JOIN customer c ON o.order_cst_id = c.customer_id " +
                        "JOIN order_item oi ON o.order_id = oi.ordIt_ord_id " +
                        "JOIN product p ON oi.ordIt_prd_id = p.product_id " +
                        "WHERE o.order_id NOT IN (SELECT mord_order_id FROM mission_order) " +
                        "GROUP BY o.order_id, o.delivery_date, o.delivery_address, o.order_status, c.cst_full_name " +
                        "ORDER BY o.delivery_date DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");  // Get as int
                Object[] row = {
                    false,  // Checkbox
                    orderId,  // Store as Integer, not String
                    formatDate(rs.getDate("delivery_date")),
                    rs.getString("cst_full_name"),
                    rs.getString("delivery_address"),
                    rs.getString("order_status"),
                    String.format("%.1f kg", rs.getDouble("total_quantity")),
                    String.format("$ %.2f", rs.getDouble("total_amount"))
                };
                tableModel.addRow(row);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading order data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // Add a method to refresh the table data
    public void refreshTable() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Reload data
        loadOrderData();
    }

    private void openManageOrdersView() {
        List<Integer> selectedOrders = getSelectedOrderIds();
        
        if (selectedOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one order to manage.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hide this view before showing the new one
        this.setVisible(false);
        
        // Create and show ManageOrdersView
        ManageOrdersView manageOrdersView = new ManageOrdersView(this, userId, selectedOrders);
        manageOrdersView.setVisible(true);
    }

    private List<Integer> getSelectedOrderIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < orderTable.getRowCount(); i++) {
            Boolean isSelected = (Boolean) orderTable.getValueAt(i, 0);
            if (Boolean.TRUE.equals(isSelected)) {  // Safer null check
                Object value = orderTable.getValueAt(i, 1);
                if (value instanceof Integer) {
                    selectedIds.add((Integer) value);
                } else {
                    System.err.println("Warning: Order ID at row " + i + " is not an Integer: " + value);
                    // If needed, try to convert string to integer
                    try {
                        selectedIds.add(Integer.parseInt(value.toString()));
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse order ID: " + value);
                    }
                }
            }
        }
        
        System.out.println("Selected order IDs: " + selectedIds);
        return selectedIds;
    }

    private void openAssignedOrders() {
        AssignedOrdersView assignedOrdersView = new AssignedOrdersView(this, userId);
        assignedOrdersView.setVisible(true);
        this.setVisible(false);
    }

    // Add getter for scheduler ID
    public int getSchedulerId() {
        return userId;
    }

//    private void generateDocument() {
//        List<Integer> selectedOrderIds = new ArrayList<>();
//        for (int i = 0; i < tableModel.getRowCount(); i++) {
//            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
//            if (isSelected != null && isSelected) {
//                Integer orderId = (Integer) tableModel.getValueAt(i, 2);
//                selectedOrderIds.add(orderId);
//            }
//        }
//
//        if (selectedOrderIds.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                "Please select at least one order to generate document.",
//                "No Selection",
//                JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        GenerateMissionDocumentDialog dialog = new GenerateMissionDocumentDialog(this);
//        dialog.setVisible(true);
//    }
    
    
    //Add method to handle report view
    
    public void showReportView() {
    	dispose();
        new ReportView(this, userId).setVisible(true);
    }
    

    // Add method to handle profile view
    public void showProfileView() {
        this.setVisible(false);
        
        JFrame profileFrame = new JFrame("Scheduler Profile");
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setSize(600, 500);
        profileFrame.setLocationRelativeTo(null);
        
        SchedulerProfileView profileView = new SchedulerProfileView(userId);
        profileFrame.add(profileView);
        
        profileFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SchedulerView.this.setVisible(true);
            }
        });
        
        profileFrame.setVisible(true);
    }

}
