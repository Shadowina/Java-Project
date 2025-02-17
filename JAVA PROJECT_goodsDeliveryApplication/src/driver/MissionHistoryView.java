package driver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import dbmanagement.DriverDAO;
import dbmanagement.DriverDAO.MissionHistory;

public class MissionHistoryView extends JFrame {
    private final JPanel mainPanel;
    private final JPanel contentPanel;
    private final int driverId;
    private final JTable historyTable;
    private final DefaultTableModel tableModel;

    public MissionHistoryView(int driverId) {
        super("Mission History");
        this.driverId = driverId;
        this.mainPanel = new JPanel(new BorderLayout());
        this.contentPanel = new JPanel(new BorderLayout());
        
        // Initialize table
        this.tableModel = createTableModel();
        this.historyTable = createHistoryTable();

        // Setup UI
        setupUI();
        
        // Load data
        loadMissionHistory();
        
        // Configure and show frame
        configureFrame();
        setVisible(true);
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
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title
        JLabel titleLabel = new JLabel("Mission History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        historyPanel.add(titleLabel, BorderLayout.NORTH);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // Add history panel to the right of the menu
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(historyPanel, BorderLayout.CENTER);
        
        contentPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private void setupMenu() {
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
        assignedMissionsBtn.addActionListener(e -> {
            dispose();
            new DriverView(driverId);
        });
        completedMissionsBtn.setEnabled(false);
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
        return new DefaultTableModel(
            new Object[]{"Mission ID", "Date", "Status", "Start Time", "End Time", "Total Orders"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createHistoryTable() {
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mission ID
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Time
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // End Time
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Total Orders
        
        return table;
    }

    private void loadMissionHistory() {
        try {
            tableModel.setRowCount(0);
            List<MissionHistory> history = DriverDAO.getMissionHistory(driverId);
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            for (MissionHistory mission : history) {
                Object[] row = {
                    mission.missionId(),
                    mission.missionDate().format(dateFormatter),
                    mission.status(),
                    mission.startTime(),
                    mission.endTime(),
                    mission.totalOrders() + (mission.totalOrders() == 1 ? " order" : " orders")
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                showMessage("No completed missions found.", "Information");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading mission history: " + e.getMessage());
        }
    }

    private void configureFrame() {
        setTitle("Mission History");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void showProfile() {
        JOptionPane.showMessageDialog(this, 
            "Profile feature coming soon!", 
            "Profile", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void signOut() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to sign out?",
            "Sign Out",
            JOptionPane.YES_NO_OPTION
        );
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