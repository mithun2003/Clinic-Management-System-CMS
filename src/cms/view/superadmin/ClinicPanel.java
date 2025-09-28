package cms.view.superadmin;

import cms.model.dao.ClinicDAO;
import cms.model.dao.UserDAO;
import cms.model.entities.Clinic;
import cms.model.entities.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.List;

/**
 * A JPanel for Super Admins to perform CRUD (Create, Read, Update, Delete)
 * operations on clinics. Features a form, a paginated data table, and
 * contextual actions like creating the first admin for a new clinic.
 */
public class ClinicPanel extends JPanel {

    // --- UI Components ---
    private JTextField tfCode, tfName, tfEmail, tfPhone, tfAddress;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnCreateAdmin;
    private JTable table;
    private DefaultTableModel model;

    // --- Pagination State ---
    private int currentPage = 1;
    private final int pageSize = 10; // Show 10 clinics per page
    private int totalPages;
    private JButton btnPrev, btnNext;
    private JLabel lblPage;

    // --- Data Access ---
    private final ClinicDAO clinicDAO;
    private final UserDAO userDAO;

    /**
     * Constructor: Initializes the UI components, layout, and event listeners.
     */
    public ClinicPanel() {
        // Initialize DAOs
        this.clinicDAO = new ClinicDAO();
        this.userDAO = new UserDAO();

        // Setup panel layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Build the UI
        initComponents();
        initListeners();
    }

    /**
     * Initializes and lays out all the UI components on the panel.
     */
    private void initComponents() {
        // --- Top Panel: Contains the form and action buttons ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createFormPanel(), BorderLayout.NORTH);
        topPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        // --- Center Panel: Contains the data table ---
        JScrollPane scrollPane = new JScrollPane(createTable());

        // --- Bottom Panel: Contains pagination controls ---
        JPanel pagerPanel = createPaginationPanel();

        // --- Add all panels to the main layout ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(pagerPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the clinic data entry form.
     *
     * @return A JPanel containing the form fields.
     */
    private JPanel createFormPanel() {
        TitledBorder clinicBorder = BorderFactory.createTitledBorder("Clinic Details");
        clinicBorder.setTitleFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 15, 10)); // Adjusted layout
        formPanel.setBorder(clinicBorder);

        tfCode = new JTextField();
        tfName = new JTextField();
        tfEmail = new JTextField();
        tfPhone = new JTextField();
        tfAddress = new JTextField();
        cbStatus = new JComboBox<>(new String[]{"Active", "Suspended"});

        formPanel.add(new JLabel("Clinic Code:"));
        formPanel.add(tfCode);
        formPanel.add(new JLabel("Clinic Name:"));
        formPanel.add(tfName);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(tfEmail);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(tfPhone);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(tfAddress);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cbStatus);

        return formPanel;
    }

    /**
     * Creates the panel containing the main action buttons.
     *
     * @return A JPanel with the CRUD and admin creation buttons.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Add Clinic");
        btnUpdate = new JButton("Update Clinic");
        btnDelete = new JButton("Delete Clinic");
        btnClear = new JButton("Clear Form");
        btnCreateAdmin = new JButton("Create First Admin");

        btnCreateAdmin.setVisible(false); // Hide by default

        styleButton(btnAdd, new Color(40, 167, 69));      // Green
        styleButton(btnUpdate, new Color(23, 162, 184));  // Blue
        styleButton(btnDelete, new Color(220, 53, 69));   // Red
        styleButton(btnClear, new Color(108, 117, 125));  // Gray
        styleButton(btnCreateAdmin, new Color(255, 193, 7)); // Yellow

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCreateAdmin);

        return buttonPanel;
    }

    /**
     * Creates and styles the JTable for displaying clinic data.
     *
     * @return The configured JTable.
     */
    private JTable createTable() {
        model = new DefaultTableModel(
                new String[]{"ID", "Code", "Name", "Email", "Phone", "Address", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        table = new JTable(model);

        // Style the table
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style the table header
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 102, 102));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        // Center align text in the ID and Code columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // Make ID column smaller

        return table;
    }

    /**
     * Creates the pagination control panel.
     *
     * @return A JPanel with the previous/next buttons and page label.
     */
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

    /**
     * Attaches all event listeners to the UI components.
     */
    private void initListeners() {
        // Pagination listeners
        btnPrev.addActionListener(_ -> {
            if (currentPage > 1) {
                currentPage--;
                loadClinicsPage(currentPage);
            }
        });
        btnNext.addActionListener(_ -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadClinicsPage(currentPage);
            }
        });

        // Button listeners
        btnAdd.addActionListener(_ -> addClinic());
        btnUpdate.addActionListener(_ -> updateClinic());
        btnDelete.addActionListener(_ -> deleteClinic());
        btnClear.addActionListener(_ -> clearForm());
        btnCreateAdmin.addActionListener(_ -> createClinicAdmin());

        // Table row selection listener
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Panel visibility listener to clear the form when hidden
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && !this.isShowing()) {
                clearForm();
            }
        });
    }

    /**
     * Handles the logic when a row is selected in the table. It populates the
     * form and shows/hides the "Create First Admin" button.
     */
    private void handleTableSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Populate form with data from the selected row
            tfCode.setText(valueOrEmpty(model.getValueAt(selectedRow, 1)));
            tfName.setText(valueOrEmpty(model.getValueAt(selectedRow, 2)));
            tfEmail.setText(valueOrEmpty(model.getValueAt(selectedRow, 3)));
            tfPhone.setText(valueOrEmpty(model.getValueAt(selectedRow, 4)));
            tfAddress.setText(valueOrEmpty(model.getValueAt(selectedRow, 5)));
            cbStatus.setSelectedItem(valueOrEmpty(model.getValueAt(selectedRow, 6)));

            // Conditionally show the "Create Admin" button
            int clinicId = (int) model.getValueAt(selectedRow, 0);
            boolean adminExists = clinicDAO.hasAdmin(clinicId);
            btnCreateAdmin.setVisible(!adminExists);
        }
    }

    /**
     * Loads a specific page of clinic data from the database and updates the
     * table.
     *
     * @param page The page number to load.
     */
    private void loadClinicsPage(int page) {
        int totalRecords = clinicDAO.getTotalClinics();
        totalPages = (totalRecords == 0) ? 1 : (int) Math.ceil((double) totalRecords / pageSize);

        model.setRowCount(0); // Clear existing data
        List<Clinic> list = clinicDAO.getClinicsPage(page, pageSize);
        for (Clinic c : list) {
            model.addRow(new Object[]{
                c.getClinicId(), c.getClinicCode(), c.getClinicName(),
                c.getEmail(), c.getPhone(), c.getAddress(), c.getStatus()
            });
        }
        lblPage.setText("Page " + currentPage + " of " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    /**
     * Handles the "Add Clinic" button action. Validates input, calls the DAO,
     * and triggers the "Create First Admin" workflow.
     */
    private void addClinic() {
        if (tfCode.getText().isBlank() || tfName.getText().isBlank() || tfEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Clinic Code, Name, and Email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Clinic c = new Clinic();
        c.setClinicCode(tfCode.getText());
        c.setClinicName(tfName.getText());
        c.setEmail(tfEmail.getText());
        c.setPhone(tfPhone.getText());
        c.setAddress(tfAddress.getText());
        c.setStatus((String) cbStatus.getSelectedItem());

        int newClinicId = clinicDAO.addClinic(c);

        if (newClinicId > 0) {
            CreateAdminDialog adminDialog = new CreateAdminDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            adminDialog.setVisible(true);

            User newAdmin = adminDialog.getNewAdmin();

            if (newAdmin != null) {
                newAdmin.setClinicId(newClinicId);
                userDAO.addUser(newAdmin);
                JOptionPane.showMessageDialog(this, "Clinic and first Admin created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Clinic created, but first Admin was not. You can add one later.", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            refreshClinics(currentPage);
        } else if (newClinicId == -2) {
            JOptionPane.showMessageDialog(this,
                    "A clinic with this Code or Email already exists. Please use a unique value.",
                    "Duplicate Entry Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not create the clinic due to a database error.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the "Update Clinic" action.
     */
    private void updateClinic() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a clinic to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);

        Clinic c = new Clinic();
        c.setClinicId(id);
        c.setClinicCode(tfCode.getText());
        c.setClinicName(tfName.getText());
        c.setEmail(tfEmail.getText());
        c.setPhone(tfPhone.getText());
        c.setAddress(tfAddress.getText());
        c.setStatus(cbStatus.getSelectedItem().toString());

        clinicDAO.updateClinic(c);
        loadClinicsPage(currentPage);
        clearForm();
    }

    /**
     * Handles the "Delete Clinic" action with a confirmation dialog.
     */
    private void deleteClinic() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a clinic to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String code = model.getValueAt(selectedRow, 1).toString();
        String name = model.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you really want to delete clinic: " + name + " (" + code + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            clinicDAO.deleteClinic(id);
            loadClinicsPage(currentPage);
            clearForm();
        }
    }

    /**
     * Handles the "Create First Admin" button action for an existing clinic.
     */
    private void createClinicAdmin() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a clinic first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int clinicId = (int) model.getValueAt(selectedRow, 0);

        // Open your modern CreateAdminDialog
        CreateAdminDialog adminDialog = new CreateAdminDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        adminDialog.setVisible(true);

        User newAdmin = adminDialog.getNewAdmin();

        if (newAdmin != null) {
            newAdmin.setClinicId(clinicId);
            userDAO.addUser(newAdmin);
            JOptionPane.showMessageDialog(this, "Admin for the clinic created successfully!");
            // After creating the admin, hide the button again
            btnCreateAdmin.setVisible(false);
        }
    }

    /**
     * Clears all form fields and the table selection.
     */
    private void clearForm() {
        tfCode.setText("");
        tfName.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfAddress.setText("");
        cbStatus.setSelectedIndex(0);
        table.clearSelection();
        btnCreateAdmin.setVisible(false);
    }

    /**
     * Public method to refresh the panel's data, typically called when the
     * panel is shown.
     */
    public void refreshClinics(int page) {
        currentPage = (page > 1 ? page : 1);
        loadClinicsPage(currentPage);
        clearForm();
    }

    /**
     * Styles a JButton with a solid background color and a hover effect.
     *
     * @param button The button to style.
     * @param color The base color for the button.
     */
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Optional: Add a simple hover effect
        Color darker = color.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darker);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    /**
     * Safely converts an object from the table model to a string, handling
     * nulls.
     *
     * @param val The object value.
     * @return The string representation or an empty string if null.
     */
    private String valueOrEmpty(Object val) {
        return val == null ? "" : val.toString();
    }

}
