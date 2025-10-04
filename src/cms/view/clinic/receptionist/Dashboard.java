package cms.view.clinic.receptionist;

import cms.model.entities.User;
import cms.view.components.DashboardTemplate;
import cms.view.components.SidebarButton;
import cms.view.login.ClinicLoginView;

import java.awt.*;
import javax.swing.*;

public class Dashboard extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnPatients, btnAppointments, btnBilling, btnLogout;

    private PatientPage patientPanel; // The main panel for this role
    // Other panels will be added later

    // --- State ---
    private final User loggedInReceptionist;

    public Dashboard(User receptionist) {
        this.loggedInReceptionist = receptionist;
        buildDashboard(getClinicName(receptionist) + " - Admin Dashboard");

        // The parent constructor calls the abstract methods to build the dashboard
    }

    @Override
    protected void addSidebarButtons() {
        btnHome = new SidebarButton("🏠 Home");
        btnPatients = new SidebarButton("👥 Patients");
        btnAppointments = new SidebarButton("📅 Appointments");
        btnBilling = new SidebarButton("💳 Billing");
        btnLogout = new SidebarButton("🚪 Logout", true);

        sidebar.add(btnHome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnPatients);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnAppointments);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnBilling);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
    }

    @Override
    protected void addContentPanels() {
        // Create the panels for this dashboard
        patientPanel = new PatientPage(loggedInReceptionist.getClinicId());

        JLabel homePlaceholder = new JLabel("Welcome, " + loggedInReceptionist.getName(), SwingConstants.CENTER);
        JLabel appointmentsPlaceholder = new JLabel("Appointment Scheduling Panel will go here.", SwingConstants.CENTER);
        JLabel billingPlaceholder = new JLabel("Billing Panel will go here.", SwingConstants.CENTER);

        contentPanel.add(homePlaceholder, "Home");
        contentPanel.add(patientPanel, "Patients");
        contentPanel.add(appointmentsPlaceholder, "Appointments");
        contentPanel.add(billingPlaceholder, "Billing");
    }

    @Override
    protected void addListeners() {
        btnHome.addActionListener(_ -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Home");
        });

        btnPatients.addActionListener(_ -> {
            btnPatients.selectInSidebar();
            cardLayout.show(contentPanel, "Patients");

            patientPanel.refreshPatientsList(); // Load patient data
        });

        btnAppointments.addActionListener(_ -> {
            btnAppointments.selectInSidebar();
            cardLayout.show(contentPanel, "Appointments");
        });

        btnBilling.addActionListener(_ -> {
            btnBilling.selectInSidebar();
            cardLayout.show(contentPanel, "Billing");
        });

        btnLogout.addActionListener(_ -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                new ClinicLoginView().setVisible(true);
                dispose();
            }
        });
    }

    @Override
    protected void setDefaultView() {
        btnHome.selectInSidebar();
        cardLayout.show(contentPanel, "Home");
    }

    // Helper to safely get the clinic name for the title
    private static String getClinicName(User user) {
        if (user != null && user.getClinic() != null) {
            return user.getClinic().getClinicName();
        }
        return "Clinic";
    }
}
