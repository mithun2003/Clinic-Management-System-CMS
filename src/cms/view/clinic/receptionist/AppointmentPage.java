package cms.view.clinic.receptionist;

import cms.model.dao.AppointmentDAO;
import cms.model.dao.DoctorDAO;
import cms.model.dao.PatientDAO;
import cms.model.entities.Appointment;
import cms.model.entities.Doctor;
import cms.model.entities.Enums;
import cms.model.entities.Patient;
import cms.utils.FontUtils;
import cms.view.components.UIStyler;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AppointmentPage extends JPanel {

    // --- UI Components ---
    private JComboBox<Patient> cbPatients;
    private JComboBox<Doctor> cbDoctors;
    private JDateChooser dcAppointmentDate;
    private JSpinner timeSpinner;
    private JButton btnBookAppointment, btnCancelAppointment;
    private JTable appointmentsTable;
    private DefaultTableModel model;

    // --- State & DAOs ---
    private final int clinicId;
    private final AppointmentDAO appointmentDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;

    public AppointmentPage(int clinicId) {
        this.clinicId = clinicId;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();

        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        initComponents();
        initListeners();
    }

    private void initComponents() {
        add(createBookingForm(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createAppointmentsTable());
        TitledBorder tableBorder = BorderFactory.createTitledBorder("Scheduled Appointments");
        tableBorder.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        scrollPane.setBorder(tableBorder);
        add(scrollPane, BorderLayout.CENTER);

        // Add a panel for actions like "Cancel Appointment"
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCancelAppointment = new JButton("Cancel Selected Appointment");
        actionPanel.add(btnCancelAppointment);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createBookingForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Book New Appointment");
        border.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        formPanel.setBorder(border);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        cbPatients = new JComboBox<>();
        cbDoctors = new JComboBox<>();
        dcAppointmentDate = new JDateChooser(new Date()); // Default to today
        dcAppointmentDate.setDateFormatString("yyyy-MM-dd");

        // Time spinner setup for HH:mm format
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        btnBookAppointment = new JButton("Book Appointment");

        // --- Layout ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Patient:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(cbPatients, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Doctor:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(cbDoctors, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Select Date & Time:"), gbc);
        JPanel dateTimePanel = new JPanel(new BorderLayout(5, 0));
        dateTimePanel.add(dcAppointmentDate, BorderLayout.CENTER);
        dateTimePanel.add(timeSpinner, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(dateTimePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(btnBookAppointment, gbc);

        return formPanel;
    }

    private JTable createAppointmentsTable() {
        model = new DefaultTableModel(
                new String[] { "ID", "Time", "Patient Name", "Doctor Name", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentsTable = new JTable(model);
        UIStyler.styleTable(appointmentsTable);
        UIStyler.setDateTimeColumn(appointmentsTable, 1);
        UIStyler.setStatusColumn(appointmentsTable, 4);
        UIStyler.centerAlignColumns(appointmentsTable, 0);
        UIStyler.setColumnWidths(appointmentsTable, Map.of(0, 40));

        return appointmentsTable;
    }

    private void initListeners() {
        btnBookAppointment.addActionListener(_ -> bookAppointment());
        btnCancelAppointment.addActionListener(_ -> cancelAppointment());

        // Add a listener to the date chooser to refresh the table when the date changes
        dcAppointmentDate.addPropertyChangeListener("date", _ -> refreshAppointmentTable());
    }

    // --- Public methods for data loading and interaction ---

    public void refreshData() {
        // Load lists of patients and doctors for the dropdowns
        List<Patient> patients = patientDAO.getPatientsByClinic(clinicId);
        cbPatients.setModel(new DefaultComboBoxModel<>(patients.toArray(new Patient[0])));

        List<Doctor> doctors = doctorDAO.getActiveDoctorsByClinic(clinicId);
        cbDoctors.setModel(new DefaultComboBoxModel<>(doctors.toArray(new Doctor[0])));

        // Load today's appointments for the table
        dcAppointmentDate.setDate(new Date()); // Ensure it's set to today
        refreshAppointmentTable();
    }

    public void setPatientForBooking(int patientId) {
        // Find the patient in the combo box and select them
        for (int i = 0; i < cbPatients.getItemCount(); i++) {
            if (cbPatients.getItemAt(i).getPatientId() == patientId) {
                cbPatients.setSelectedIndex(i);
                break;
            }
        }
    }

    private void refreshAppointmentTable() {
        Date selectedDate = dcAppointmentDate.getDate();
        if (selectedDate == null) {
            model.setRowCount(0); // Clear table if no date is selected
            return;
        }

        LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDate(clinicId, localDate);

        model.setRowCount(0);
        for (Appointment appt : appointments) {
            model.addRow(new Object[] {
                    appt.getAppointmentId(),
                    appt.getAppointmentDate(),
                    appt.getPatientName(),
                    appt.getDoctorName(),
                    appt.getStatus()
            });
        }
    }

    private void bookAppointment() {
        Patient selectedPatient = (Patient) cbPatients.getSelectedItem();
        Doctor selectedDoctor = (Doctor) cbDoctors.getSelectedItem();
        Date selectedDate = dcAppointmentDate.getDate();
        Date selectedTime = (Date) timeSpinner.getValue();

        if (selectedPatient == null || selectedDoctor == null || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient, doctor, and date.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Combine date and time
        LocalDate datePart = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime timePart = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalDateTime fullDateTime = LocalDateTime.of(datePart, timePart);

        Appointment newAppointment = new Appointment();
        newAppointment.setPatientId(selectedPatient.getPatientId());
        newAppointment.setDoctorId(selectedDoctor.getDoctorId());
        newAppointment.setClinicId(this.clinicId);
        newAppointment.setAppointmentDate(fullDateTime);
        newAppointment.setStatus(Enums.AppointmentStatus.Scheduled);

        if (appointmentDAO.bookAppointment(newAppointment)) {
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshAppointmentTable(); // Refresh the table to show the new appointment
        } else {
            JOptionPane.showMessageDialog(this, "Failed to book the appointment. Please try again.", "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to cancel.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appointmentId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // You'll need an updateAppointmentStatus method in your DAO
            if (appointmentDAO.updateAppointmentStatus(appointmentId, Enums.AppointmentStatus.Cancelled)) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
                refreshAppointmentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel the appointment.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}