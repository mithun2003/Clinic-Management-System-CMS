package cms.view.clinic.doctor;

import cms.model.dao.ReportDAO;
import cms.model.entities.Doctor;
import cms.view.components.UIStyler;
import cms.view.components.UIStyler.StatCardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePage extends JPanel {

    private final Doctor loggedInDoctor;
    private final int clinicId;
    private final ReportDAO reportDAO;

    // Stat cards
    private StatCardPanel todaysAppointmentsCard;
    private StatCardPanel pendingAppointmentsCard;

    public HomePage(Doctor doctor) {
        this.loggedInDoctor = doctor;
        this.clinicId = doctor.getUser().getClinicId();
        this.reportDAO = new ReportDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- Welcome Header ---
        String clinicNameColor = "#006666"; // A slightly darker, richer teal
        String labelText = "<html>Welcome, <font color='" + clinicNameColor + "'>" + loggedInDoctor.getName()
                + "</font></html>";
        JLabel welcomeLabel = new JLabel(labelText, JLabel.LEFT);
        welcomeLabel.setFont(FontUtils.getUiFont(Font.BOLD, 28));

        // === Stats Cards Panel ===
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setPreferredSize(new Dimension(710, 140));

        todaysAppointmentsCard = new StatCardPanel("Today's Appointments", "📅");
        pendingAppointmentsCard = new UIStyler.StatCardPanel("Patients Waiting", "🧍");

        statsPanel.add(todaysAppointmentsCard);
        statsPanel.add(pendingAppointmentsCard);

        // Use a wrapper panel to center the stats cards
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(statsPanel);

        // === Add Components to Main Panel ===
        add(welcomeLabel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            todaysAppointmentsCard.setValue(String.valueOf(
                    reportDAO.getTodaysAppointmentCountForDoctor(clinicId, this.loggedInDoctor.getDoctorId())));

            pendingAppointmentsCard.setValue(String.valueOf(reportDAO.getPendingPatientCountForToday(clinicId)));

        });
    }

}