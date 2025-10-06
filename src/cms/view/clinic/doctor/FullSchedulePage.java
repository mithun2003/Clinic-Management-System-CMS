package cms.view.clinic.doctor;

import cms.model.dao.AppointmentDAO;
import cms.model.entities.Appointment;
import cms.model.entities.Doctor;
import cms.utils.FontUtils;
import cms.view.components.UIStyler;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullSchedulePage extends JPanel {

    // --- UI Components ---
    private JDateChooser dcDateSelector;
    private JTable scheduleTable;
    private DefaultTableModel model;

    // --- State & DAO ---
    private final Doctor loggedInDoctor;
    private final AppointmentDAO appointmentDAO;
    private List<Appointment> currentAppointments = new ArrayList<>();

    public FullSchedulePage(Doctor doctor) {
        this.loggedInDoctor = doctor;
        this.appointmentDAO = new AppointmentDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
        initListeners();
        refreshSchedule(); // Ensure table is populated on load
    }

    private void initComponents() {
        // --- Top Panel: Date Selector ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Select a Date to View Schedule"));

        dcDateSelector = new JDateChooser(new Date()); // Default to today
        dcDateSelector.setDateFormatString("yyyy-MM-dd");
        dcDateSelector.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        dcDateSelector.setPreferredSize(new Dimension(200, 30));

        topPanel.add(new JLabel("View schedule for:"));
        topPanel.add(dcDateSelector);

        // --- Center Panel: Schedule Table ---
        JScrollPane scrollPane = new JScrollPane(createScheduleTable());
        TitledBorder tableBorder = BorderFactory.createTitledBorder("Appointments for Selected Date");
        tableBorder.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        scrollPane.setBorder(tableBorder);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JTable createScheduleTable() {
        model = new DefaultTableModel(
                new String[] { "ID", "Time", "Patient Name", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(model);

        UIStyler.styleTable(scheduleTable);

        // Hide the ID column
        UIStyler.hideColumn(scheduleTable, 0);

        // Apply special renderers
        UIStyler.setTimeOnlyColumn(scheduleTable, 1);
        UIStyler.setStatusColumn(scheduleTable, 3);

        return scheduleTable;
    }

    private void initListeners() {
        // When the user selects a new date, refresh the table
        dcDateSelector.addPropertyChangeListener("date", evt -> {
            if ("date".equals(evt.getPropertyName())) {
                refreshSchedule();
            }
        });

    }

    /**
     * Public method to load/refresh the data for the selected date.
     * This will be called when the panel is first shown and when the date changes.
     */
    public void refreshSchedule() {
        if (loggedInDoctor == null) {
            return; // Don't do anything if the doctor profile isn't loaded
        }

        Date selectedDate = dcDateSelector.getDate();
        if (selectedDate == null) {
            model.setRowCount(0); // Clear the table if no date is selected
            return;
        }

        // Convert java.util.Date to java.time.LocalDate
        LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Fetch the appointments for this doctor on the selected date
        List<Appointment> appointments = appointmentDAO.getAppointmentsForDoctorByDate(loggedInDoctor.getDoctorId(),
                localDate);

        model.setRowCount(0); // Clear existing rows
        for (Appointment appt : appointments) {
            model.addRow(new Object[] {
                    appt.getAppointmentId(),
                    appt.getAppointmentDate(), // The renderer will format this to time only
                    appt.getPatientName(),
                    appt.getStatus()
            });
        }
    }

    /**
     * Finds the selected appointment and displays its notes in a dialog.
     */
    private void showNotesForSelectedAppointment() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow < 0)
            return; // Should not happen with the listener guard, but safe to have

        // Get the appointment ID from the hidden column in the table model
        int appointmentId = (int) model.getValueAt(selectedRow, 0);

        // Find the full Appointment object from our cached list
        Appointment selectedAppointment = currentAppointments.stream()
                .filter(appt -> appt.getAppointmentId() == appointmentId)
                .findFirst()
                .orElse(null);

        if (selectedAppointment != null) {
            String notes = selectedAppointment.getNotes();
            String patientName = selectedAppointment.getPatientName();

            // Use a JTextArea inside a JScrollPane for better formatting of long notes
            JTextArea notesArea = new JTextArea(notes);
            notesArea.setWrapStyleWord(true);
            notesArea.setLineWrap(true);
            notesArea.setEditable(false);
            notesArea.setFont(FontUtils.getUiFont(Font.PLAIN, 14));

            JScrollPane scrollPane = new JScrollPane(notesArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));

            // Show the notes in a JOptionPane
            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "Consultation Notes for " + patientName,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}