package cms.view.clinic.admin;

import cms.model.dao.DoctorDAO;
import cms.model.dao.SpecializationDAO;
import cms.model.dao.UserDAO;
import cms.model.entities.Doctor;
import cms.model.entities.Enums;
import cms.model.entities.User;
import cms.utils.FontUtils;
import cms.utils.PasswordUtils;
import cms.view.components.StatusRenderer;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StaffPage extends JPanel {

    // --- UI Components ---
    private JTextField tfName, tfUsername;
    private JPasswordField pfPassword;
    private JComboBox<Enums.Role> cbRole;
    private JComboBox<Enums.Status> cbStatus;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnEditDoctorDetails;
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
    private final UserDAO userDAO;
    private final DoctorDAO doctorDAO;

    public StaffPage(int clinicId) {
        this.clinicId = clinicId;
        this.userDAO = new UserDAO();
        this.doctorDAO = new DoctorDAO(); // Initialize DoctorDAO

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        initListeners();
    }

    private void initComponents() {
        // --- Top Panel: Form and Buttons ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 20));
        topPanel.add(createFormPanel(), BorderLayout.NORTH);
        topPanel.add(createButtonPanel(), BorderLayout.CENTER);

        // --- Center Panel: Data Table ---
        JScrollPane scrollPane = new JScrollPane(createTable());

        // --- Bottom Panel: Pagination ---
        JPanel pagerPanel = createPaginationPanel();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(pagerPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        // Using GridBagLayout for better control over conditional fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Staff Member Details");
        border.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        formPanel.setBorder(border);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Initialize components
        tfName = new JTextField();
        tfUsername = new JTextField();
        pfPassword = new JPasswordField();
        cbRole = new JComboBox<>(new Enums.Role[] { Enums.Role.DOCTOR, Enums.Role.RECEPTIONIST });
        cbStatus = new JComboBox<>(new Enums.Status[] { Enums.Status.Active, Enums.Status.Suspended });

        // --- Layout using GridBagLayout ---
        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(tfName, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        formPanel.add(tfUsername, gbc);
        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password (for new user):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(pfPassword, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(cbRole, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(cbStatus, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Add Staff");
        btnUpdate = new JButton("Update Staff");
        btnDelete = new JButton("Delete Staff");
        btnClear = new JButton("Clear Form");
        btnEditDoctorDetails = new JButton("Edit Doctor Details");
        btnEditDoctorDetails.setVisible(false);

        styleButton(btnAdd, new Color(40, 167, 69)); // Green
        styleButton(btnUpdate, new Color(23, 162, 184)); // Blue
        styleButton(btnDelete, new Color(220, 53, 69)); // Red
        styleButton(btnClear, new Color(108, 117, 125)); // Gray
        styleButton(btnEditDoctorDetails, new Color(0, 102, 102));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnEditDoctorDetails);

        return buttonPanel;
    }

    private JTable createTable() {
        model = new DefaultTableModel(new String[] { "ID", "Name", "Username", "Role", "Specialization", "Status" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style the table header
        table.getTableHeader().setFont(FontUtils.getUiFont(Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 102, 102));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        // Center align text in the ID and Code columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // Make ID column smaller

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
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

    private void initListeners() {
        btnAdd.addActionListener(_ -> addStaffMember());
        btnUpdate.addActionListener(_ -> updateStaffMember());
        btnDelete.addActionListener(_ -> deleteStaffMember());
        btnClear.addActionListener(_ -> clearForm());
        btnEditDoctorDetails.addActionListener(_ -> editDoctorDetails());

        // Pagination listeners...
        btnPrev.addActionListener(_ -> {
            if (currentPage > 1) {
                currentPage--;
                loadStaffPage(currentPage);
            }
        });
        btnNext.addActionListener(_ -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadStaffPage(currentPage);
            }
        });

        // Table selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                tfName.setText(model.getValueAt(selectedRow, 1).toString());
                tfUsername.setText(model.getValueAt(selectedRow, 2).toString());
                cbRole.setSelectedItem(model.getValueAt(selectedRow, 3));
                cbStatus.setSelectedItem(model.getValueAt(selectedRow, 5));

                pfPassword.setText(""); // Clear password field on selection
                pfPassword.setEnabled(false); // Disable password field for updates

                // Conditionally show the "Edit Doctor Details" button
                Enums.Role selectedRole = (Enums.Role) model.getValueAt(selectedRow, 3);
                btnEditDoctorDetails.setVisible(selectedRole == Enums.Role.DOCTOR);
            }
        });

    }

    // This is the public method called by the dashboard to load/reload data
    public void refreshStaffList() {
        currentPage = 1;
        loadStaffPage(currentPage);
        clearForm();
    }

    private void loadStaffPage(int page) {
        int totalRecords = userDAO.getUserCountByClinic(clinicId, true);
        totalPages = (totalRecords == 0) ? 1 : (int) Math.ceil((double) totalRecords / pageSize);

        model.setRowCount(0);
        List<User> staffList = userDAO.getPaginatedUsersByClinicId(clinicId, page, pageSize);
        for (User user : staffList) {
            model.addRow(new Object[] {
                    user.getUserId(),
                    user.getName(),
                    user.getUsername(),
                    user.getRole(),
                    user.getSpecialization(),
                    user.getStatus()
            });
        }

        lblPage.setText("Page " + currentPage + " of " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void addStaffMember() {
        String name = tfName.getText();
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Name, Username, and Password are required for a new staff member.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User();
        newUser.setClinicId(this.clinicId);
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(PasswordUtils.hashPassword(password));
        newUser.setRole((Enums.Role) cbRole.getSelectedItem());
        newUser.setStatus((Enums.Status) cbStatus.getSelectedItem());

        int newUserId = userDAO.addUser(newUser);

        if (newUserId > 0) { // Success!
            // If the new user is a doctor, save their specialization
            if (newUser.getRole() == Enums.Role.DOCTOR) {
                editDoctorDetails(newUserId, true);
            }
            JOptionPane.showMessageDialog(this, "Staff member '" + name + "' was added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshStaffList(); // This also clears the form

        } else if (newUserId == -1) { // Generic database error
            JOptionPane.showMessageDialog(this, "A database error occurred. Please check the logs.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);

        } else { // Specifically, this means a duplicate was found (or other specific error
                 // codes)
            JOptionPane.showMessageDialog(this,
                    "Failed to add staff member. The username '" + username + "' may already exist in this clinic.",
                    "Duplicate Username", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editDoctorDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor from the table.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int userId = (int) model.getValueAt(selectedRow, 0);
        editDoctorDetails(userId, false); // Call helper for existing doctor
    }

    // Helper method for both adding and editing doctor details
    private void editDoctorDetails(int userId, boolean isNewDoctor) {
        DoctorDetailsDialog dialog = new DoctorDetailsDialog((Frame) SwingUtilities.getWindowAncestor(this), userId,
                this.clinicId);
        dialog.setVisible(true);

        Doctor doctorDetails = dialog.getResult();
        if (doctorDetails != null) {
            boolean success;
            if (isNewDoctor || !doctorDAO.doctorExists(userId)) {
                success = doctorDAO.addDoctorDetails(doctorDetails);
            } else {
                success = doctorDAO.updateDoctorDetails(doctorDetails);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Doctor details saved successfully!");
                refreshStaffList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save doctor details.", "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the action of the "Update Details" button.
     * Updates the common profile information (Name, Username, Role) for the
     * selected user.
     * Doctor-specific details are handled by the "Edit Doctor Details" button.
     */
    private void updateStaffMember() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member from the table to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Get Data ---
        int userId = (int) model.getValueAt(selectedRow, 0);
        Enums.Role originalRole = (Enums.Role) model.getValueAt(selectedRow, 3);
        Enums.Role newRole = (Enums.Role) cbRole.getSelectedItem();
        String newName = tfName.getText().trim();
        String newUsername = tfUsername.getText().trim();

        // --- Validation ---
        if (newName.isEmpty() || newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Username cannot be empty.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Create User Object for Update ---
        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        userToUpdate.setName(newName);
        userToUpdate.setUsername(newUsername);
        userToUpdate.setRole(newRole);

        // --- Call DAO and Handle Role Change ---
        if (userDAO.updateUser(userToUpdate)) {
            // --- IMPORTANT: Handle the case where a user is no longer a doctor ---
            // If their role was changed FROM Doctor TO something else,
            // we should deactivate their doctor profile.
            if (originalRole == Enums.Role.DOCTOR && newRole != Enums.Role.DOCTOR) {
                doctorDAO.setDoctorStatusInactive(userId);
                JOptionPane.showMessageDialog(this, "User role changed. Their doctor profile has been deactivated.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Staff details updated successfully!");
            }

            refreshStaffList(); // This also clears the form
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update staff details. The username might already be taken.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaffMember() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this staff member? This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "Staff member deleted successfully!");
                refreshStaffList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete staff member.", "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfUsername.setText("");
        pfPassword.setText("");
        cbRole.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        table.clearSelection();
        pfPassword.setEnabled(true); // Re-enable password field
        btnEditDoctorDetails.setVisible(false);
    }

    /**
     * Styles a JButton with a solid background color and a hover effect.
     *
     * @param button The button to style.
     * @param color  The base color for the button.
     */
    private void styleButton(JButton button, Color color) {
        button.setFont(FontUtils.getEmojiFont(Font.BOLD, 14));
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

}
