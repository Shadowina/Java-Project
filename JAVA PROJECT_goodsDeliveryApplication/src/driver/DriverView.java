package driver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import customer.CustomerDashboard;
import customer.CustomerProfileView;
import dbmanagement.DriverDAO;
import dbmanagement.DriverDAO.Mission;

public class DriverView extends JFrame {
    private final JPanel mainPanel;
    private final JPanel contentPanel;
    private final JTable missionTable;
    private final DefaultTableModel tableModel;
    private final int driverId;
    private final List<Integer> selectedMissions;
    private JButton completeButton;

    public DriverView(int userId) {
        super("Driver Dashboard");
        
        // Initialize components first
        this.driverId = userId;
        this.selectedMissions = new ArrayList<>();
        this.mainPanel = new JPanel(new BorderLayout());
        this.contentPanel = new JPanel(new BorderLayout());
        this.tableModel = createTableModel();
        this.missionTable = createMissionTable();

        // Setup the UI
        setupUI();
        
        // Load missions
        loadMissions();
        
        // Configure and show frame
        configureFrame();
        setVisible(true);
        
        System.out.println("DriverView initialized for user ID: " + userId);
    }

    private void setupUI() {
        // Create main layout
        mainPanel.setLayout(new BorderLayout());
        
        // Setup content
        setupContent();
        
        // Setup menu
        setupMenu();
        
        // Add panels to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        setContentPane(mainPanel);
    }

    private void setupContent() {
        JPanel missionPanel = new JPanel(new BorderLayout(10, 10));
        missionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title
        JLabel titleLabel = new JLabel("Your Missions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        missionPanel.add(titleLabel, BorderLayout.NORTH);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(missionTable);
        missionPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Complete button
        completeButton = new JButton("Complete Mission");
        completeButton.setBackground(new Color(15, 113, 210));
        completeButton.setForeground(Color.WHITE);
        completeButton.setEnabled(false);
        completeButton.addActionListener(e -> completeMission());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(completeButton);
        missionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add mission panel to the right of the menu
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(missionPanel, BorderLayout.CENTER);
        
        contentPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel setupMenu() {
        // Create menu panel with BoxLayout for vertical alignment
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(75, 0, 130));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Top menu items
        JButton assignedMissionsBtn = createMenuButton("Assigned Missions");
        JButton completedMissionsBtn = createMenuButton("Completed Missions");

        // Bottom menu items
        JButton profileBtn = createMenuButton("Profile");
        JButton signOutBtn = createMenuButton("Sign Out");

        // Add action listeners
        assignedMissionsBtn.addActionListener(e -> showMissionsPanel());; // Disable since we're already in this view
        completedMissionsBtn.addActionListener(e -> {
            dispose(); // Close current window
            new MissionHistoryView(driverId); // Open mission history view
        });
        profileBtn.addActionListener(e -> showProfile());
        signOutBtn.addActionListener(e -> signOut());

        // Add components to menu
        menuPanel.add(assignedMissionsBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(completedMissionsBtn);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(profileBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(signOutBtn);

        contentPanel.add(menuPanel, BorderLayout.WEST);
        return menuPanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(75, 0, 130));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(95, 20, 150));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 0, 130));
            }
        });

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new Object[]{"Select", "Mission Date", "Mission ID", "Status", "Orders"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
    }

    private JTable createMissionTable() {
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                updateCompleteButtonState();
            }
        });
        return table;
    }

    private void loadMissions() {
        try {
            tableModel.setRowCount(0);
            selectedMissions.clear();

            System.out.println("Loading missions for user ID: " + driverId);
            List<Mission> missions = DriverDAO.getDriverMissions(driverId);
            System.out.println("Retrieved " + missions.size() + " missions");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            for (Mission mission : missions) {
                Object[] row = {
                    false,
                    mission.missionDate().format(formatter),
                    mission.missionId(),
                    mission.status(),
                    mission.orderCount() + (mission.orderCount() == 1 ? " order" : " orders")
                };
                tableModel.addRow(row);
                System.out.println("Added mission to table: " + mission.missionId());
            }

            if (tableModel.getRowCount() == 0) {
                System.out.println("No missions found for driver");
                showMessage("No missions assigned.", "Information");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading missions: " + e.getMessage());
        }
    }

    private void completeMission() {
        List<Integer> selectedMissionIds = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Boolean) tableModel.getValueAt(i, 0)) {
                selectedMissionIds.add((Integer) tableModel.getValueAt(i, 2));
            }
        }

        if (selectedMissionIds.isEmpty()) {
            showMessage("Please select at least one mission to complete.", "Warning");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to mark " + 
            (selectedMissionIds.size() == 1 ? "this mission" : "these missions") + 
            " as completed?",
            "Confirm Completion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean allCompleted = true;
                for (Integer missionId : selectedMissionIds) {
                    if (!DriverDAO.updateMissionStatus(missionId, driverId, "Completed")) {
                        allCompleted = false;
                        break;
                    }
                }

                if (allCompleted) {
                    showMessage(
                        selectedMissionIds.size() == 1 
                            ? "Mission marked as completed successfully!" 
                            : "All selected missions marked as completed successfully!",
                        "Success"
                    );
                    loadMissions(); // Refresh the table
                } else {
                    showError("Some missions could not be completed. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error completing mission(s): " + e.getMessage());
            }
        }
    }

    private void updateCompleteButtonState() {
        completeButton.setEnabled(tableModel.getDataVector().stream().anyMatch(row -> (Boolean) ((Vector<?>) row).get(0)));
    }

    private void configureFrame() {
        setTitle("Driver Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        // Ensure the frame is visible
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            System.out.println("Frame made visible");
        });
    }

    private void showProfile() { //contentPanel.removeAll();
    
    dispose(); // Close the dashboard
    JFrame frame = new JFrame("Driver Profile");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(600, 500);
    frame.setLocationRelativeTo(null); // Center on screen
    
    DriverProfileView profileView = new DriverProfileView(driverId);
    frame.add(profileView);
    frame.setVisible(true);

    // Add window listener to handle when profile is closed
    frame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            // Reopen the dashboard when profile is closed
            new DriverView(driverId).setVisible(true);
        }
    });
    
    // Create and add the profile view
//    DriverProfileView profileView = new DriverProfileView(driverId);
//    contentPanel.add(profileView, BorderLayout.CENTER);
    
    // Refresh the display
    contentPanel.revalidate();
    contentPanel.repaint();
    }
    
    private void showMissionsPanel() {
        // Clear the current content panel
        contentPanel.removeAll();
        
        // Recreate the missions panel
        JPanel missionPanel = new JPanel(new BorderLayout(10, 10));
        missionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title
        JLabel titleLabel = new JLabel("Your Missions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        missionPanel.add(titleLabel, BorderLayout.NORTH);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(missionTable);
        missionPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Complete button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(completeButton);
        missionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add mission panel to content panel
        contentPanel.add(missionPanel, BorderLayout.CENTER);
        
        // Refresh the display
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private void signOut() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
