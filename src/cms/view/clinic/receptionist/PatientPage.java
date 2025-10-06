package cms.view.clinic.receptionist;

import cms.model.dao.PatientDAO;
import cms.model.entities.Patient;
import cms.utils.FontUtils;
import cms.view.components.UIStyler;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PatientPage extends JPanel {

    // --- UI Components ---
    private JTextField tfName, tfPhone, tfSearch;
    private JComboBox<String> cbGender, cbBloodGroup;
    private JDateChooser dcDob; // Using a proper date chooser
    private JTextArea taAllergies, taAddress;
    private JButton btnAdd, btnUpdate, btnClear, btnSearch, btnBookAppointment, btnShowAll;
    private JTable table;
    private DefaultTableModel model;

    // --- Pagination ---
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages;
    private JButton btnPrev, btnNext;
    private JLabel lblPage;

    // --- State & DAO ---
    private final int clinicId;
    private final PatientDAO patientDAO;
    private Integer selectedPatientId = null;

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
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(createFormPanel(), BorderLayout.NORTH);
        topPanel.add(createButtonPanel(), BorderLayout.CENTER);

        // --- Center: Search and Table ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(createTable()), BorderLayout.CENTER);
        centerPanel.add(createPaginationPanel(), BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Patient Details");
        border.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        formPanel.setBorder(border);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.weightx = 1.0;

        // Initialize components
        tfName = new JTextField();
        dcDob = new JDateChooser();
        dcDob.setDateFormatString("yyyy-MM-dd");
        cbGender = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        tfPhone = new JTextField();

        taAddress = new JTextArea(3, 20); // 3 rows, 20 columns
        taAddress.setLineWrap(true);
        taAddress.setWrapStyleWord(true); // Prevents breaking words in half

        cbBloodGroup = new JComboBox<>(new String[] { "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-" });

        // Create the allergies text area
        taAllergies = new JTextArea(3, 20);
        taAllergies.setLineWrap(true);
        taAllergies.setWrapStyleWord(true);

        // --- Layout ---
        // Row 0: Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name*:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formPanel.add(tfName, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Row 1: DOB and Gender
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(dcDob, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Gender*:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(cbGender, gbc);

        // Row 2: Phone and Blood Group
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(tfPhone, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        formPanel.add(cbBloodGroup, gbc);

        // Row 3: Address and Allergies
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(new JScrollPane(taAddress), gbc);
        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Allergies:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 3;
        formPanel.add(new JScrollPane(taAllergies), gbc);

        return formPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        tfSearch = new JTextField();
        btnSearch = new JButton("Search");
        btnShowAll = new JButton("Show All");

        JPanel buttonContainer = new JPanel();
        buttonContainer.add(btnSearch);
        buttonContainer.add(btnShowAll);

        searchPanel.add(new JLabel("Find Patient (Name or Phone):"), BorderLayout.WEST);
        searchPanel.add(tfSearch, BorderLayout.CENTER);
        searchPanel.add(buttonContainer, BorderLayout.EAST);

        btnShowAll.addActionListener(_ -> refreshPatientsList());
        return searchPanel;
    }

    private JTable createTable() {
        model = new DefaultTableModel(new String[] { "ID", "Name", "Gender", "DOB", "Phone" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        UIStyler.styleTable(table);
        UIStyler.centerAlignColumns(table, 0,1);
        UIStyler.setColumnWidths(table, Map.of(0, 40));

        // table.setRowHeight(30);
        // table.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        // table.setForeground(Color.DARK_GRAY);
        // table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // // Style the table header
        // table.getTableHeader().setFont(FontUtils.getUiFont(Font.BOLD, 14));
        // table.getTableHeader().setBackground(new Color(0, 102, 102));
        // table.getTableHeader().setForeground(Color.WHITE);
        // table.getTableHeader().setReorderingAllowed(false);

        // DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        // centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        // table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        return table;
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

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnAdd = new JButton("Add New Patient");
        btnUpdate = new JButton("Update Selected Patient");
        btnClear = new JButton("Clear Form");
        btnBookAppointment = new JButton("Book Appointment for Selected");

        UIStyler.styleButton(btnAdd, new Color(40, 167, 69)); // Green
        UIStyler.styleButton(btnUpdate, new Color(23, 162, 184)); // Blue
        UIStyler.styleButton(btnClear, new Color(108, 117, 125)); // Gray
        UIStyler.styleButton(btnBookAppointment, new Color(0, 102, 102));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBookAppointment);
        return buttonPanel;
    }

    private void initListeners() {
        btnAdd.addActionListener(_ -> addPatient());
        btnUpdate.addActionListener(_ -> updatePatient());
        btnClear.addActionListener(_ -> clearForm());
        btnSearch.addActionListener(_ -> searchPatients());
        btnBookAppointment.addActionListener(_ -> bookAppointmentForSelectedPatient());
        btnShowAll.addActionListener(_ -> refreshPatientsList());

        btnPrev.addActionListener(_ -> {
            if (currentPage > 1) {
                currentPage--;
                loadPatientsPage(currentPage);
            }
        });
        btnNext.addActionListener(_ -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadPatientsPage(currentPage);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });
    }

    public void refreshPatientsList() {
        currentPage = 1;
        tfSearch.setText("");
        loadPatientsPage(currentPage);
        clearForm();
    }

    private void loadPatientsPage(int page) {
        int totalRecords = patientDAO.getTotalPatientsByClinic(clinicId);
        totalPages = (totalRecords == 0) ? 1 : (int) Math.ceil((double) totalRecords / pageSize);

        model.setRowCount(0);
        List<Patient> patientList = patientDAO.getPaginatedPatientsByClinic(clinicId, page, pageSize);
        for (Patient p : patientList) {
            model.addRow(new Object[] { p.getPatientId(), p.getName(), p.getGender(), p.getDob(), p.getPhone() });
        }

        lblPage.setText("Page " + currentPage + " of " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void searchPatients() {
        String searchTerm = tfSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshPatientsList();
            return;
        }
        // For search, we might not use pagination, or we'd need a more complex DAO
        // For now, let's just show all search results on one page
        List<Patient> searchResults = patientDAO.searchPatients(clinicId, searchTerm);
        model.setRowCount(0);
        for (Patient p : searchResults) {
            model.addRow(new Object[] { p.getPatientId(), p.getName(), p.getGender(), p.getDob(), p.getPhone() });
        }
        // Disable pagination controls during search
        lblPage.setText("Search Results");
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            selectedPatientId = (Integer) model.getValueAt(selectedRow, 0);
            Patient p = patientDAO.getPatientById(selectedPatientId);
            if (p != null) {
                tfName.setText(p.getName());
                if (p.getDob() != null) {
                    dcDob.setDate(Date.from(p.getDob().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } else {
                    dcDob.setDate(null);
                }
                cbGender.setSelectedItem(p.getGender());
                tfPhone.setText(p.getPhone());
                taAddress.setText(p.getAddress());
                cbBloodGroup.setSelectedItem(p.getBloodGroup());
                taAllergies.setText(p.getAllergies());
            }
        }
    }

    private void addPatient() {
        if (tfName.getText().isBlank() || cbGender.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Patient Name and Gender are required fields.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient p = createPatientFromForm();

        if (patientDAO.addPatient(p)) {
            JOptionPane.showMessageDialog(this, "Patient registered successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshPatientsList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register patient. The phone number may already exist.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        if (selectedPatientId == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the table to update.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient p = createPatientFromForm();
        p.setPatientId(selectedPatientId);

        if (patientDAO.updatePatient(p)) {
            JOptionPane.showMessageDialog(this, "Patient details updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshPatientsList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update patient details.", "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // A helper method to create a patient from the form to reduce duplication
    private Patient createPatientFromForm() {
        Patient p = new Patient();
        p.setClinicId(this.clinicId);
        p.setName(tfName.getText().trim());
        if (dcDob.getDate() != null) {
            p.setDob(dcDob.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        p.setGender((String) cbGender.getSelectedItem());
        p.setPhone(tfPhone.getText().trim());
        p.setAddress(taAddress.getText().trim());
        p.setBloodGroup((String) cbBloodGroup.getSelectedItem());
        p.setAllergies(taAllergies.getText());
        return p;
    }

    private void clearForm() {
        tfName.setText("");
        dcDob.setDate(null);
        cbGender.setSelectedIndex(0);
        tfPhone.setText("");
        taAddress.setText("");
        cbBloodGroup.setSelectedIndex(0);
        taAllergies.setText("");
        tfSearch.setText("");
        selectedPatientId = null;
        table.clearSelection();
    }

    private void bookAppointmentForSelectedPatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the list first!", "No Patient Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int patientId = (int) model.getValueAt(selectedRow, 0);

        ReceptionistDashboard parentDashboard = (ReceptionistDashboard) SwingUtilities.getWindowAncestor(this);
        if (parentDashboard != null) {
            parentDashboard.showAppointmentPanelForPatient(patientId);
        }
    }

}