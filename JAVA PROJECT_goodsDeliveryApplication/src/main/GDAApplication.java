package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import customer.CustomerDashboard;
import driver.DriverView;
import scheduler.SchedulerView;
//import userauthentication.signin;
import userauthentication.welcomePage;

public class GDAApplication {
    private static welcomePage loginWindow = null; // Add this to track the login window

    public static void launchLogin() {
        SwingUtilities.invokeLater(() -> {
            if (loginWindow != null) {
                loginWindow.dispose(); // Close any existing login window
            }
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            loginWindow = new welcomePage();
            loginWindow.setVisible(true);
        });
    }

    public static void launchCustomerDashboard(int customerId) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard(customerId));
    }
    
    public static void launchSchedulerView(int userId) {
        SwingUtilities.invokeLater(() -> {
            SchedulerView schedulerView = new SchedulerView(userId);
            schedulerView.setVisible(true);
        });
    }
    
    public static void launchDriverView(int driverId) {
        SwingUtilities.invokeLater(() -> new DriverView(driverId));
    }

    public static void main(String[] args) {
        // Start the application with the login screen
        launchLogin();
    }
} 