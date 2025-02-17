package scheduler.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import com.toedter.calendar.JDateChooser;


import utils.MissionDocumentGenerator;


public class GenerateMissionDocumentDialog extends JDialog {
    private JDateChooser dateChooser;
    private JButton generateButton;


    public GenerateMissionDocumentDialog(Frame parent) {
        super(parent, "Generate Mission Document", true);
        initializeComponents();
    }


    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));


        // Create date picker using JCalendar
        dateChooser = new JDateChooser();


        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);


        // Add date picker
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Select Date:"), gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dateChooser, gbc);


        // Add generate button
        generateButton = new JButton("Generate Document");
        generateButton.addActionListener(e -> generateDocument());


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(generateButton, gbc);


        add(mainPanel, BorderLayout.CENTER);


        // Set dialog properties
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }


    private void generateDocument() {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a date",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("missions_" + new SimpleDateFormat("yyyy-MM-dd").format(selectedDate) + ".docx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String outputPath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!outputPath.endsWith(".docx")) {
                    outputPath += ".docx";
                }

                MissionDocumentGenerator.generateMissionDocument(selectedDate, outputPath);

                int choice = JOptionPane.showConfirmDialog(this,
                    "Document generated successfully! Would you like to open it?",
                    "Success",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    openDocument(outputPath);
                }

                dispose();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error generating document: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void openDocument(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Desktop not supported. Please open the file manually from:\n" + filePath,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error opening document: " + e.getMessage() + "\nFile location: " + filePath,
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    // Add this inner class
    private class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);


        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parse(text);
        }


        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                return dateFormatter.format(value);
            }
            return "";
        }
    }
}
