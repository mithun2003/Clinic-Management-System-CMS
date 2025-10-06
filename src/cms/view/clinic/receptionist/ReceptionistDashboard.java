package cms.view.clinic.receptionist;

import cms.model.entities.User;
import cms.view.components.DashboardTemplate;
import cms.view.components.SidebarButton;
import cms.view.login.ClinicLoginView;

import javax.swing.*;

public class ReceptionistDashboard extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnPatients, btnAppointments, btnBilling;

    private HomePage homePanel;
    private PatientPage patientPanel;
    private AppointmentPage appointmentPanel;
    private BillingPage billingPanel;

    // --- State ---
    private final User loggedInReceptionist;

    public ReceptionistDashboard(User receptionist) {
        this.loggedInReceptionist = receptionist;
        buildDashboard(getClinicName(receptionist) + " - Receptionist Dashboard");

        // The parent constructor calls the abstract methods to build the dashboard
    }

    @Override
    protected void addSidebarButtons() {
        btnHome = new SidebarButton("🏠 Home");
        btnPatients = new SidebarButton("👥 Patients");
        btnAppointments = new SidebarButton("📅 Appointments");
        btnBilling = new SidebarButton("💳 Billing");

        mainNavPanel.add(btnHome);
        mainNavPanel.add(btnPatients);
        mainNavPanel.add(btnAppointments);
        mainNavPanel.add(btnBilling);
    }

    @Override
    protected void addContentPanels() {
        // Create the panels for this dashboard
        homePanel = new HomePage(loggedInReceptionist);
        patientPanel = new PatientPage(loggedInReceptionist.getClinicId());
        appointmentPanel = new AppointmentPage(loggedInReceptionist.getClinicId());
        billingPanel = new BillingPage(loggedInReceptionist.getClinicId());
        
        
        contentPanel.add(homePanel, "Home");
        contentPanel.add(patientPanel, "Patients");
        contentPanel.add(appointmentPanel, "Appointments");
        contentPanel.add(billingPanel, "Billing");
    }

    @Override
    protected void addListeners() {
        btnHome.addActionListener(_ -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Home");
            homePanel.refreshData();
        });

        btnPatients.addActionListener(_ -> {
            btnPatients.selectInSidebar();
            cardLayout.show(contentPanel, "Patients");

            patientPanel.refreshPatientsList(); // Load patient data
        });

        btnAppointments.addActionListener(_ -> {
            btnAppointments.selectInSidebar();
            cardLayout.show(contentPanel, "Appointments");
            appointmentPanel.refreshData();
        });

        btnBilling.addActionListener(_ -> {
            btnBilling.selectInSidebar();
            cardLayout.show(contentPanel, "Billing");
            billingPanel.refreshBillList();
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
        System.out.println(user.getName());
        System.out.println(user.getClinic().getClinicId());
        System.out.println(user.getClinic().getClinicName());
        if (user != null && user.getClinic() != null) {
            return user.getClinic().getClinicName();
        }
        return "Clinic";
    }

    /**
     * Switches to the Appointment page and pre-selects a patient for booking.
     * This method is called from the PatientPage.
     * 
     * @param patientId The ID of the patient to be selected.
     */
    public void showAppointmentPanelForPatient(int patientId) {
        btnAppointments.selectInSidebar();
        cardLayout.show(contentPanel, "Appointments");

        appointmentPanel.refreshData();
        appointmentPanel.setPatientForBooking(patientId);
    }
}
