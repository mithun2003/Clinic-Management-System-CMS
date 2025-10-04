package cms.view.clinic.receptionist;

import cms.model.dao.PatientDAO;
import cms.model.entities.Patient;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PatientPage extends JPanel {

    // --- UI Components ---
    private JTextField tfName, tfPhone, tfEmail, tfAddress, tfSearch, tfDob;
    private JComboBox<String> cbGender, cbBloodGroup;
    private JButton btnAdd, btnUpdate, btnClear, btnSearch, btnBookAppointment;
    private JTable table;
    private DefaultTableModel model;

    // --- State & DAO ---
    private final int clinicId;
    private final PatientDAO patientDAO;
    private List<Patient> currentPatientList; // To keep track of the displayed patients

    public PatientPage(int clinicId) {
        this.clinicId = clinicId;
        this.patientDAO = new PatientDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        initListeners();
    }

    private void initComponents() {
        // --- Top: Form for Adding/Updating ---
        add(createFormPanel(), BorderLayout.NORTH);

        // --- Center: Search, Table, and Actions ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(createTable()), BorderLayout.CENTER);
        centerPanel.add(createActionPanel(), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Patient Details");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.setBorder(border);
        // ... (GridBagLayout setup as before) ...
        return formPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        tfSearch = new JTextField();
        btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Find Patient (Name or Phone):"), BorderLayout.WEST);
        searchPanel.add(tfSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        return searchPanel;
    }

    private JTable createTable() {
        model = new DefaultTableModel(new String[]{"ID", "Name", "Gender", "DOB", "Phone"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        // ... (Style the table as before) ...
        return table;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add New Patient");
        btnUpdate = new JButton("Update Selected Patient");
        btnClear = new JButton("Clear Form");
        btnBookAppointment = new JButton("Book Appointment for Selected");

        // Style buttons...

        actionPanel.add(btnAdd);
        actionPanel.add(btnUpdate);
        actionPanel.add(btnClear);
        actionPanel.add(btnBookAppointment);
        return actionPanel;
    }

    private void initListeners() {
        // Listeners for Add, Update, Clear, Search...

        btnBookAppointment.addActionListener(e -> bookAppointmentForSelectedPatient());

        // Table selection listener to populate the form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
    }

    // --- Public method to load data when the panel is shown ---
    public void refreshPatientsList() {
        // Load the initial list of all patients (or recent patients)
        // For simplicity, let's assume we load all for now.
        currentPatientList = patientDAO.getPatientsByClinic(clinicId);
        updateTable(currentPatientList);
    }

    private void updateTable(List<Patient> patients) {
        model.setRowCount(0);
        for (Patient p : patients) {
            model.addRow(new Object[]{p.getPatientId(), p.getName(), p.getGender(), p.getDob(), p.getPhone()});
        }
    }

    private void bookAppointmentForSelectedPatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the list first!", "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the patient ID from the table
        int patientId = (int) model.getValueAt(selectedRow, 0);
        String patientName = (String) model.getValueAt(selectedRow, 1);

        // Here, you would switch to the "Appointments" panel and pass the patient's info.
        // We'll simulate this with a message for now.
        JOptionPane.showMessageDialog(this, "Navigating to Appointment Booking for: " + patientName + " (ID: " + patientId + ")");

        // The actual implementation would involve calling a method on the parent dashboard
        // to switch the card and pass the patient data.
        // e.g., ((ReceptionistDashboard) getParentDashboard()).showAppointmentPanelForPatient(patientId);
    }

    private void populateFormFromSelectedRow() {
        // ... (Logic to fill tfName, tfDob, etc., from the selected table row) ...
    }

    // ... (Other methods for addPatient, updatePatient, etc.) ...
}