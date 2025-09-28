package cms.view.superadmin;

import cms.controller.SuperAdminAuthController;
import cms.model.entities.SuperAdmin;
import cms.utils.PlaceholderTextField;
import cms.utils.TitleBarManager;
import javax.swing.*;
import java.awt.*;

public class SuperAdminLoginView extends JFrame {

    private PlaceholderTextField usernameField;
    private PlaceholderTextField passwordField;
    private JButton btnLogin;
    private SuperAdminAuthController controller;

    // For dragging the undecorated frame
    private int mouseX, mouseY;

    public SuperAdminLoginView() {
        controller = new SuperAdminAuthController();

        // --- Frame Setup ---
        setUndecorated(true); // Remove default title bar
        setSize(1000, 665);
        // For a login screen, dispose() is better so it doesn't close the whole app
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 👇 JUST ONE LINE TO CREATE THE TITLE BAR
        JPanel titleBar = TitleBarManager.createTitleBar(this, "Super Admin Login");
        add(titleBar, BorderLayout.NORTH);

        // --- Main Content Panel (to center the login form) ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        // --- Login Form Panel ---
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setPreferredSize(new Dimension(350, 250));
        loginFormPanel.setOpaque(false); // Make it transparent to see mainPanel's color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.weightx = 1.0; 

        // Username Label and Field
        usernameField = new PlaceholderTextField("Enter Username", "👤");
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginFormPanel.add(usernameField, gbc);

        // --- Password Field with Emoji Icon ---
        passwordField = new PlaceholderTextField("Enter Password", "🔒", true); // true for password
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginFormPanel.add(passwordField, gbc);

        // Login Button
        btnLogin = new JButton("Login");
        styleLoginButton(btnLogin);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make button span both columns
//        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFormPanel.add(btnLogin, gbc);

        mainPanel.add(loginFormPanel); // Add the form to the centering panel

        // --- Action Listener ---
        btnLogin.addActionListener(e -> doLogin());
    }

    private void styleLoginButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 102)); // Teal color
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 128, 128)); // Lighter teal on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 102)); // Back to normal
            }
        });
    }

    // --- Login Logic (unchanged) ---
    private void doLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        SuperAdmin sa = controller.login(username, password);
        if (sa != null) {
//            JOptionPane.showMessageDialog(this, "Super Admin Login Successful!");
            new SuperAdminDashboardView(sa).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Super Admin login credentials!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Main Method for Testing (unchanged) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuperAdminLoginView().setVisible(true));
    }
}
