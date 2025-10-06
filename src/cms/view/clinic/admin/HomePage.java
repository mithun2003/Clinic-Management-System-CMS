package cms.view.clinic.admin; // Assuming you are organizing by role

import cms.model.dao.ReportDAO;
import cms.model.dao.UserDAO;
import cms.model.entities.User;
import cms.utils.FontUtils;
import cms.view.components.UIStyler.StatCardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePage extends JPanel {

    private final User loggedInAdmin;
    private final int clinicId;

    // DAOs
    private final ReportDAO reportDAO;
    private final UserDAO userDAO;

    // Stat cards for this dashboard
    private final StatCardPanel totalPatientsCard, todaysAppointmentsCard, totalStaffCard;

    public HomePage(User admin) {
        this.loggedInAdmin = admin;
        this.clinicId = admin.getClinicId(); // Get the ID of the current clinic
        this.reportDAO = new ReportDAO();
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // === Welcome Header ===
        String clinicName = (loggedInAdmin.getClinic() != null) ? loggedInAdmin.getClinic().getClinicName()
                : "Your Clinic";

        // Construct the HTML string
        String clinicNameColor = "#006666"; // A slightly darker, richer teal
        String labelText = "<html>Dashboard for <font color='" + clinicNameColor + "'>" + clinicName + "</font></html>";

        JLabel welcomeLabel = new JLabel(labelText, JLabel.LEFT);
        welcomeLabel.setFont(FontUtils.getUiFont(Font.BOLD, 28));
        // welcomeLabel.setForeground(new Color(0, 0, 0));

        // === Stats Cards Panel ===
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setPreferredSize(new Dimension(710, 140));

        totalPatientsCard = new StatCardPanel("Total Patients", "👥");
        todaysAppointmentsCard = new StatCardPanel("Today's Appointments", "📅");
        totalStaffCard = new StatCardPanel("Total Staff Members", "🧑‍⚕️");

        statsPanel.add(totalPatientsCard);
        statsPanel.add(todaysAppointmentsCard);
        statsPanel.add(totalStaffCard);

        // Use a wrapper panel to center the stats cards
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(statsPanel);

        // === Add Components to Main Panel ===
        add(welcomeLabel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        // Load data when the panel is created
        refreshData();
    }

    // This method can be called to refresh the stats
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            totalPatientsCard.setValue(String.valueOf(reportDAO.getPatientCountForClinic(clinicId)));
            todaysAppointmentsCard.setValue(String.valueOf(reportDAO.getTodaysAppointmentCountForClinic(clinicId)));
            totalStaffCard.setValue(String.valueOf(userDAO.getUserCountByClinic(clinicId, true)));
        });
    }

}
