package cms.view.clinic.admin; // Or your correct package

import cms.model.entities.User;
import cms.view.components.DashboardTemplate;
import cms.view.components.SidebarButton;
import cms.view.login.ClinicLoginView;
import javax.swing.*;

public class Dashboard extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnManageStaff, btnReports, btnSettings;

    // We'll declare the panels as fields so they can be accessed in listeners
    private HomePage homePanel; // A home page specific to the clinic admin
    private StaffPage staffPanel;
    private ReportPage reportPanel;
    private SettingsPage settingsPanel;

    // --- State ---
    private final User loggedInAdmin;

    public Dashboard(User admin) {
        // Step 1: The parent constructor is called implicitly first.
        // It builds the empty JFrame shell with an empty sidebar and content panel.

        // Step 2: Initialize THIS class's fields. This is now safe.
        this.loggedInAdmin = admin;

        // Step 3: Now that all fields are initialized, call the final build method
        // from the template, passing the now-safe title.
        buildDashboard(getClinicName(loggedInAdmin) + " - Admin Dashboard");
    }

    // --- Override the abstract methods to populate the template ---
    @Override
    protected void addSidebarButtons() {
        // This method populates the 'sidebar' panel created by the template.
        btnHome = new SidebarButton("🏠 Home");
        btnManageStaff = new SidebarButton("👥 Manage Staff");
        btnReports = new SidebarButton("📊 View Reports");
        btnSettings = new SidebarButton("⚙️ Settings");

        mainNavPanel.add(btnHome);
        mainNavPanel.add(btnManageStaff);
        mainNavPanel.add(btnReports);
        mainNavPanel.add(btnSettings);
    }

    @Override
    protected void addContentPanels() {
        // This method populates the 'contentPanel' created by the template.
        // It's now safe to use 'loggedInAdmin' because this runs after the constructor has set it.

        homePanel = new HomePage(loggedInAdmin);
        staffPanel = new StaffPage(loggedInAdmin.getClinicId());
        reportPanel = new ReportPage(loggedInAdmin);
        settingsPanel = new SettingsPage(loggedInAdmin);

        contentPanel.add(homePanel, "Home");
        contentPanel.add(staffPanel, "ManageStaff");
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
        // This is a fallback. To make it work reliably, your login DAO
        // must fetch and set the Clinic object within the User object.
        return "Clinic";
    }
}
