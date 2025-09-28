package cms.view.login;

import cms.controller.AuthController;
import cms.model.entities.User;
import cms.utils.TitleBarManager;
import cms.view.components.PlaceholderTextField;
import cms.view.clinic.admin.ClinicAdminDashboard;
//import cms.view.admin.ClinicAdminDashboardView;
// import cms.view.doctor.DoctorDashboardView;
// import cms.view.receptionist.ReceptionistDashboardView;

import javax.swing.*;
import java.awt.*;

public class StaffLoginView extends JFrame {

    // --- UI Components ---
    private PlaceholderTextField clinicCodeField;
    private PlaceholderTextField usernameField;
    private PlaceholderTextField passwordField;
    private JButton btnLogin;

    // --- Controller ---
    private final AuthController controller;

    /**
     * Constructor: Orchestrates the initialization of the frame, components,
     * and listeners.
     */
    public StaffLoginView() {
        this.controller = new AuthController();

        initializeFrame();
        createAndPlaceComponents();
        initListeners();
    }

    /**
     * Sets up the main properties of the JFrame window.
     */
    private void initializeFrame() {
        setUndecorated(true);
        setSize(1000, 665);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * Creates all UI components and arranges them in the frame's layout.
     */
    private void createAndPlaceComponents() {
        // --- Custom Title Bar ---
        JPanel titleBar = TitleBarManager.createTitleBar(this, "Clinic Staff Login");
        add(titleBar, BorderLayout.NORTH);

        // --- Main Content Panel (for centering) ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        // --- Login Form Panel ---
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setPreferredSize(new Dimension(350, 300));
        loginFormPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);

        // --- Input Fields ---
        this.clinicCodeField = new PlaceholderTextField("Enter Clinic Code", "🏥");
        gbc.gridy = 0;
        loginFormPanel.add(clinicCodeField, gbc);

        this.usernameField = new PlaceholderTextField("Enter Username", "👤");
        gbc.gridy = 1;
        loginFormPanel.add(usernameField, gbc);

        this.passwordField = new PlaceholderTextField("Enter Password", "🔒", true);
        gbc.gridy = 2;
        loginFormPanel.add(passwordField, gbc);

        // --- Login Button ---
        this.btnLogin = new JButton("Login");
        styleLoginButton(btnLogin);
        gbc.gridy = 3;
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
    private void initListeners() {
        btnLogin.addActionListener(e -> doLogin());
    }

    /**
     * Applies a modern, consistent style to the login button.
     *
     * @param button The JButton to be styled.
     */
    private void styleLoginButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 102));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 128, 128));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 102));
            }
        });
    }

    /**
     * Handles the login logic and routes the user to the correct dashboard.
     */
    private void doLogin() {
        String clinicCode = clinicCodeField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (clinicCode.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = controller.login(clinicCode, username, password);

        if (user != null) {
            // Route to the correct dashboard based on the user's role
            switch (user.getRole()) {
                case "ADMIN":
                    new ClinicAdminDashboard(user).setVisible(true);
//                    JOptionPane.showMessageDialog(this, "Dashboard coming soon!");
                    break;
                case "DOCTOR":
                    // Replace with: new DoctorDashboardView(user).setVisible(true);
                    JOptionPane.showMessageDialog(this, "Doctor Dashboard is under construction.");
                    break;
                case "RECEPTIONIST":
                    // Replace with: new ReceptionistDashboardView(user).setVisible(true);
                    JOptionPane.showMessageDialog(this, "Receptionist Dashboard is under construction.");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Login successful, but no dashboard is available for your role.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose(); // Close the login window
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or the clinic is inactive. Please try again.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
