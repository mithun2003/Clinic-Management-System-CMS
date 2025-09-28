package cms.view.clinic.admin; // Or your correct package

import cms.model.entities.User;
import cms.view.components.DashboardTemplate;
import cms.view.components.SidebarButton;
import cms.view.login.StaffLoginView;

import javax.swing.*;
import java.awt.*;

public class ClinicAdminDashboard extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnManageStaff, btnReports;
    // We'll declare the panels as fields so they can be accessed in listeners
    // private ClinicHomePagePanel homePanel; // A home page specific to the clinic admin
    // private StaffPanel staffPanel;

    // --- State ---
    private final User loggedInAdmin;

    public ClinicAdminDashboard(User admin) {
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
//        btnLogout = new SidebarButton("🚪 Logout", true);

        mainNavPanel.add(btnHome);
        mainNavPanel.add(btnManageStaff);
        mainNavPanel.add(btnReports);
    }

    @Override
    protected void addContentPanels() {
        // This method populates the 'contentPanel' created by the template.
        // It's now safe to use 'loggedInAdmin' because this runs after the constructor has set it.

        // homePanel = new ClinicHomePagePanel(loggedInAdmin);
        // staffPanel = new StaffPanel(loggedInAdmin.getClinicId());
        // Using placeholders for now
        JLabel homePlaceholder = new JLabel("Clinic Admin Home Page - Welcome, " + loggedInAdmin.getName(), SwingConstants.CENTER);
        JLabel staffPlaceholder = new JLabel("Staff Management Panel will go here.", SwingConstants.CENTER);
        JLabel reportsPlaceholder = new JLabel("Clinic Reports Panel will go here.", SwingConstants.CENTER);

        contentPanel.add(homePlaceholder, "Home");
        contentPanel.add(staffPlaceholder, "ManageStaff");
        contentPanel.add(reportsPlaceholder, "Reports");
    }

    @Override
    protected void addListeners() {
        // This method attaches listeners to the buttons created above.
        btnHome.addActionListener(e -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Home");
            // homePanel.refreshData();
        });

        btnManageStaff.addActionListener(e -> {
            btnManageStaff.selectInSidebar();
            cardLayout.show(contentPanel, "ManageStaff");
            // staffPanel.refreshStaffList();
        });

        btnReports.addActionListener(e -> {
            btnReports.selectInSidebar();
            cardLayout.show(contentPanel, "Reports");
            // reportsPanel.refreshData();
        });

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                new StaffLoginView().setVisible(true);
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
