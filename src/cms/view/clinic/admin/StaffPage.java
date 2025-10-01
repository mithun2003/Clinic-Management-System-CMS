package cms.view.clinic.admin;

import cms.model.dao.UserDAO;
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
    private JComboBox<User.Role> cbRole;
    private JComboBox<User.Status> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
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

    public StaffPage(int clinicId) {
        this.clinicId = clinicId;
        this.userDAO = new UserDAO();

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
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 15, 10));
        TitledBorder border = BorderFactory.createTitledBorder("Staff Member Details");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.setBorder(border);

        tfName = new JTextField();
        tfUsername = new JTextField();
        pfPassword = new JPasswordField();
        cbRole = new JComboBox<>(new User.Role[]{ User.Role.DOCTOR, User.Role.RECEPTIONIST }); // Admins can't create other Admins for now
        cbStatus = new JComboBox<>(User.Status.values());

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(tfName);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(tfUsername);
        formPanel.add(new JLabel("Password (for new user):"));
        formPanel.add(pfPassword);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(cbRole);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cbStatus);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Add Staff");
        btnUpdate = new JButton("Update Staff");
        btnDelete = new JButton("Delete Staff");
        btnClear = new JButton("Clear Form");

        styleButton(btnAdd, new Color(40, 167, 69));      // Green
        styleButton(btnUpdate, new Color(23, 162, 184));  // Blue
        styleButton(btnDelete, new Color(220, 53, 69));   // Red
        styleButton(btnClear, new Color(108, 117, 125));  // Gray

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        return buttonPanel;
    }

    private JTable createTable() {
        model = new DefaultTableModel(new String[]{ "ID", "Name", "Username", "Role", "Status" }, 0) {
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

        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
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
                cbStatus.setSelectedItem(model.getValueAt(selectedRow, 4));
                pfPassword.setText(""); // Clear password field on selection
                pfPassword.setEnabled(false); // Disable password field for updates
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
        totalPages = ( totalRecords == 0 ) ? 1 : (int) Math.ceil((double) totalRecords / pageSize);

        model.setRowCount(0);
        List<User> staffList = userDAO.getPaginatedUsersByClinicId(clinicId, page, pageSize);
        for (User user : staffList) {
            model.addRow(new Object[]{ user.getUserId(), user.getName(), user.getUsername(), user.getRole(), user.getStatus() });
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
            JOptionPane.showMessageDialog(this, "Name, Username, and Password are required for a new staff member.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User();
        newUser.setClinicId(this.clinicId);
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(PasswordUtils.hashPassword(password));
        newUser.setRole((User.Role) cbRole.getSelectedItem());
        newUser.setStatus((User.Status) cbStatus.getSelectedItem());

        if (userDAO.addUser(newUser)) {
            JOptionPane.showMessageDialog(this, "Staff member added successfully!");
            refreshStaffList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add staff member. The username may already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaffMember() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) model.getValueAt(selectedRow, 0);
        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        userToUpdate.setName(tfName.getText());
        userToUpdate.setUsername(tfUsername.getText());
        userToUpdate.setRole((User.Role) cbRole.getSelectedItem());
        userToUpdate.setStatus((User.Status) cbStatus.getSelectedItem());

        if (userDAO.updateUser(userToUpdate)) {
            JOptionPane.showMessageDialog(this, "Staff details updated successfully!");
            refreshStaffList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update staff details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaffMember() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Failed to delete staff member.", "Database Error", JOptionPane.ERROR_MESSAGE);
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
    }

    /**
     * Styles a JButton with a solid background color and a hover effect.
     *
     * @param button The button to style.
     * @param color The base color for the button.
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
