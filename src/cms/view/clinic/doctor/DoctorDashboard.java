package cms.view.clinic.doctor;

import cms.model.dao.DoctorDAO;
import cms.model.entities.Doctor;
import cms.model.entities.User;
import cms.view.components.DashboardTemplate;
import cms.view.components.SidebarButton;
import cms.view.login.ClinicLoginView;

import javax.swing.*;

public class DoctorDashboard extends DashboardTemplate {

    // --- Components specific to this dashboard ---
    private SidebarButton btnHome, btnConsultations, btnSchedule;

    private HomePage homePanel;
    private ConsultationPage consultationPanel;
    private FullSchedulePage fullSchedulePanel;

    // --- State ---
    private final Doctor loggedInDoctor;

    public DoctorDashboard(User user) {
        this.loggedInDoctor = new DoctorDAO().getDoctorByUserId(user.getUserId());
        this.loggedInDoctor.setUser(user);
        buildDashboard(getClinicName(user) + " - Doctor Dashboard");

    }

    @Override
    protected void addSidebarButtons() {
        btnHome = new SidebarButton("🏠 Home");
        btnConsultations = new SidebarButton("🩺 Consultations");
        btnSchedule = new SidebarButton("📅 Full Schedule");

        mainNavPanel.add(btnHome);
        mainNavPanel.add(btnConsultations);
        mainNavPanel.add(btnSchedule);
    }

    @Override
    protected void addContentPanels() {
        homePanel = new HomePage(loggedInDoctor);
        consultationPanel = new ConsultationPage(loggedInDoctor);
        fullSchedulePanel = new FullSchedulePage(loggedInDoctor);

        contentPanel.add(homePanel, "Home");
        contentPanel.add(consultationPanel, "Consultations");
        contentPanel.add(fullSchedulePanel, "Schedule");
    }

    @Override
    protected void addListeners() {
        btnHome.addActionListener(_ -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Home");
            homePanel.refreshData();
        });

        btnConsultations.addActionListener(_ -> {
            btnHome.selectInSidebar();
            cardLayout.show(contentPanel, "Consultations");
            consultationPanel.refreshData();
        });

        btnSchedule.addActionListener(_ -> {
            btnSchedule.selectInSidebar();
            cardLayout.show(contentPanel, "Schedule");
            fullSchedulePanel.refreshSchedule();
        });

        btnLogout.addActionListener(_ -> {
            int choice = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to log out?", "Confirm Logout",
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
        homePanel.refreshData(); // Load data on startup
    }

    // Helper to safely get the clinic name for the title
    private static String getClinicName(User user) {
        if (user != null && user.getClinic() != null) {
            return user.getClinic().getClinicName();
        }
        return "Clinic";
    }
}