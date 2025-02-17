package dbmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Product;

//import components.ProductGridPanel.Product;


public class ProductDAO {
    private static final String SELECT_ALL_PRODUCTS = """
        SELECT product_id, product_name, description, price_per_kg,
               image_url, stock_quantity, category
        FROM product
        """;

    private static final String SELECT_PRODUCTS_BY_CATEGORY = """
        SELECT product_id, product_name, description, price_per_kg,
               image_url, stock_quantity, category
        FROM product
        WHERE category = ?
        """;

    private static final String SEARCH_PRODUCTS = """
        SELECT product_id, product_name, description, price_per_kg,
               image_url, stock_quantity, category
        FROM product
        WHERE LOWER(product_name) LIKE ?
        OR LOWER(description) LIKE ?
        """;

    private static final String SELECT_RANDOM_PRODUCTS = """
        SELECT product_id, product_name, description, price_per_kg,
               image_url, stock_quantity, category
        FROM product
        ORDER BY RAND()
        LIMIT 12
        """;

    public static List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBManagementGDA.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SELECT_ALL_PRODUCTS);

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }
        } finally {
            if (rs != null) {
				try { rs.close(); } catch (SQLException e) { /* ignored */ }
			}
            if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { /* ignored */ }
			}
            if (conn != null) {
				try { conn.close(); } catch (SQLException e) { /* ignored */ }
			}
        }

        return products;
    }

    public static List<Product> getProductsByCategory(String category) throws SQLException {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManagementGDA.getConnection();
            pstmt = conn.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY);
            pstmt.setString(1, category);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }
        } finally {
            if (rs != null) {
				try { rs.close(); } catch (SQLException e) { /* ignored */ }
			}
            if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { /* ignored */ }
			}
            if (conn != null) {
				try { conn.close(); } catch (SQLException e) { /* ignored */ }
			}
        }

        return products;
    }

    public static List<Product> searchProducts(String searchTerm) throws SQLException {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManagementGDA.getConnection();
            pstmt = conn.prepareStatement(SEARCH_PRODUCTS);
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }
        } finally {
            if (rs != null) {
				try { rs.close(); } catch (SQLException e) { /* ignored */ }
			}
            if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { /* ignored */ }
			}
            if (conn != null) {
				try { conn.close(); } catch (SQLException e) { /* ignored */ }
			}
        }

        return products;
    }

    public static List<Product> getRandomProducts() throws SQLException {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RANDOM_PRODUCTS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }
        }

        return products;
    }

    private static Product createProductFromResultSet(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("product_name"),
            rs.getString("description"),
            rs.getDouble("price_per_kg"),
            rs.getString("image_url"),
            rs.getInt("stock_quantity")
            
        );
    }
}