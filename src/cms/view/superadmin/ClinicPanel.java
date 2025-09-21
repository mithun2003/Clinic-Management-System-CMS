package cms.view.superadmin;

import cms.model.dao.ClinicDAO;
import cms.model.entities.Clinic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class ClinicPanel extends JPanel {

    // Form fields
    private JTextField tfCode, tfName, tfEmail, tfPhone, tfAddress;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // Table
    private JTable table;
    private DefaultTableModel model;

    // Pagination
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages;
    private JButton btnPrev, btnNext;
    private JLabel lblPage;

    // DAO
    private ClinicDAO clinicDAO;

    public ClinicPanel() {
        setLayout(new BorderLayout(10, 10));
        clinicDAO = new ClinicDAO();

        // === Form ===
        TitledBorder clinicBorder = BorderFactory.createTitledBorder("Clinic Form");
        clinicBorder.setTitleFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(clinicBorder);
//        formPanel.setBorder(BorderFactory.createTitledBorder("Clinic Form"));

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

        // === Buttons ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("➕ Add Clinic");
        btnUpdate = new JButton("✏️ Update Clinic");
        btnDelete = new JButton("❌ Delete Clinic");
        btnClear = new JButton("Clear");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        // === Add to BorderLayout ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === Table + Scroll ===
        model = new DefaultTableModel(
                new String[]{"ID", "Code", "Name", "Email", "Phone", "Address", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // This will make all cells non-editable
                return false;
            }
        };
        table = new JTable(model);

        // ✅ Table Styling
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 102, 102));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        // Center align for ID + Code
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

        // === Pagination controls ===
        JPanel pagerPanel = new JPanel(new FlowLayout());
        btnPrev = new JButton("⬅ Prev");
        btnNext = new JButton("Next ➡");
        lblPage = new JLabel("Page 1");
        pagerPanel.add(btnPrev);
        pagerPanel.add(lblPage);
        pagerPanel.add(btnNext);

        // === Add panels to layout ===
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);   // ✅ Now adds scrollPane, not raw table
        add(pagerPanel, BorderLayout.SOUTH);

        // Load initial data
//        loadClinicsPage(currentPage);
        // === Listeners ===
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadClinicsPage(currentPage);
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadClinicsPage(currentPage);
            }
        });

        btnAdd.addActionListener(e -> addClinic());
        btnUpdate.addActionListener(e -> updateClinic());
        btnDelete.addActionListener(e -> deleteClinic());
        btnClear.addActionListener(e -> clearForm());

        // Table row -> form
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                tfCode.setText(valueOrEmpty(model.getValueAt(row, 1)));
                tfName.setText(valueOrEmpty(model.getValueAt(row, 2)));
                tfEmail.setText(valueOrEmpty(model.getValueAt(row, 3)));
                tfPhone.setText(valueOrEmpty(model.getValueAt(row, 4)));
                tfAddress.setText(valueOrEmpty(model.getValueAt(row, 5)));
                cbStatus.setSelectedItem(valueOrEmpty(model.getValueAt(row, 6)));
            }
        });

        // 👇 ADD THIS LISTENER AT THE END OF THE CONSTRUCTOR
        this.addHierarchyListener(new java.awt.event.HierarchyListener() {
            @Override
            public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
                // Check if the change was the panel being hidden
                if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                        && !ClinicPanel.this.isShowing()) {

                    // When the panel is hidden, clear the form
                    clearForm();
                }
            }
        });
    }
// helper method

    private String valueOrEmpty(Object val) {
        System.out.println(val);
        return val == null ? "" : val.toString();
    }

    // === Methods ===
    private void loadClinicsPage(int page) {
        int totalRecords = clinicDAO.getTotalClinics();
        totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        model.setRowCount(0); // clear
        List<Clinic> list = clinicDAO.getClinicsPage(page, pageSize);
        for (Clinic c : list) {
            model.addRow(new Object[]{
                c.getClinicId(), c.getClinicCode(), c.getClinicName(),
                c.getEmail(), c.getPhone(), c.getAddress(), c.getStatus()
            });
        }

        model.fireTableDataChanged();
        lblPage.setText("Page " + currentPage + " of " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void addClinic() {
        if (tfCode.getText().isBlank() || tfName.getText().isBlank() || tfEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in at least Code, Name, and Email before adding.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Clinic c = new Clinic();
        c.setClinicCode(tfCode.getText());
        c.setClinicName(tfName.getText());
        c.setEmail(tfEmail.getText());
        c.setPhone(tfPhone.getText());
        c.setAddress(tfAddress.getText());
        c.setStatus(cbStatus.getSelectedItem().toString());

        clinicDAO.addClinic(c);
        loadClinicsPage(currentPage);
        clearForm();
    }

    private void updateClinic() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a clinic first!");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

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

    private void deleteClinic() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a clinic first!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String code = model.getValueAt(row, 1).toString();
        String name = model.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you really want to delete clinic: " + name + " (" + code + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            clinicDAO.deleteClinic(id);
            loadClinicsPage(currentPage);
            clearForm();
        }
    }

    private void clearForm() {
        tfCode.setText("");
        tfName.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfAddress.setText("");
        cbStatus.setSelectedIndex(0);
    }

    public void refreshClinics() {
        currentPage = 1;
        loadClinicsPage(currentPage);
    }
}
