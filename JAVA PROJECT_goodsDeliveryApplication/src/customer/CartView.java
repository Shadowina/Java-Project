package customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import components.FooterPanel;
import components.HeaderPanel;
import dbmanagement.DBManagementGDA;

public class CartView extends JFrame {
    private final int customerId;
    private final HeaderPanel headerPanel;
    private final FooterPanel footerPanel;
    private final JPanel mainPanel;
    private final JTable cartTable;
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel;
    private double totalAmount = 0.0;
    private boolean isLoading = false;
    private final Vector<Integer> cartIds;
    private Vector<Integer> productIds;

    // SQL Queries
    private static final String SELECT_CART_ITEMS = """
            SELECT c.cart_id, c.quantity, p.product_id, p.product_name,
                   p.price_per_kg, (p.price_per_kg * c.quantity) as total_price
            FROM cart c
            JOIN product p ON c.cart_prd_id = p.product_id
            WHERE c.cart_cst_id = ?
            """;

    private static final String UPDATE_QUANTITY = """
            UPDATE cart
            SET quantity = ?
            WHERE cart_id = ? AND cart_cst_id = ?
            """;

    private static final String DELETE_CART_ITEM = """
            DELETE FROM cart 
            WHERE cart_id = ? AND cart_cst_id = ?
            """;

    private static final String INSERT_ORDER = """
            INSERT INTO `order` (order_cst_id, delivery_address, delivery_date, order_status)
            VALUES (?, ?, ?, 'Pending')
            """;

    private static final String INSERT_ORDER_ITEM = """
            INSERT INTO order_item (ordIt_ord_id, ordIt_prd_id, ordIt_prd_name,
                                  quantity_kg, total_price)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String CLEAR_CART = """
            DELETE FROM cart 
            WHERE cart_cst_id = ?
            """;

    public CartView(int customerId) {
        System.out.println("Initializing CartView with customer ID: " + customerId);
        this.customerId = customerId;
        this.headerPanel = new HeaderPanel();
        this.footerPanel = new FooterPanel();
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
        this.cartIds = new Vector<>();
        this.productIds = new Vector<>();

        // Initialize table
        String[] columnNames = {"Product", "Price/kg", "Quantity", "Total", "Actions"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity is editable
            }
        };
        this.cartTable = new JTable(tableModel);
        this.totalLabel = new JLabel("Total: $0.00");

        setupUI();
        loadCartItems();
        configureFrame();
        setVisible(true);  // Make sure the frame is visible
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Style the main panel
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Configure table
        setupTable();

        // Create cart panel
        JPanel cartPanel = createCartPanel();

        // Create summary panel
        JPanel summaryPanel = createSummaryPanel();

        // Add panels to main panel
        mainPanel.add(cartPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.EAST);

        // Assemble the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupTable() {
        cartTable.setRowHeight(40);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set up the quantity column editor
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.1));
        DefaultCellEditor quantityEditor = new DefaultCellEditor(new JTextField()) {
            private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.1));

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                spinner.setValue(Double.parseDouble(value.toString()));
                return spinner;
            }

            @Override
            public Object getCellEditorValue() {
                return spinner.getValue();
            }
        };
        cartTable.getColumnModel().getColumn(2).setCellEditor(quantityEditor);

        // Set up the remove button column
        cartTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), cartTable, this));

        // Add quantity change listener
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) {  // Quantity column
                int row = e.getFirstRow();
                Object value = cartTable.getModel().getValueAt(row, 2);
                if (value != null) {
                    double newQuantity = Double.parseDouble(value.toString());
                    updateQuantity(row, newQuantity);
                }
            }
        });
        
        //cartTable.getColumnModel().getColumn(4

        // Set column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);  // Product name
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Price
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Quantity
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Total
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Remove button
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Add title
        JLabel titleLabel = new JLabel("Shopping Cart");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Add table in scroll pane
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 0, 0),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        panel.setPreferredSize(new Dimension(300, 0));

        // Total amount
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Delivery Address
        JLabel addressLabel = new JLabel("Delivery Address:");
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressScroll.setMaximumSize(new Dimension(250, 80));

        // Date Selection
        JLabel dateLabel = new JLabel("Delivery Date:");
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create combo boxes for day, month, and year
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datePanel.setBackground(Color.WHITE);
        
        // Days 1-31
        JComboBox<Integer> dayBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayBox.addItem(i);
        }
        
        // Months 1-12
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        JComboBox<String> monthBox = new JComboBox<>(months);
        
        // Years (current year + next year)
        JComboBox<Integer> yearBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        yearBox.addItem(currentYear);
        yearBox.addItem(currentYear + 1);

        // Add listeners to update valid days when month/year changes
        monthBox.addActionListener(e -> updateDays(dayBox, monthBox, yearBox));
        yearBox.addActionListener(e -> updateDays(dayBox, monthBox, yearBox));

        datePanel.add(dayBox);
        datePanel.add(monthBox);
        datePanel.add(yearBox);
        datePanel.setMaximumSize(new Dimension(250, 30));
        datePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Checkout button
        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkoutButton.addActionListener(e -> 
            proceedToCheckout(addressArea.getText(), dayBox, monthBox, yearBox));

        // Continue shopping button
        JButton continueButton = new JButton("Continue Shopping");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.addActionListener(e -> continueShopping());

        // Add components
        panel.add(Box.createVerticalStrut(20));
        panel.add(totalLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(addressLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(addressScroll);
        panel.add(Box.createVerticalStrut(20));
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(checkoutButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(continueButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private void loadCartItems() {
        if (isLoading) return;
        isLoading = true;
        totalAmount = 0.0;
        cartIds.clear();
        productIds.clear();

        System.out.println("Loading cart items for customer ID: " + customerId);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws SQLException {
                try (Connection conn = DBManagementGDA.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(SELECT_CART_ITEMS)) {

                    pstmt.setInt(1, customerId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        tableModel.setRowCount(0);
                        System.out.println("Executing cart items query for customer: " + customerId);

                        while (rs.next()) {
                            Vector<Object> row = new Vector<>();
                            row.add(rs.getString("product_name"));
                            row.add(String.format("$%.2f", rs.getDouble("price_per_kg")));
                            row.add(rs.getDouble("quantity"));
                            double total = rs.getDouble("total_price");
                            row.add(String.format("$%.2f", total));
                            row.add("Remove");

                            cartIds.add(rs.getInt("cart_id"));
                            productIds.add(rs.getInt("product_id"));
                            totalAmount += total;
                            
                            System.out.println("Adding row: " + row);
                            tableModel.addRow(row);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                updateTotalLabel();
                isLoading = false;
                System.out.println("Finished loading cart items. Total rows: " + tableModel.getRowCount());
            }
        };
        worker.execute();
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Total: $%.2f", totalAmount));
    }

    private void updateQuantity(int row, double newQuantity) {
        if (newQuantity <= 0) {
            removeItem(row);
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DBManagementGDA.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(UPDATE_QUANTITY)) {

                    pstmt.setDouble(1, newQuantity);
                    pstmt.setInt(2, getCartIdForRow(row));
                    pstmt.setInt(3, customerId);
                    pstmt.executeUpdate();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadCartItems(); // Refresh the cart
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CartView.this,
                        "Error updating quantity: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void removeItem(int row) {
        if (row < 0 || row >= cartIds.size()) {
            System.out.println("Invalid row index: " + row);
            return;
        }

        int cartId = cartIds.get(row);
        System.out.println("Attempting to remove cart item with ID: " + cartId);
        
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_CART_ITEM)) {

            pstmt.setInt(1, cartId);
            pstmt.setInt(2, customerId);
            
            System.out.println("Executing delete query for cartId: " + cartId + ", customerId: " + customerId);
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                System.out.println("Successfully removed item from cart");
                loadCartItems(); // Refresh the cart
            } else {
                System.out.println("No rows were deleted");
                JOptionPane.showMessageDialog(this,
                    "Failed to remove item from cart",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            System.err.println("Error removing item: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error removing item: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCartIdForRow(int row) {
        if (row >= 0 && row < cartIds.size()) {
            return cartIds.get(row);
        }
        return 0;
    }

    private int getProductIdForRow(int row) {
        if (row >= 0 && row < productIds.size()) {
            return productIds.get(row);
        }
        return 0;
    }

    private void proceedToCheckout(String address, JComboBox<Integer> dayBox,
                                 JComboBox<String> monthBox, JComboBox<Integer> yearBox) {
        if (address == null || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a delivery address",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int day = (Integer) dayBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;
        int year = (Integer) yearBox.getSelectedItem();
        LocalDate selectedDate = LocalDate.of(year, month, day);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (Connection conn = DBManagementGDA.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        System.out.println("Starting checkout process...");
                        
                        // Insert order
                        try (PreparedStatement orderStmt = conn.prepareStatement(
                                INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                            orderStmt.setInt(1, customerId);
                            orderStmt.setString(2, address);
                            orderStmt.setDate(3, java.sql.Date.valueOf(selectedDate));
                            int orderResult = orderStmt.executeUpdate();
                            System.out.println("Order insert result: " + orderResult);

                            // Get the generated order ID
                            try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                                if (rs.next()) {
                                    int orderId = rs.getInt(1);
                                    System.out.println("Generated order ID: " + orderId);
                                    
                                    // First, insert order items
                                    insertOrderItems(conn, orderId);
                                    System.out.println("Order items inserted successfully");
                                    
                                    // Then, clear the cart
                                    try (PreparedStatement clearStmt = conn.prepareStatement(CLEAR_CART)) {
                                        clearStmt.setInt(1, customerId);
                                        int clearResult = clearStmt.executeUpdate();
                                        System.out.println("Cart clear result: " + clearResult);
                                    }
                                }
                            }
                        }
                        conn.commit();
                        System.out.println("Transaction committed successfully");
                        return true;
                    } catch (SQLException e) {
                        System.err.println("Error during checkout: " + e.getMessage());
                        e.printStackTrace(); // Add this for more detailed error info
                        conn.rollback();
                        throw e;
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        System.out.println("Checkout completed successfully");
                        JOptionPane.showMessageDialog(CartView.this,
                            "Order placed successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new CustomerDashboard(customerId).setVisible(true);
                    }
                } catch (Exception e) {
                    System.err.println("Error in done(): " + e.getMessage());
                    e.printStackTrace(); // Add this for more detailed error info
                    JOptionPane.showMessageDialog(CartView.this,
                        "Error placing order: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void insertOrderItems(Connection conn, int orderId) throws SQLException {
        try (PreparedStatement itemStmt = conn.prepareStatement(INSERT_ORDER_ITEM)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int productId = getProductIdForRow(i);
                if (productId == 0) {
                    throw new SQLException("Invalid product ID for row " + i);
                }
                
                itemStmt.setInt(1, orderId);                    // ordIt_ord_id
                itemStmt.setInt(2, productId);                  // ordIt_prd_id
                itemStmt.setString(3, tableModel.getValueAt(i, 0).toString());  // ordIt_prd_name
                itemStmt.setInt(4, (int)Double.parseDouble(tableModel.getValueAt(i, 2).toString())); // quantity_kg
                itemStmt.setFloat(5, (float)parsePrice(tableModel.getValueAt(i, 3).toString())); // total_price
                
                System.out.println("Inserting order item: orderId=" + orderId + 
                                 ", productId=" + productId + 
                                 ", name=" + tableModel.getValueAt(i, 0).toString() +
                                 ", quantity=" + tableModel.getValueAt(i, 2).toString() +
                                 ", price=" + tableModel.getValueAt(i, 3).toString());
                
                itemStmt.executeUpdate();
            }
        }
    }

    private double parsePrice(String price) {
        return Double.parseDouble(price.replace("$", "").trim());
    }

    private void continueShopping() {
        dispose();
        new ProductView(customerId).setVisible(true);
    }

    private void configureFrame() {
        setTitle("Shopping Cart");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void updateDays(JComboBox<Integer> dayBox, JComboBox<String> monthBox, 
                           JComboBox<Integer> yearBox) {
        int year = (Integer) yearBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;
        int selectedDay = (Integer) dayBox.getSelectedItem();
        
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        
        dayBox.removeAllItems();
        for (int i = 1; i <= daysInMonth; i++) {
            dayBox.addItem(i);
        }
        
        if (selectedDay <= daysInMonth) {
            dayBox.setSelectedItem(selectedDay);
        }
    }

    private boolean isValidDeliveryDate(LocalDate selectedDate) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate maxDate = LocalDate.now().plusDays(30);
        return !selectedDate.isBefore(tomorrow) && !selectedDate.isAfter(maxDate);
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setBackground(new Color(220, 53, 69));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "");
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private boolean isPushed;
    private final JTable table;
    private final CartView cartView;
    private int currentRow;  // Add this to track the current row

    public ButtonEditor(JCheckBox checkBox, JTable table, CartView cartView) {
        super(checkBox);
        this.table = table;
        this.cartView = cartView;
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        // Modify the action listener
        button.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                cartView,
                "Are you sure you want to remove this item?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                cartView.removeItem(currentRow);
            }
            fireEditingStopped();
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentRow = row;  // Store the current row
        label = value.toString();
        button.setText(label);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }
}