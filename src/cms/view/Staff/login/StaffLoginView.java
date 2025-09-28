package cms.view.login;

import cms.controller.AuthController;
import cms.model.entities.User;

import javax.swing.*;
import java.awt.*;

public class StaffLoginView extends JFrame {

    private JTextField tfClinicCode, tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    private AuthController authController;

    public StaffLoginView() {
        authController = new AuthController();

        setTitle("Clinic Management - Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Clinic Code:"));
        tfClinicCode = new JTextField();
        panel.add(tfClinicCode);

        panel.add(new JLabel("Username:"));
        tfUsername = new JTextField();
        panel.add(tfUsername);

        panel.add(new JLabel("Password:"));
        pfPassword = new JPasswordField();
        panel.add(pfPassword);

        btnLogin = new JButton("Login");
        panel.add(new JLabel()); // blank
        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String clinicCode = tfClinicCode.getText();
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        User user = authController.login(clinicCode, username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Login Successful! Role: " + user.getRole());

            // Route to role-specific dashboard
            switch (user.getRole()) {
                case "ADMIN":
                    // new ClinicAdminDashboardView(user).setVisible(true);
                    break;
                case "DOCTOR":
                    // new DoctorDashboardView(user).setVisible(true);
                    break;
                case "RECEPTIONIST":
                    // new ReceptionistDashboardView(user).setVisible(true);
                    break;
            }

            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid login. Please try again.", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for quick test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffLoginView().setVisible(true));
    }
}