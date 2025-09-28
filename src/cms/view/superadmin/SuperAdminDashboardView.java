package cms.view.superadmin;

import cms.model.entities.SuperAdmin;
import cms.view.components.DashboardTemplate; // Import the template
import cms.view.components.SidebarButton;
import cms.view.login.SuperAdminLoginView;

import javax.swing.*;
import java.awt.*;

// 👇 Step 1: Extend the DashboardTemplate instead of JFrame
public class SuperAdminDashboardView extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnManageClinics, btnReports;
    private HomePagePanel homePanel;
    private ClinicPanel clinicPanel;
    private ReportsPanel reportsPanel;

    // --- State ---
    private final SuperAdmin loggedInSuperAdmin;

    public SuperAdminDashboardView(SuperAdmin sa) {
        // Step 1: The parent constructor is called implicitly. It builds the empty shell.

        // Step 2: Initialize THIS class's fields. This is now safe.
        this.loggedInSuperAdmin = sa;

        // Step 3: Now that everything is initialized, call the final build method
        // from the template, passing the now-safe title.
        buildDashboard("Super Admin Dashboard - " + loggedInSuperAdmin.getName());
    }

    // 👇 Step 4: Implement the abstract methods to "fill in the blanks" of the template
    @Override
    protected void addSidebarButtons() {
        // This method is called by the template to add OUR buttons to ITS sidebar.
        btnHome = new SidebarButton("🏠 Home");
        btnManageClinics = new SidebarButton("🏥 Manage Clinics");
        btnReports = new SidebarButton("📊 Reports");
//        btnLogout = new SidebarButton("🚪 Logout", true, SidebarButton.Align.CENTER);

        mainNavPanel.add(btnHome);
        mainNavPanel.add(btnManageClinics);
        mainNavPanel.add(btnReports);

//        sidebar.add(Box.createVerticalGlue());
        // This pushes the logout button to the bottom
//        sidebar.add(btnLogout);
    }

    @Override
    protected void addContentPanels() {
        // This method is called by the template to add OUR panels to ITS content area.
        homePanel = new HomePagePanel(loggedInSuperAdmin);
        clinicPanel = new ClinicPanel();
        reportsPanel = new ReportsPanel();

        contentPanel.add(homePanel, "Home");
        contentPanel.add(clinicPanel, "ManageClinics");
        contentPanel.add(reportsPanel, "Reports");
    }

    @Override
    protected void addListeners() {
        // This method is called by the template to attach OUR listeners.
        btnHome.addActionListener(e -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Home");
            homePanel.refreshData();
        });

        btnManageClinics.addActionListener(e -> {
            btnManageClinics.selectInSidebar();
            cardLayout.show(contentPanel, "ManageClinics");
            clinicPanel.refreshClinics(1);
        });

        btnReports.addActionListener(e -> {
            btnReports.selectInSidebar();
            cardLayout.show(contentPanel, "Reports");
            reportsPanel.loadReportData();
        });

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to log out?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                new SuperAdminLoginView().setVisible(true);
                dispose();
            }
        });
    }

    @Override
    protected void setDefaultView() {
        // This method is called by the template to set the initial view.
        btnHome.selectInSidebar();
        cardLayout.show(contentPanel, "Home");
    }

 
}
