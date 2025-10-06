package cms.view.clinic.doctor;

import cms.model.dao.AppointmentDAO;
import cms.model.dao.BillingDAO;
import cms.model.dao.PatientDAO;
import cms.model.entities.Appointment;
import cms.model.entities.Doctor;
import cms.model.entities.Enums;
import cms.model.entities.Patient;
import cms.utils.FontUtils;
import cms.view.components.UIStyler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConsultationPage extends JPanel {

    // --- UI Components ---
    private JTable appointmentsTable;
    private DefaultTableModel model;
    private JTextArea taConsultationNotes, taAllergies;
    private JButton btnCompleteConsultation;
    private JLabel lblSelectedPatient, lblPatientInfo;

    // --- Pagination ---
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages;
    private JButton btnPrev, btnNext;
    private JLabel lblPage;

    // --- State & DAOs ---
    private final Doctor loggedInDoctor;
    private final AppointmentDAO appointmentDAO;
    private final BillingDAO billingDAO;
    private final PatientDAO patientDAO;
    private Appointment selectedAppointment = null; // Store the full selected appointment
    private List<Appointment> todaysAppointments; // Cache the list for the day

    public ConsultationPage(Doctor doctor) {
        this.loggedInDoctor = doctor;
        this.appointmentDAO = new AppointmentDAO();
        this.billingDAO = new BillingDAO();
        this.patientDAO = new PatientDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        initComponents();
        initListeners();
    }

    private void initComponents() {

        // --- Top Panel: Consultation Area ---
        JPanel consultationPanel = createConsultationPanel();

        // --- Center Panel: Today's Appointment Queue ---
        JScrollPane tableScrollPane = new JScrollPane(createAppointmentsTable());
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Today's Patient Queue"));
        // // --- Split Pane to hold both ---
        // // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        // // tableScrollPane, consultationPanel);
        // // splitPane.setDividerLocation(500);
        // JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        // consultationPanel, tableScrollPane);
        // splitPane.setDividerLocation(300);
        // splitPane.setEnabled(false); // Disable dragging/resizing
        // splitPane.setDividerSize(0); // Hides the draggable line completely
        // splitPane.setContinuousLayout(false);
        // splitPane.setOneTouchExpandable(false);

        // --- Bottom Panel: Pagination ---
        JPanel pagerPanel = createPaginationPanel();

        add(consultationPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(pagerPanel, BorderLayout.SOUTH);
    }

    private JTable createAppointmentsTable() {
        // ID column is hidden but used to track selection
        model = new DefaultTableModel(
                new String[] { "ID", "Patient ID", "Time", "Patient Name", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentsTable = new JTable(model);

        UIStyler.styleTable(appointmentsTable);

        // Hide the ID column (index 0)
        // appointmentsTable.getColumnModel().getColumn(0).setMinWidth(0);
        // appointmentsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        // appointmentsTable.getColumnModel().getColumn(0).setWidth(0);
        UIStyler.hideColumn(appointmentsTable, 0, 1);

        // Apply special renderers to the visible columns
        UIStyler.setTimeOnlyColumn(appointmentsTable, 2); // Time is now at index 1
        UIStyler.setStatusColumn(appointmentsTable, 4); // Status is now at index 3

        return appointmentsTable;
    }

    private JPanel createConsultationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        TitledBorder border = BorderFactory.createTitledBorder("Consultation Details");
        border.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        panel.setBorder(border);

        // --- Top Panel for Patient Info ---
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        lblSelectedPatient = new JLabel("Please select a patient from the queue.");
        lblSelectedPatient.setFont(FontUtils.getUiFont(Font.BOLD, 20));
        lblPatientInfo = new JLabel("DOB: | Gender: ");
        lblPatientInfo.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        lblPatientInfo.setVisible(false);
        infoPanel.add(lblSelectedPatient);
        infoPanel.add(lblPatientInfo);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        taConsultationNotes = new JTextArea(8, 20);
        taConsultationNotes.setLineWrap(true);
        taConsultationNotes.setWrapStyleWord(true);
        taConsultationNotes.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        JScrollPane notesScrollPane = new JScrollPane(taConsultationNotes);
        notesScrollPane.setBorder(BorderFactory.createTitledBorder("Consultation & Prescription Notes"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formPanel.add(notesScrollPane, gbc);


        taAllergies = new JTextArea(8, 20);
        taAllergies.setEditable(false);
        taAllergies.setForeground(Color.RED);
        taAllergies.setLineWrap(true);
        taAllergies.setWrapStyleWord(true);
        taAllergies.setFont(FontUtils.getUiFont(Font.BOLD, 14));
        JScrollPane allergiesPanel = new JScrollPane(taAllergies);
        allergiesPanel.setBorder(BorderFactory.createTitledBorder("Known Allergies (Read-Only)"));
        // allergiesPanel.add(taAllergies, BorderLayout.CENTER);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formPanel.add(allergiesPanel, gbc);

        // // --- Notes Section (Main editable area) ---
        // taConsultationNotes = new JTextArea();
        // taConsultationNotes.setLineWrap(true);
        // taConsultationNotes.setWrapStyleWord(true);
        // taConsultationNotes.setFont(FontUtils.getUiFont(Font.PLAIN, 14));

        // JScrollPane notesScrollPane = new JScrollPane(taConsultationNotes);
        // notesScrollPane.setBorder(BorderFactory.createTitledBorder("Consultation & Prescription Notes"));

        // // --- Allergies Panel (No scrollbar, smaller fixed width) ---
        // taAllergies = new JTextArea(8, 20);
        // taAllergies.setEditable(false);
        // taAllergies.setForeground(Color.RED);
        // taAllergies.setFont(FontUtils.getUiFont(Font.BOLD, 14));
        // taAllergies.setLineWrap(true);
        // taAllergies.setWrapStyleWord(true);

        // JPanel allergiesPanel = new JPanel(new BorderLayout());
        // allergiesPanel.setBorder(BorderFactory.createTitledBorder("Known Allergies (Read-Only)"));
        // allergiesPanel.add(taAllergies, BorderLayout.CENTER);

        // // --- Fixed Split Pane (70% Notes, 30% Allergies) ---
        // JSplitPane notesAndAllergiesPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, notesScrollPane, allergiesPanel);
        // notesAndAllergiesPane.setDividerLocation(0.7); // 70% width for notes
        // notesAndAllergiesPane.setResizeWeight(0.7);
        // notesAndAllergiesPane.setEnabled(false);
        // notesAndAllergiesPane.setDividerSize(0);
        // notesAndAllergiesPane.setContinuousLayout(false);
        // notesAndAllergiesPane.setOneTouchExpandable(false);

        // --- Button ---
        btnCompleteConsultation = new JButton("Mark as Completed & Generate Bill");
        btnCompleteConsultation.setEnabled(false);

        // --- Add components to panel ---
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        // panel.add(notesAndAllergiesPane, BorderLayout.CENTER);
        panel.add(btnCompleteConsultation, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel pagerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrev = new JButton("⬅ Previous");
        btnNext = new JButton("Next ➡");
        lblPage = new JLabel("Page 1 of 1");
        pagerPanel.add(btnPrev);
        pagerPanel.add(lblPage);
        pagerPanel.add(btnNext);
        return pagerPanel;
    }

    private void initListeners() {
        btnCompleteConsultation.addActionListener(_ -> completeConsultation());
        // Pagination listeners...
        btnPrev.addActionListener(_ -> {
            if (currentPage > 1) {
                currentPage--;
                loadAppointmentPage(currentPage);
            }
        });
        btnNext.addActionListener(_ -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadAppointmentPage(currentPage);
            }
        });

        appointmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

    }

    private void handleTableSelection() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int appointmentId = (int) model.getValueAt(selectedRow, 0);
            int patientId = (int) model.getValueAt(selectedRow, 1);

            // Find the full appointment object from the cached list
            this.selectedAppointment = todaysAppointments.stream()
                    .filter(appt -> appt.getAppointmentId() == appointmentId)
                    .findFirst()
                    .orElse(null);

            if (this.selectedAppointment != null) {
                Patient patient = patientDAO.getPatientById(patientId);

                if (patient != null) {
                    lblSelectedPatient.setText("Patient: " + patient.getName());
                    lblPatientInfo.setText("DOB: " + patient.getDob() + " | Gender: " + patient.getGender()
                            + " | Blood Group: " + patient.getBloodGroup());
                    lblPatientInfo.setVisible(true);
                    taAllergies
                            .setText(patient.getAllergies() != null ? patient.getAllergies() : "No known allergies.");
                }

                taConsultationNotes.setText(selectedAppointment.getNotes());
                // Only enable the button if the appointment is still 'Scheduled'
                boolean isCompleted = (selectedAppointment.getStatus() == Enums.AppointmentStatus.Completed);
                taConsultationNotes.setEditable(!isCompleted);
                btnCompleteConsultation.setEnabled(!isCompleted);
            }
        }
    }

    public void refreshData() {
        currentPage = 1;
        loadAppointmentPage(currentPage);
        clearConsultationPanel();
    }

    private void loadAppointmentPage(int page) {
        int totalRecords = appointmentDAO.getTodaysAppointmentCountForDoctor(loggedInDoctor.getDoctorId());
        totalPages = (totalRecords == 0) ? 1 : (int) Math.ceil((double) totalRecords / pageSize);

        model.setRowCount(0);
        todaysAppointments = appointmentDAO.getTodaysAppointmentsForDoctor(loggedInDoctor.getDoctorId(), page,
                pageSize);

        model.setRowCount(0);
        for (Appointment appt : todaysAppointments) {
            model.addRow(new Object[] {
                    appt.getAppointmentId(),
                    appt.getPatientId(),
                    appt.getAppointmentDate(), // The renderer will format this to time only
                    appt.getPatientName(),
                    appt.getStatus()
            });
        }
        lblPage.setText("Page " + currentPage + " of " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void completeConsultation() {
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the queue first.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the notes from the text area
        String notes = taConsultationNotes.getText();

        // DAO call to update appointment status and save notes
        boolean statusUpdated = appointmentDAO.completeAppointment(selectedAppointment.getAppointmentId(), notes);

        if (statusUpdated) {
            // Get doctor's fee from the in-memory Doctor object
            double fee = this.loggedInDoctor.getConsultationFee();

            // Create the bill
            boolean billCreated = billingDAO.createBillForAppointment(selectedAppointment.getAppointmentId(), fee);

            if (billCreated) {
                JOptionPane.showMessageDialog(this, "Consultation completed. A bill has been sent to the front desk.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData(); // Refresh the table to show the updated "Completed" status
            } else {
                JOptionPane.showMessageDialog(this,
                        "Appointment completed, but there was an error generating the bill.", "Billing Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update the appointment status.", "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearConsultationPanel() {
        selectedAppointment = null;
        lblSelectedPatient.setText("Please select a patient from the queue.");
        lblPatientInfo.setText("Age: | Gender: | Blood Group:"); // Reset info label
        taConsultationNotes.setText("");
        taAllergies.setText("");
        btnCompleteConsultation.setEnabled(false);
        taConsultationNotes.setEditable(true);
        appointmentsTable.clearSelection();
    }
}