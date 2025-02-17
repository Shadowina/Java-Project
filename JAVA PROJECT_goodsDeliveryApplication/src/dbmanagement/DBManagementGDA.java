package dbmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.User;


public class DBManagementGDA {
    private static final Logger LOGGER = Logger.getLogger(DBManagementGDA.class.getName());

    // Database connection details
    public static final String URL = "jdbc:mysql://localhost:3306/dbgoodsdeliveryapp";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    static {
        try {
            // Initialize the database connection
            getConnection();
            LOGGER.info("Database connection initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection", e);
            throw new RuntimeException("Database connection initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closePool() {
        // This method is no longer used with the new connection approach
    }

    public static boolean registerIntoDB(User user) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO user (user_name, user_email, user_password, phone_no, role) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getUserName());
                pstmt.setString(2, user.getUserEmail());
                pstmt.setString(3, user.getUserPassword());
                pstmt.setString(4, user.getPhoneNumber());
                pstmt.setString(5, user.getRole().name());

                int rowsAffected = pstmt.executeUpdate();

                if (user.getRole().name().equals("DRIVER")) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int userId = rs.getInt(1);
                            String driverSql = "INSERT INTO driver (dr_user_id, dr_full_name, truck_registration, truck_capacity, driver_status) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement driverPstmt = conn.prepareStatement(driverSql)) {
                                driverPstmt.setInt(1, userId);
                                driverPstmt.setString(2, user.getUserName());
                                driverPstmt.setString(3, "PENDING");
                                driverPstmt.setDouble(4, 0.0);
                                driverPstmt.setString(5, "INACTIVE");
                                driverPstmt.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public static boolean updateDriverTruckInfo(int userId, String truckRegNo, double truckCapacity) throws SQLException {
        String sql = "UPDATE driver SET truck_registration = ?, truck_capacity = ?, driver_status = ? WHERE dr_user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, truckRegNo);
            pstmt.setDouble(2, truckCapacity);
            pstmt.setString(3, "ACTIVE");
            pstmt.setInt(4, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static ResultSet getDriverInfo(int userId) throws SQLException {
        String sql = "SELECT d.*, u.user_name, u.user_email, u.phone_no "
                  + "FROM driver d "
                  + "JOIN user u ON d.dr_user_id = u.user_id "
                  + "WHERE d.dr_user_id = ?";

        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);

        return pstmt.executeQuery();
    }

    public static boolean insertCustomerInfo(int userId, String fullName) throws SQLException {
        String sql = "INSERT INTO customer (cst_user_id, cst_full_name, cst_status) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, "ACTIVE");

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static ResultSet getCustomerInfo(int userId) throws SQLException {
        String sql = "SELECT c.*, u.user_name, u.user_email, u.phone_no "
                  + "FROM customer c "
                  + "JOIN user u ON c.cst_user_id = u.user_id "
                  + "WHERE c.cust_user_id = ?";

        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);

        return pstmt.executeQuery();
    }

    public static boolean insertSchedulerInfo(int userId, String fullName, String email) throws SQLException {
        String sql = "INSERT INTO scheduler (sch_user_id, sch_full_name, sch_email, sch_status) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, "ACTIVE");

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static ResultSet getSchedulerInfo(int userId) throws SQLException {
        String sql = "SELECT s.*, u.user_name, u.user_email, u.phone_no "
                  + "FROM scheduler s "
                  + "JOIN user u ON s.sch_user_id = u.user_id "
                  + "WHERE s.sch_user_id = ?";

        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);

        return pstmt.executeQuery();
    }

    public static ResultSet getAllProducts() throws SQLException {
        String sql = "SELECT * FROM product";

        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    public static User getUserByEmail(String email) throws SQLException {
        String sql = """
            SELECT u.*, 
                   COALESCE(d.dr_user_id, c.cst_user_id, s.sch_user_id) as role_user_id,
                   CASE 
                       WHEN d.dr_user_id IS NOT NULL THEN 'DRIVER'
                       WHEN c.cst_user_id IS NOT NULL THEN 'CUSTOMER'
                       WHEN s.sch_user_id IS NOT NULL THEN 'SCHEDULER'
                   END as user_role
            FROM user u
            LEFT JOIN (SELECT DISTINCT dr_user_id FROM driver WHERE driver_status = 'ACTIVE') d ON u.user_id = d.dr_user_id
            LEFT JOIN customer c ON u.user_id = c.cst_user_id AND c.cst_status = 'ACTIVE'
            LEFT JOIN scheduler s ON u.user_id = s.sch_user_id AND s.sch_status = 'ACTIVE'
            WHERE u.user_email = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("user_name"),
                        rs.getString("user_email"),
                        rs.getString("phone_no"),
                        rs.getString("user_password"),
                        User.Role.valueOf(rs.getString("user_role"))
                    );
                }
            }
        }
        return null;
    }
}