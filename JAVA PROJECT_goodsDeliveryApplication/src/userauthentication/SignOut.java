package userauthentication;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SignOut {

    // Make it a singleton to ensure consistent behavior across the application
    private static SignOut instance;

    private SignOut() {}

    public static SignOut getInstance() {
        if (instance == null) {
            instance = new SignOut();
        }
        return instance;
    }

    /**
     * Handles the sign out process
     * @param sourceComponent The component that triggered the sign out
     * @return true if sign out was successful, false if cancelled
     */
    public boolean performSignOut(Component sourceComponent) {
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            sourceComponent,
            "Are you sure you want to sign out?",
            "Sign Out",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Find the top-level window
                Window window = SwingUtilities.getWindowAncestor(sourceComponent);

                // Close the current window
                if (window != null) {
                    window.dispose();
                }

                // Create new signin window
                SwingUtilities.invokeLater(() -> {
                    new signin().setVisible(true);
                });
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    sourceComponent,
                    "Error during sign out: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }

        return false;
    }
}