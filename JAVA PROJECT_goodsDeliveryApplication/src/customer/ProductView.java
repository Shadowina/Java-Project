package customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import components.FooterPanel;
import components.HeaderPanel;
import dbmanagement.DBManagementGDA;
import models.Product;

public class ProductView extends JFrame {
    private final int customerId;
    private int actualCustomerId;
    private final HeaderPanel headerPanel;
    private final FooterPanel footerPanel;
    private final JPanel mainPanel;
    private final JPanel productGridPanel;
    private final JScrollPane scrollPane;
    private boolean isLoading = false;

    private static final String SELECT_PRODUCTS = """
            SELECT product_id, product_name, description,
                   price_per_kg, image_url, stock_quantity, category
            FROM product
            """;

    private static final String CHECK_CUSTOMER = """
            SELECT customer_id FROM customer WHERE customer_id = ?
            """;

    private static final String INSERT_CART_ITEM = """
            INSERT INTO cart (cart_cst_id, cart_prd_id, quantity)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE quantity = quantity + ?
            """;

    private static final String VERIFY_CUSTOMER = """
            SELECT customer_id, customer_name FROM customer WHERE customer_id = ?
            """;

    private static final String GET_CUSTOMER_ID = """
            SELECT customer_id FROM customer WHERE cst_user_id = ?
            """;

    public ProductView(int customerId) {
        this.customerId = customerId;
        this.actualCustomerId = getActualCustomerId(customerId);
        System.out.println("ProductView initialized with customer ID: " + customerId); // Debug line

        this.headerPanel = new HeaderPanel();
        this.footerPanel = new FooterPanel();
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
        this.productGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        this.scrollPane = new JScrollPane(productGridPanel);

        setupUI();
        loadProducts();
        configureFrame();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Style the main panel
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Style the product grid panel
        productGridPanel.setBackground(Color.WHITE);

        // Configure scroll pane
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh Products");
        refreshButton.addActionListener(e -> loadProducts());

        // Add components to main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(new JLabel("Available Products", SwingConstants.CENTER), BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Assemble the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        if (isLoading) {
			return;
		}
        isLoading = true;
        productGridPanel.removeAll();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                List<Product> products = new ArrayList<>();
                try (Connection conn = DBManagementGDA.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(SELECT_PRODUCTS);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        products.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("description"),
                            rs.getDouble("price_per_kg"),
                            rs.getString("image_url"),
                            rs.getInt("stock_quantity")
                        ));
                    }
                }
                return products;
            }

            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    displayProducts(products);
                } catch (Exception e) {
                    handleError("Error loading products: " + e.getMessage());
                } finally {
                    isLoading = false;
                }
            }
        };
        worker.execute();
    }

    private void displayProducts(List<Product> products) {
        for (Product product : products) {
            productGridPanel.add(createProductCard(product));
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        // Product image
        ImageIcon imageIcon = loadImage(product.getImageUrl());
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Product name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f/kg", product.getPricePerKg()));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quantity spinner
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quantityPanel.setBackground(Color.WHITE);
        JLabel quantityLabel = new JLabel("Quantity (kg):");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1.0, 1.0, 100.0, 0.5));
        quantitySpinner.setPreferredSize(new Dimension(60, 25));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        quantityPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to cart button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.addActionListener(e -> addToCart(product, (Double) quantitySpinner.getValue()));

        // Add components to card
        card.add(Box.createVerticalStrut(10));
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(quantityPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(addToCartButton);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private ImageIcon loadImage(String imageUrl) {
        ImageIcon icon = new ImageIcon(imageUrl);
        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private int getActualCustomerId(int userId) {
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_CUSTOMER_ID)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer ID: " + e.getMessage());
        }
        return -1;
    }

    private void addToCart(Product product, double quantity) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (Connection conn = DBManagementGDA.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(INSERT_CART_ITEM)) {
                    
                    pstmt.setInt(1, customerId);
                    pstmt.setInt(2, product.getProductId());
                    pstmt.setDouble(3, quantity);  // For INSERT
                    pstmt.setDouble(4, quantity);  // For UPDATE when duplicate
                    
                    return pstmt.executeUpdate() > 0;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        int choice = JOptionPane.showConfirmDialog(
                            ProductView.this,
                            "Product added to cart! Would you like to view your cart?",
                            "Success",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                        );

                        if (choice == JOptionPane.YES_OPTION) {
                            viewCart();
                        }
                    }
                } catch (Exception e) {
                    handleError("Error adding to cart: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void viewCart() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Opening cart view with customer ID: " + customerId);
            CartView cartView = new CartView(customerId);
            cartView.setVisible(true);
            dispose();
        });
    }

    private void configureFrame() {
        setTitle("Products");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void verifyCustomer() {
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(VERIFY_CUSTOMER)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Customer verified: ID=" + rs.getInt("customer_id") +
                                     ", Name=" + rs.getString("customer_name"));
                } else {
                    System.out.println("Customer not found with ID: " + customerId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying customer: " + e.getMessage());
        }
    }

    private void handleError(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    // For testing
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            new ProductView(1).setVisible(true);  // Using test customer ID 1
//        });
//    }
}
