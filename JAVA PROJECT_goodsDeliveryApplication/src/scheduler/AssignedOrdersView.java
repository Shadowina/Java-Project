package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class AssignedOrdersView extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private SchedulerView parentView;
    private int schedulerId;

    public AssignedOrdersView(SchedulerView parentView, int schedulerId) {
        System.out.println("AssignedOrdersView constructor called with schedulerId: " + schedulerId);
        this.schedulerId = schedulerId;
        this.parentView = parentView;

        setTitle("Assigned Orders");
        setLayout(new BorderLayout());

        createMainPanel();

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);

        System.out.println("AssignedOrdersView initialization completed");
    }

    private void loadAssignedOrders() {
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT o.order_id, o.delivery_date, o.delivery_address, o.order_status, " +
                 "c.cst_full_name, d.dr_full_name, m.mission_id, " +
                 "SUM(oi.quantity_kg) as total_quantity, " +
                 "SUM(oi.quantity_kg * p.price_per_kg) as total_amount " +
                 "FROM `order` o " +
                 "JOIN customer c ON o.order_cst_id = c.customer_id " +
                 "JOIN order_item oi ON o.order_id = oi.ordIt_ord_id " +
                 "JOIN product p ON oi.ordIt_prd_id = p.product_id " +
                 "JOIN mission_order mo ON o.order_id = mo.mord_order_id " +
                 "JOIN mission m ON mo.mord_mission_id = m.mission_id " +
                 "JOIN driver d ON m.msn_dr_id = d.driver_id " +
                 "WHERE o.order_status = 'Assigned' " +  // Only show assigned orders
                 "GROUP BY o.order_id, o.delivery_date, o.delivery_address, o.order_status, " +
                 "c.cst_full_name, d.dr_full_name, m.mission_id " +
                 "ORDER BY o.delivery_date DESC")) {
             
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    formatDate(rs.getDate("delivery_date")),
                    rs.getString("cst_full_name"),
                    rs.getString("delivery_address"),
                    rs.getString("dr_full_name"),
                    rs.getInt("mission_id"),
                    String.format("%.1f kg", rs.getDouble("total_quantity")),
                    String.format("$ %.2f", rs.getDouble("total_amount"))
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading assigned orders: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createMainPanel() {
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
        JLabel titleLabel = new JLabel("Assigned Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Create and add table
        createOrderTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadAssignedOrders();
    }

    private void createOrderTable() {
        String[] columns = {
            "Order ID",
            "Delivery Date",
            "Customer Name",
            "Delivery Address",
            "Driver Name",
            "Mission ID",
            "Quantity (kg)",
            "Total Amount"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make all cells non-editable
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: // Order ID
                    case 5: // Mission ID
                        return Integer.class;
                    case 1: // Delivery Date
                        return java.sql.Timestamp.class;
                    case 2: // Customer Name
                    case 3: // Delivery Address
                    case 4: // Driver Name
                        return String.class;
                    case 6: // Quantity
                    case 7: // Total Amount
                        return Double.class;
                    default:
                        return String.class;
                }
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

        // Set column widths
        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Order ID
        columnModel.getColumn(1).setPreferredWidth(150); // Delivery Date
        columnModel.getColumn(2).setPreferredWidth(150); // Customer Name
        columnModel.getColumn(3).setPreferredWidth(200); // Delivery Address
        columnModel.getColumn(4).setPreferredWidth(150); // Driver Name
        columnModel.getColumn(5).setPreferredWidth(80);  // Mission ID
        columnModel.getColumn(6).setPreferredWidth(100); // Quantity
        columnModel.getColumn(7).setPreferredWidth(100); // Total Amount

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}