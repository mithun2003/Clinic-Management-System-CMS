package cms.view.clinic.admin;

import cms.model.dao.UserDAO;
import cms.model.entities.User;
import cms.utils.FontUtils;
import cms.utils.PasswordUtils;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class SettingsPage extends JPanel {

    private final UserDAO userDAO;
    private final User loggedInAdmin;

    private JPasswordField tfCurrentPassword;
    private JPasswordField pfNewPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnUpdatePassword;

    public SettingsPage(User admin) {
        this.loggedInAdmin = admin;
        this.userDAO = new UserDAO();

        // Use a layout that can center content
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Create a dedicated panel for the form itself ---
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setOpaque(false); // Make it transparent
        // Give the form a maximum size so it doesn't stretch too wide
        formContainer.setPreferredSize(new Dimension(500, 250));
        formContainer.setMaximumSize(new Dimension(600, 300));

        // --- Form Panel with GridLayout ---
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));

        TitledBorder border = BorderFactory.createTitledBorder("Change Your Password");
        border.setTitleFont(FontUtils.getUiFont(Font.BOLD, 18));
        border.setTitleColor(new Color(0, 102, 102));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setOpaque(false);

        // Add labels and fields to the form panel
        formPanel.add(new JLabel("Current Password:"));
        tfCurrentPassword = new JPasswordField();
        formPanel.add(tfCurrentPassword);

        formPanel.add(new JLabel("New Password:"));
        pfNewPassword = new JPasswordField();
        formPanel.add(pfNewPassword);

        formPanel.add(new JLabel("Confirm New Password:"));
        pfConfirmPassword = new JPasswordField();
        formPanel.add(pfConfirmPassword);

        // Add components to the container
        formContainer.add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        btnUpdatePassword = new JButton("Update Password");
        styleButton(btnUpdatePassword);
        btnUpdatePassword.addActionListener(_ -> updatePassword());
        buttonPanel.add(btnUpdatePassword);

        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Add the container to the main panel, which will center it
        add(formContainer);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 102));
        button.setForeground(Color.WHITE);
        button.setFont(FontUtils.getUiFont(Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updatePassword() {
        String current = new String(tfCurrentPassword.getPassword());
        String newPassword = new String(pfNewPassword.getPassword());
        String confirm = new String(pfConfirmPassword.getPassword());

        if (current.isEmpty() || newPassword.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate the current password using the hash stored in the logged-in user object
        if (!PasswordUtils.checkPassword(current, loggedInAdmin.getPassword())) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hash the new password before sending it to the DAO
        String newHashedPassword = PasswordUtils.hashPassword(newPassword);

        if (userDAO.updateUserPassword(loggedInAdmin.getUserId(), newHashedPassword)) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // CRITICAL: Update the in-memory user object with the new hash
            loggedInAdmin.setPassword(newHashedPassword);

            // Clear the form
            tfCurrentPassword.setText("");
            pfNewPassword.setText("");
            pfConfirmPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
