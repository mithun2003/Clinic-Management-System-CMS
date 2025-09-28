package cms.view.superadmin;

import cms.model.entities.User;
import cms.utils.PasswordUtils;
import cms.utils.TitleBarManager;
import cms.view.components.PlaceholderTextField;
import javax.swing.*;
import java.awt.*;

public class CreateAdminDialog extends JDialog {

    private final PlaceholderTextField nameField, usernameField, passwordField;
    private User newAdmin = null; // This will hold the result

    public CreateAdminDialog(JFrame parent) {
        super(parent, "Create First Clinic Admin", true); // true for modal

        setUndecorated(true);
        setSize(450, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- Custom Title Bar ---
        CreateAdminDialog cad = this;
        JPanel titleBar = TitleBarManager.createTitleBar(cad, "Create First Clinic Admin");
        add(titleBar, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        // Fields
        nameField = new PlaceholderTextField("Admin Full Name", "👤");
        usernameField = new PlaceholderTextField("Admin Username", "📧");
        passwordField = new PlaceholderTextField("Password", "🔒", true);

        gbc.gridy = 0;
        formPanel.add(nameField, gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnCreate = new JButton("Create Admin");
        JButton btnCancel = new JButton("Cancel");

        // Style button
        btnCreate.setBackground(new Color(0, 102, 102));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnCreate);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        btnCreate.addActionListener(_ -> createAdmin());
        btnCancel.addActionListener(_ -> dispose());
    }

    private void createAdmin() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hash the password
        String hashedPassword = PasswordUtils.hashPassword(password);

        // Create the user object to return
        newAdmin = new User();
        newAdmin.setName(name);
        newAdmin.setUsername(username);
        newAdmin.setPassword(hashedPassword); // Store the HASH
        newAdmin.setRole("ADMIN");

        dispose(); // Close the dialog
    }

    // This method allows the calling panel to get the result
    public User getNewAdmin() {
        return newAdmin;
    }
}