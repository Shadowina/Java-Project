package utils;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dbmanagement.DBManagementGDA;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;



public class MissionDocumentGenerator {
    private static final String WAREHOUSE_ADDRESS = "123 Warehouse Street, Rouen"; // Replace with actual warehouse address

    public static void generateMissionDocument(java.util.Date selectedDate, String outputPath) throws Exception {
        // Create document
        XWPFDocument document = new XWPFDocument();

        // Add title
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String titleText = "Missions for " + dateFormat.format(selectedDate);
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.setText(titleText);
        titleRun.addBreak();

        // Get missions data from database
        List<MissionData> missions = getMissionsForDate(selectedDate);

        // Add each mission to document
        for (MissionData mission : missions) {
            addMissionToDocument(document, mission);
        }

        // Save document
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            document.write(out);
        }
    }

    private static void addMissionToDocument(XWPFDocument document, MissionData mission) {
        // Add mission header
        XWPFParagraph missionHeader = document.createParagraph();
        missionHeader.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun missionHeaderRun = missionHeader.createRun();
        missionHeaderRun.setBold(true);
        missionHeaderRun.setText("Driver: " + mission.driverName);
        missionHeaderRun.addBreak();
        missionHeaderRun.setText("Mission ID: " + mission.missionId);
        missionHeaderRun.addBreak();

        // Add route header
        XWPFParagraph routeHeader = document.createParagraph();
        XWPFRun routeRun = routeHeader.createRun();
        routeRun.setBold(true);
        routeRun.setText("Route:");
        routeRun.addBreak();

        // Add starting point
        XWPFParagraph startPoint = document.createParagraph();
        XWPFRun startRun = startPoint.createRun();
        startRun.setText("Start: " + WAREHOUSE_ADDRESS);
        startRun.addBreak();

        // Add delivery points
        for (int i = 0; i < mission.deliveryAddresses.size(); i++) {
            XWPFParagraph deliveryPoint = document.createParagraph();
            XWPFRun deliveryRun = deliveryPoint.createRun();
            deliveryRun.setText((i + 1) + ". " + mission.deliveryAddresses.get(i));
            deliveryRun.addBreak();
        }

        // Add return to warehouse
        XWPFParagraph returnPoint = document.createParagraph();
        XWPFRun returnRun = returnPoint.createRun();
        returnRun.setText("End: " + WAREHOUSE_ADDRESS);
        returnRun.addBreak();
    }


    private static List<MissionData> getMissionsForDate(java.util.Date selectedDate) throws SQLException {
        List<MissionData> missions = new ArrayList<>();

        Connection conn = DriverManager.getConnection(DBManagementGDA.URL,
                                                   DBManagementGDA.USER,
                                                   DBManagementGDA.PASSWORD);

        String query = "SELECT m.mission_id, d.dr_full_name, o.delivery_address " +
                      "FROM mission m " +
                      "JOIN driver d ON m.msn_dr_id = d.driver_id " +
                      "JOIN mission_order mo ON m.mission_id = mo.mord_mission_id " +
                      "JOIN `order` o ON mo.mord_order_id = o.order_id " +
                      "WHERE DATE(m.mission_date) = ? " +
                      "ORDER BY m.mission_id, mo.sequence_number";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDate(1, new java.sql.Date(selectedDate.getTime()));

        ResultSet rs = pstmt.executeQuery();

        // Add this debug print
        System.out.println("Executing query for date: " + selectedDate);

        MissionData currentMission = null;

        while (rs.next()) {
            int missionId = rs.getInt("mission_id");

            if (currentMission == null || currentMission.missionId != missionId) {
                currentMission = new MissionData(
                    missionId,
                    rs.getString("dr_full_name")
                );
                missions.add(currentMission);
            }

            currentMission.deliveryAddresses.add(rs.getString("delivery_address"));
        }

        // Add this debug print
        System.out.println("Number of missions found: " + missions.size());

        rs.close();
        pstmt.close();
        conn.close();

        return missions;
    }
    private static class MissionData {
        int missionId;
        String driverName;
        List<String> deliveryAddresses;

        MissionData(int missionId, String driverName) {
            this.missionId = missionId;
            this.driverName = driverName;
            this.deliveryAddresses = new ArrayList<>();
        }
    }
}