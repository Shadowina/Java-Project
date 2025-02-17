package dbmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    private static final String GET_ASSIGNED_ORDERS = """
        SELECT o.order_id, c.customer_name, o.delivery_address, o.status
        FROM order o
        JOIN customer c ON o.customer_id = c.customer_id
        WHERE o.driver_id = ? AND o.status != 'Delivered'
        ORDER BY o.order_date DESC
        """;

    private static final String UPDATE_ORDER_STATUS = """
        UPDATE order
        SET status = ?,
            updated_at = CURRENT_TIMESTAMP
        WHERE order_id = ? AND driver_id = ?
        """;

    private static final String GET_DRIVER_DETAILS = """
        SELECT driver_name, phone_number, email, status
        FROM driver
        WHERE driver_id = ?
        """;

    private static final String GET_DRIVER_MISSIONS = """
        SELECT m.mission_id, m.mission_date, m.mission_status,
               COUNT(mo.mord_order_id) as order_count
        FROM mission m
        LEFT JOIN mission_order mo ON m.mission_id = mo.mord_mission_id
        WHERE m.msn_dr_id = (SELECT driver_id FROM driver WHERE dr_user_id = ?)
        AND m.mission_status = 'Pending'
        GROUP BY m.mission_id, m.mission_date, m.mission_status
        ORDER BY m.mission_date DESC
        """;

    private static final String UPDATE_MISSION_STATUS = """
        UPDATE mission 
        SET mission_status = ?, 
            mission_end_time = CURRENT_TIMESTAMP,
            updated_at = CURRENT_TIMESTAMP
        WHERE mission_id = ? 
        AND msn_dr_id = (SELECT driver_id FROM driver WHERE dr_user_id = ?)
        """;

    private static final String GET_COMPLETED_MISSIONS = """
        SELECT m.mission_id, m.mission_date, m.mission_status,
               m.mission_start_time, m.mission_end_time,
               COUNT(mo.mord_order_id) as total_orders
        FROM mission m
        LEFT JOIN mission_order mo ON m.mission_id = mo.mord_mission_id
        WHERE m.msn_dr_id = (SELECT driver_id FROM driver WHERE dr_user_id = ?)
        AND m.mission_status = 'Completed'
        GROUP BY m.mission_id, m.mission_date, m.mission_status,
                 m.mission_start_time, m.mission_end_time
        ORDER BY m.mission_date DESC
        """;

    public static List<Order> getAssignedOrders(int driverId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ASSIGNED_ORDERS)) {

            pstmt.setInt(1, driverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("delivery_address"),
                        rs.getString("status")
                    ));
                }
            }
        }

        return orders;
    }

    public static boolean updateOrderStatus(int orderId, int driverId, String newStatus)
            throws SQLException {
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_ORDER_STATUS)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, driverId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static Driver getDriverDetails(int driverId) throws SQLException {
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_DRIVER_DETAILS)) {

            pstmt.setInt(1, driverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Driver(
                        driverId,
                        rs.getString("driver_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public static List<Mission> getDriverMissions(int userId) throws SQLException {
        List<Mission> missions = new ArrayList<>();
        
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_DRIVER_MISSIONS)) {
            
            pstmt.setInt(1, userId);
            System.out.println("Executing query for user_id: " + userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mission mission = new Mission(
                        rs.getInt("mission_id"),
                        rs.getDate("mission_date").toLocalDate(),
                        rs.getString("mission_status"),
                        rs.getInt("order_count")
                    );
                    missions.add(mission);
                    System.out.println("Found mission: " + mission.missionId() + 
                                     ", status: " + mission.status() + 
                                     ", date: " + mission.missionDate());
                }
            }
        }
        
        return missions;
    }

    public static boolean updateMissionStatus(int missionId, int userId, String status) throws SQLException {
        try (Connection conn = DBManagementGDA.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement pstmt = conn.prepareStatement(UPDATE_MISSION_STATUS);
                pstmt.setString(1, status);
                pstmt.setInt(2, missionId);
                pstmt.setInt(3, userId);
                
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    // Update associated orders status
                    PreparedStatement orderPstmt = conn.prepareStatement("""
                        UPDATE `order` o
                        JOIN mission_order mo ON o.order_id = mo.mord_order_id
                        SET o.order_status = 'Delivered'
                        WHERE mo.mord_mission_id = ?
                        """);
                    orderPstmt.setInt(1, missionId);
                    orderPstmt.executeUpdate();
                    
                    conn.commit();
                    return true;
                }
                
                conn.rollback();
                return false;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public static List<MissionHistory> getMissionHistory(int userId) throws SQLException {
        List<MissionHistory> history = new ArrayList<>();
        
        try (Connection conn = DBManagementGDA.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_COMPLETED_MISSIONS)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(new MissionHistory(
                    rs.getInt("mission_id"),
                    rs.getDate("mission_date").toLocalDate(),
                    rs.getString("mission_status"),
                    rs.getTimestamp("mission_start_time"),
                    rs.getTimestamp("mission_end_time"),
                    rs.getInt("total_orders")
                ));
            }
        }
        
        return history;
    }

    // Record classes for data transfer
    public record Order(
        int orderId,
        String customerName,
        String deliveryAddress,
        String status
    ) {}

    public record Driver(
        int driverId,
        String driverName,
        String phoneNumber,
        String email,
        String status
    ) {}

    // Record class for Mission data
    public record Mission(
        int missionId,
        LocalDate missionDate,
        String status,
        int orderCount
    ) {}

    // Record class for Mission History
    public record MissionHistory(
        int missionId,
        LocalDate missionDate,
        String status,
        Timestamp startTime,
        Timestamp endTime,
        int totalOrders
    ) {}
}