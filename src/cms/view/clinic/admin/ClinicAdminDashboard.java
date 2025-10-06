package cms.view.clinic.admin;

import cms.model.entities.User;
import cms.view.components.DashboardTemplate;
import cms.view.components.SidebarButton;
import cms.view.login.ClinicLoginView;
import javax.swing.*;

public class ClinicAdminDashboard extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnManageStaff, btnSpecializations, btnReports, btnSettings;

    // We'll declare the panels as fields so they can be accessed in listeners
    private HomePage homePanel;
    private StaffPage staffPanel;
    private ReportPage reportPanel;
    private SettingsPage settingsPanel;
    private SpecializationPage specializationPanel;

    // --- State ---
    private final User loggedInAdmin;

    public ClinicAdminDashboard(User admin) {
        this.loggedInAdmin = admin;
        buildDashboard(getClinicName(loggedInAdmin) + " - Admin Dashboard");
    }

    // --- Override the abstract methods to populate the template ---
    @Override
    protected void addSidebarButtons() {
        // This method populates the 'sidebar' panel created by the template.
        btnHome = new SidebarButton("🏠 Home");
        btnManageStaff = new SidebarButton("👥 Manage Staff");
        btnSpecializations = new SidebarButton("✨ Specializations");
        btnReports = new SidebarButton("📊 View Reports");
        btnSettings = new SidebarButton("⚙️ Settings");

        mainNavPanel.add(btnHome);
        mainNavPanel.add(btnManageStaff);
        mainNavPanel.add(btnSpecializations);
        mainNavPanel.add(btnReports);
        mainNavPanel.add(btnSettings);
    }

    @Override
    protected void addContentPanels() {
        // This method populates the 'contentPanel' created by the template.
        // It's now safe to use 'loggedInAdmin' because this runs after the constructor has set it.

        homePanel = new HomePage(loggedInAdmin);
        staffPanel = new StaffPage(loggedInAdmin.getClinicId());
        specializationPanel = new SpecializationPage(loggedInAdmin.getClinicId());
        reportPanel = new ReportPage(loggedInAdmin);
        settingsPanel = new SettingsPage(loggedInAdmin);

        contentPanel.add(homePanel, "Home");
        contentPanel.add(staffPanel, "ManageStaff");
        contentPanel.add(specializationPanel, "Specializations");
        contentPanel.add(reportPanel, "Reports");
        contentPanel.add(settingsPanel, "Settings");
    }

    @Override
    protected void addListeners() {
        // This method attaches listeners to the buttons created above.
        btnHome.addActionListener(_ -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Home");
            homePanel.refreshData();
        });

        btnManageStaff.addActionListener(_ -> {
            btnManageStaff.selectInSidebar();
            cardLayout.show(contentPanel, "ManageStaff");
            staffPanel.refreshStaffList();
        });

         btnSpecializations.addActionListener(_ -> {
            btnSpecializations.selectInSidebar();
            cardLayout.show(contentPanel, "Specializations");
            specializationPanel.refreshList(); // Load the data
        });

        btnReports.addActionListener(_ -> {
            btnReports.selectInSidebar();
            cardLayout.show(contentPanel, "Reports");
            reportPanel.loadReportData();
        });

        btnSettings.addActionListener(_ -> {
            btnSettings.selectInSidebar();
            cardLayout.show(contentPanel, "Settings");
        });

        btnLogout.addActionListener(_ -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                new ClinicLoginView().setVisible(true);
                dispose();
            }
        });
    }

    @Override
    protected void setDefaultView() {
        // This sets the initial view when the dashboard first appears.
        btnHome.selectInSidebar();
        cardLayout.show(contentPanel, "Home");
    }

    // Helper to safely get the clinic name for the title
    private static String getClinicName(User user) {
        if (user != null && user.getClinic() != null && user.getClinic().getClinicName() != null) {
            return user.getClinic().getClinicName();
        }
        return "Clinic";
    }
}
