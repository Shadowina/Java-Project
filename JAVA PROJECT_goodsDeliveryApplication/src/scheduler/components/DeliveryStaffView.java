package scheduler.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scheduler.SchedulerView;
import userauthentication.SignOut;

public class DeliveryStaffView extends JPanel {
    private JPanel menuPanel;
    private final int userId;
    private SchedulerView parentView;

    public DeliveryStaffView(int userId, SchedulerView parentView) {
        this.userId = userId;
        this.parentView = parentView;
        setLayout(new BorderLayout());
        createMenuPanel();
    }

    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setLayout(new BorderLayout());

        // Top section of menu
        JPanel topMenu = new JPanel();
        topMenu.setLayout(new BoxLayout(topMenu, BoxLayout.Y_AXIS));
        topMenu.setBackground(new Color(51, 51, 51));
        topMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo
        ImageIcon originalIcon = new ImageIcon("icons/truck.png");
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel logoLabel = new JLabel("ezzyDelivery");
        logoLabel.setIcon(resizedIcon);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Menu items
        JLabel deliveryLabel = createMenuLabel("Delivery");
        JLabel reportLabel = createMenuLabel("Report");

        topMenu.add(logoLabel);
        topMenu.add(Box.createRigidArea(new Dimension(0, 30)));
        topMenu.add(deliveryLabel);
        topMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        topMenu.add(reportLabel);

        // Bottom section of menu
        JPanel bottomMenu = new JPanel();
        bottomMenu.setLayout(new BoxLayout(bottomMenu, BoxLayout.Y_AXIS));
        bottomMenu.setBackground(new Color(51, 51, 51));
        bottomMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel profileLabel = createMenuLabel("Profile");
        JLabel signOutLabel = createMenuLabel("Sign Out");

        bottomMenu.add(profileLabel);
        bottomMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        bottomMenu.add(signOutLabel);

        menuPanel.add(topMenu, BorderLayout.NORTH);
        menuPanel.add(bottomMenu, BorderLayout.SOUTH);
        
        // Add click Listener for Report
        
        reportLabel.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
                openReport();
            }
        });

        // Add click listener for Profile
        profileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openProfile();
            }
        });

        // Existing sign out listener
        signOutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SignOut.getInstance().performSignOut(signOutLabel);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                signOutLabel.setForeground(new Color(70, 130, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signOutLabel.setForeground(Color.WHITE);
            }
        });
    }

    private JLabel createMenuLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add hover effect
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(70, 130, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.WHITE);
            }
        });

        return label;
    }
    
    private void openReport() {
        if (parentView != null) {
            parentView.showReportView();
        }
    }


    private void openProfile() {
        if (parentView != null) {
            parentView.showProfileView();
        }
    }

    private int getCurrentUserId() {
        return this.userId;
    }

    public JPanel getMenuPanel() {
        return menuPanel;
    }
}