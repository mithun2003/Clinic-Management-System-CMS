package cms.view.login;

import cms.controller.SuperAdminAuthController;
import cms.model.entities.SuperAdmin;
import cms.utils.TitleBarManager;
import cms.view.components.PlaceholderTextField;
import cms.view.superadmin.SuperAdminDashboardView;

import javax.swing.*;
import java.awt.*;

public class SuperAdminLoginView extends JFrame {

    // --- UI Components ---
    private PlaceholderTextField usernameField;
    private PlaceholderTextField passwordField;
    private JButton btnLogin;

    // --- Controller ---
    private final SuperAdminAuthController controller;

    /**
     * Constructor for the Super Admin Login window.
     * It orchestrates the initialization of the frame, components, and listeners.
     */
    public SuperAdminLoginView() {
        this.controller = new SuperAdminAuthController();
        
        initializeFrame();
        createAndPlaceComponents();
        setupEventListeners();
    }

    /**
     * Sets up the main properties of the JFrame window.
     */
    private void initializeFrame() {
        setUndecorated(true);
        setSize(1024, 665);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    /**
     * Creates all UI components and arranges them in the frame's layout.
     */
    private void createAndPlaceComponents() {
        // --- Reusable Custom Title Bar ---
        JPanel titleBar = TitleBarManager.createTitleBar(this, "Super Admin Login");
        add(titleBar, BorderLayout.NORTH);

        // --- Main Content Panel (for centering) ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        // --- Login Form Panel ---
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setPreferredSize(new Dimension(350, 250));
        loginFormPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);

        // --- Username and Password Fields ---
        this.usernameField = new PlaceholderTextField("Enter Username", "👤");
        gbc.gridy = 0;
        loginFormPanel.add(usernameField, gbc);

        this.passwordField = new PlaceholderTextField("Enter Password", "🔒", true);
        gbc.gridy = 1;
        loginFormPanel.add(passwordField, gbc);

        // --- Login Button ---
        this.btnLogin = new JButton("Login");
        styleLoginButton(btnLogin);
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);
        loginFormPanel.add(btnLogin, gbc);

        // Add the final form to the centering panel
        mainPanel.add(loginFormPanel);
    }

    /**
     * Attaches all event listeners for this view.
     */
    private void setupEventListeners() {
        btnLogin.addActionListener(_ -> doLogin());
    }

    /**
     * Applies a modern, consistent style to the login button.
     * @param button The JButton to be styled.
     */
    private void styleLoginButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 102));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 128, 128));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 102));
            }
        });
    }

    /**
     * Handles the login logic when the login button is clicked.
     */
    private void doLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SuperAdmin sa = controller.login(username, password);

        if (sa != null) {
            new SuperAdminDashboardView(sa).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

}