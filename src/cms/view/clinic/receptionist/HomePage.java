package cms.view.clinic.receptionist;

import cms.model.dao.ReportDAO;
import cms.model.entities.User;
import cms.utils.FontUtils;
import cms.view.components.UIStyler.StatCardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePage extends JPanel {

    private final User loggedInUser;
    private final int clinicId;
    private final ReportDAO reportDAO;

    // Stat cards
    private StatCardPanel todaysAppointmentsCard;
    private StatCardPanel newPatientsTodayCard;
    private StatCardPanel pendingPatientsCard;
    private StatCardPanel unpaidBillsCard;

    public HomePage(User user) {
        this.loggedInUser = user;
        this.clinicId = user.getClinicId();
        this.reportDAO = new ReportDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- Welcome Header ---
        String clinicNameColor = "#006666"; // A slightly darker, richer teal
        String labelText = "<html>Welcome, <font color='" + clinicNameColor + "'>" + loggedInUser.getName() + "</font></html>";
        JLabel welcomeLabel = new JLabel(labelText, JLabel.LEFT);
        welcomeLabel.setFont(FontUtils.getUiFont(Font.BOLD, 28));

        // --- Stats Cards Panel ---
        // JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        statsPanel.setBackground(Color.WHITE);
        // statsPanel.setPreferredSize(new Dimension(800, 150));

        todaysAppointmentsCard = new StatCardPanel("Today's Appointments", "📅");
        newPatientsTodayCard = new StatCardPanel("New Patients Today", "⭐");
        pendingPatientsCard = new StatCardPanel("Pending Patients", "🧍");
        unpaidBillsCard = new StatCardPanel("Pending Bills", "💳");

        statsPanel.add(todaysAppointmentsCard);
        statsPanel.add(newPatientsTodayCard);
        statsPanel.add(pendingPatientsCard);
        statsPanel.add(unpaidBillsCard);

        JPanel verticalCenterWrapper = new JPanel();
        verticalCenterWrapper.setLayout(new BoxLayout(verticalCenterWrapper, BoxLayout.Y_AXIS));
        verticalCenterWrapper.setBackground(Color.WHITE);

        // 1. Add top glue
        verticalCenterWrapper.add(Box.createVerticalGlue());
        // 2. Add the panel with the stat cards
        verticalCenterWrapper.add(statsPanel);
        // 3. Add bottom glue
        verticalCenterWrapper.add(Box.createVerticalGlue());

        // --- Add components to the main layout ---
        add(welcomeLabel, BorderLayout.NORTH);
        // Add the wrapper to the center, which will handle the vertical alignment
        add(verticalCenterWrapper, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            todaysAppointmentsCard.setValue(String.valueOf(reportDAO.getTodaysAppointmentCountForClinic(clinicId)));

            newPatientsTodayCard.setValue(String.valueOf(reportDAO.getNewPatientCountForToday(clinicId)));

            pendingPatientsCard.setValue(String.valueOf(reportDAO.getPendingPatientCountForToday(clinicId)));

            unpaidBillsCard.setValue(String.valueOf(reportDAO.getUnpaidBillCount(clinicId)));
        });
    }

}