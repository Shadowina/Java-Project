package scheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import dbmanagement.DBManagementGDA;
import scheduler.ManageOrdersView.ButtonEditor;
import scheduler.ManageOrdersView.ButtonRenderer;
import scheduler.components.GenerateMissionDocumentDialog;
import utils.MissionDocumentGenerator;

public class ReportView extends JFrame{
	 	private JTable missionTable;
	    private DefaultTableModel tableModel;
	    private final List<Integer> selectedMissions;
	    private SchedulerView parentView;
	    private int userId;
	    
	    
	    public ReportView (SchedulerView parentView, int userId) {
	    	this.parentView = parentView;
	    	this.userId = userId;
	        this.selectedMissions = new ArrayList<>();
	        
	        
	        
	        setTitle("View Missions");
	        setLayout(new BorderLayout());
	        
	        // Create main panel
	        JPanel mainPanel = createMainPanel();
	        add(mainPanel);

	        // Set frame properties
	        setSize(1000, 600);
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
	            //parentView.setVisible(true);
	            this.dispose();
	            new SchedulerView(userId);
	        });
	        titlePanel.add(backButton, BorderLayout.WEST);

	        // Title
	        JLabel titleLabel = new JLabel("Manage Missions");
	        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
	        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        titlePanel.add(titleLabel, BorderLayout.CENTER);

	        mainPanel.add(titlePanel, BorderLayout.NORTH);

	        // Create table
	        createMissionTable();
	        JScrollPane scrollPane = new JScrollPane(missionTable);
	        scrollPane.setBorder(BorderFactory.createEmptyBorder());

	        // Create buttons panel
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
	        buttonPanel.setBackground(Color.WHITE);

	        JButton generateButton = new JButton("Generate Report");
	        generateButton.setFont(new Font("Arial", Font.PLAIN, 14));
	        generateButton.addActionListener(e -> generateDocument());
	        
	        buttonPanel.add(generateButton);
	       

	        mainPanel.add(scrollPane, BorderLayout.CENTER);
	        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

	        return mainPanel;
	    }
	    
	    private void createMissionTable() {
	        String[] columns = {
	            "Select",  // Checkbox column
	            "Mission Date",
	            "Mission ID",
	            "Driver",
	            "Status" 	        
	            };

	        tableModel = new DefaultTableModel(columns, 0) {
	            @Override
	            public Class<?> getColumnClass(int columnIndex) {
	                return columnIndex == 0 ? Boolean.class : Object.class;
	            }

	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return column == 0;  //Only check box column is editable
	            }
	        };
	    
	
	    missionTable = new JTable(tableModel);
        missionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        missionTable.setRowHeight(30);
        missionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        missionTable.getTableHeader().setBackground(new Color(240, 240, 240));
        missionTable.setShowGrid(true);
        missionTable.setGridColor(new Color(230, 230, 230));
        
        // Center align all columns except checkbox and move buttons
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < missionTable.getColumnCount() - 1; i++) {
            missionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        TableColumnModel columnModel = missionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);  // Checkbox
        columnModel.getColumn(0).setMaxWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80);   // Mission Date
        columnModel.getColumn(2).setPreferredWidth(80);  // Mission ID
        columnModel.getColumn(3).setPreferredWidth(170);  // Driver
        columnModel.getColumn(4).setPreferredWidth(90);  // Status
        

        loadMission();
	    
}
	    
	    private void loadMission() {
	        try (Connection conn = DBManagementGDA.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(
	                 "SELECT m.mission_id, m.mission_date, m.mission_status, " +
	                 "d.dr_full_name, s.sch_full_name, m.mission_start_time, m.mission_end_time " +
	                 "FROM `mission` m " +
	                 "JOIN driver d ON m.msn_dr_id = d.driver_id " +
	                 "JOIN scheduler s ON m.msn_sch_id = s.scheduler_id " +
	                 "ORDER BY m.mission_date DESC")) {

	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                Object[] row = {
	                    false,
	                    rs.getInt("mission_id"),
	                    formatDate(rs.getDate("mission_date")),
	                    rs.getString("dr_full_name"),
	                    rs.getString("mission_status"),
	                    rs.getTimestamp("mission_start_time") != null ? rs.getTimestamp("mission_start_time").toString() : "N/A",
	                    rs.getTimestamp("mission_end_time") != null ? rs.getTimestamp("mission_end_time").toString() : "N/A",
	                    rs.getString("sch_full_name")
	                };
	                tableModel.addRow(row);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this,
	                "Error loading mission data: " + e.getMessage(),
	                "Database Error",
	                JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    
	    private String formatDate(Date date) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        return sdf.format(date);
	    }
	    
	    //Check it later
	    
//	    private void generateDocument() {
//	        List<Integer> selectedMissionIds = new ArrayList<>();
//	        for (int i = 0; i < tableModel.getRowCount(); i++) {
//	            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
//	            if (isSelected != null && isSelected) {
//	                Integer missionId = (Integer) tableModel.getValueAt(i, 2);
//	                selectedMissionIds.add(missionId);
//	            }
//	        }
//
//	        if (selectedMissionIds.isEmpty()) {
//	            JOptionPane.showMessageDialog(this,
//	                "Please select at least one order to generate document.",
//	                "No Selection",
//	                JOptionPane.WARNING_MESSAGE);
//	            return;
//	        }
//
//	        GenerateMissionDocumentDialog dialog = new GenerateMissionDocumentDialog(this);
//	        dialog.setVisible(true);
//	    }
	    
	    private void generateDocument() {
	        try (Connection conn = DBManagementGDA.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(
	                 "SELECT DISTINCT mission_date FROM mission ORDER BY mission_date ASC")) {

	            // Fetch unique dates from the mission table
	            ResultSet rs = pstmt.executeQuery();
	            List<java.util.Date> uniqueDates = new ArrayList<>();
	            while (rs.next()) {
	                uniqueDates.add(rs.getDate("mission_date"));
	            }

	            if (uniqueDates.isEmpty()) {
	                JOptionPane.showMessageDialog(this,
	                    "No mission dates found in the database.",
	                    "No Data",
	                    JOptionPane.INFORMATION_MESSAGE);
	                return;
	            }

	            // Show a dialog for the user to select a date
	            java.util.Date selectedDate = (java.util.Date) JOptionPane.showInputDialog(
	                this,
	                "Select a date to generate missions:",
	                "Select Date",
	                JOptionPane.QUESTION_MESSAGE,
	                null,
	                uniqueDates.toArray(),
	                uniqueDates.get(0)
	            );

	            if (selectedDate == null) {
	                JOptionPane.showMessageDialog(this,
	                    "No date selected. Document generation cancelled.",
	                    "Cancelled",
	                    JOptionPane.WARNING_MESSAGE);
	                return;
	            }

	            // Pass the selected date to the MissionDocumentGenerator
	            String outputPath = "Mission_Document_" + new java.text.SimpleDateFormat("yyyyMMdd").format(selectedDate) + ".docx";
	            MissionDocumentGenerator.generateMissionDocument(selectedDate, outputPath);

	            JOptionPane.showMessageDialog(this,
	                "Mission document generated successfully at: " + outputPath,
	                "Success",
	                JOptionPane.INFORMATION_MESSAGE);

	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this,
	                "Error fetching mission dates: " + e.getMessage(),
	                "Database Error",
	                JOptionPane.ERROR_MESSAGE);
	        } catch (Exception e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this,
	                "Error generating mission document: " + e.getMessage(),
	                "Document Generation Error",
	                JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    
}
