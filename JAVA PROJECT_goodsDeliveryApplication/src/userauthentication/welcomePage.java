package userauthentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class welcomePage extends JFrame implements ActionListener {
    private final JLabel appName = new JLabel("ezzyDelivery");
    private JButton signin, signup;
    private JPanel mainPanel, centeringPanel, buttonPanel;

    public welcomePage() {
        initializeComponents();
    }

    private void initializeComponents() {
        this.setLayout(new BorderLayout(0, 20));
        this.setTitle("Welcome Page");

        // Create main panel to serve as a container
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 255));  // Light background for contrast

        // Style the app name label (no longer creating it)
        appName.setFont(new Font("Arial", Font.BOLD, 28));
        appName.setForeground(new Color(75, 0, 130));
        appName.setHorizontalAlignment(SwingConstants.CENTER);
        appName.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        // Create the centering panel with fixed size
        centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setPreferredSize(new Dimension(350, 400));
        centeringPanel.setBackground(new Color(147, 112, 219));  // Purple background
        centeringPanel.setBorder(BorderFactory.createLineBorder(new Color(75, 0, 130), 2));

        // Create and style the button panel
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);  // Make it transparent to show parent's background

        // Create and style the buttons
        signin = createStyledButton("Sign In");
        signup = createStyledButton("Sign Up");

        // Add action listeners
        signin.addActionListener(this);
        signup.addActionListener(this);

        // Add buttons to button panel with proper spacing
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.insets = new Insets(0, 10, 0, 10);  // Add spacing between buttons
        buttonGbc.ipadx = 20;  // Add internal padding
        buttonGbc.ipady = 10;

        buttonPanel.add(signin, buttonGbc);
        buttonPanel.add(signup, buttonGbc);

        // Add button panel to centering panel
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.insets = new Insets(20, 20, 20, 20);  // Add padding around the buttons
        centeringPanel.add(buttonPanel, centerGbc);

        // Add centering panel to main panel
        mainPanel.add(centeringPanel, new GridBagConstraints());

        // Add components to frame
        this.add(appName, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);

        // Set up the frame
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(600, 650);
        this.setLocationRelativeTo(null);  // Center the window on screen
        this.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(75, 0, 130));  // Dark purple
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(106, 90, 205));  // Lighter purple on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(75, 0, 130));  // Back to original color
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signin) {
            signin signinWindow = new signin();
            signinWindow.setVisible(true);
            dispose();
        }

        if (e.getSource() == signup) {
            signup signupWindow = new signup();
            signupWindow.setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new welcomePage());
    }
}
