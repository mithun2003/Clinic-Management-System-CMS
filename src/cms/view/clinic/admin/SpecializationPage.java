package cms.view.clinic.admin;

import cms.model.dao.SpecializationDAO;
import cms.model.entities.Enums;
import cms.utils.FontUtils;
import cms.view.components.UIStyler;

import java.awt.*;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class SpecializationPage extends JPanel {

    // --- UI Components ---
    private JTextField tfSpecializationName;
    private JComboBox<Enums.Status> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTable table;
    private DefaultTableModel model;

    // --- State & DAO ---
    private final int clinicId;
    private final SpecializationDAO specializationDAO;
    private Integer selectedSpecializationId = null; // To track the selected item

    public SpecializationPage(int clinicId) {
        this.clinicId = clinicId;
        this.specializationDAO = new SpecializationDAO();

        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
        initListeners();
    }

    private void initComponents() {
        // --- Top Panel: Form for Adding/Updating ---
        JPanel formPanel = createFormPanel();

        // --- Buttons Panel ---
        JPanel buttonPanel = createButtonPanel();

        // --- Table to display specializations ---
        JScrollPane scrollPane = new JScrollPane(createTable());

        // --- Assemble the layout ---
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(1, 4, 15, 10));
        TitledBorder border = BorderFactory.createTitledBorder("Specialization Details");
        border.setTitleFont(FontUtils.getUiFont(Font.BOLD, 16));
        formPanel.setBorder(border);

        tfSpecializationName = new JTextField();
        cbStatus = new JComboBox<>(new Enums.Status[] { Enums.Status.Active, Enums.Status.Inactive });

        formPanel.add(new JLabel("Specialization Name:"));
        formPanel.add(tfSpecializationName);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(cbStatus);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Add New");
        btnUpdate = new JButton("Update Selected");
        btnDelete = new JButton("Remove Selected");
        btnClear = new JButton("Clear Form");

        UIStyler.styleButton(btnAdd, new Color(40, 167, 69)); // Green
        UIStyler.styleButton(btnUpdate, new Color(23, 162, 184)); // Blue
        UIStyler.styleButton(btnDelete, new Color(220, 53, 69)); // Red
        UIStyler.styleButton(btnClear, new Color(108, 117, 125)); // Gray

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        return buttonPanel;
    }

    private JTable createTable() {
        model = new DefaultTableModel(new String[] { "ID", "Specialization Name", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        UIStyler.styleTable(table);
        UIStyler.centerAlignColumns(table, 0);
        UIStyler.setColumnWidths(table, Map.of(0, 40));
        UIStyler.setStatusColumn(table, 2);

        // // --- Table Styling ---
        // table.setRowHeight(30);
        // table.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        // table.getTableHeader().setFont(FontUtils.getUiFont(Font.BOLD, 14));
        // table.getTableHeader().setBackground(new Color(0, 102, 102));
        // table.getTableHeader().setForeground(Color.WHITE);
        // table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // // Apply the reusable StatusRenderer to the status column
        // table.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());

        return table;
    }

    private void initListeners() {
        btnAdd.addActionListener(_ -> addSpecialization());
        btnUpdate.addActionListener(_ -> updateSpecialization());
        btnDelete.addActionListener(_ -> deleteSpecialization());
        btnClear.addActionListener(_ -> clearForm());

        // Table row selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                selectedSpecializationId = (Integer) model.getValueAt(selectedRow, 0);
                tfSpecializationName.setText((String) model.getValueAt(selectedRow, 1));
                cbStatus.setSelectedItem(model.getValueAt(selectedRow, 2));
            }
        });
    }

    // Public method to be called when the panel is shown
    public void refreshList() {
        model.setRowCount(0); // Clear the table
        List<SpecializationDAO.SpecializationDTO> specializations = specializationDAO
                .getSpecializationsForClinic(clinicId);
        for (SpecializationDAO.SpecializationDTO spec : specializations) {
            model.addRow(new Object[] { spec.getId(), spec.getName(), spec.getStatus() });
        }
        clearForm();
    }

    private void addSpecialization() {
        String newName = tfSpecializationName.getText().trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Specialization name cannot be empty.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (specializationDAO.addSpecialization(clinicId, newName)) {
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add specialization. It may already exist.", "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSpecialization() {
        if (selectedSpecializationId == null) {
            JOptionPane.showMessageDialog(this, "Please select a specialization from the table to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String updatedName = tfSpecializationName.getText().trim();
        if (updatedName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Specialization name cannot be empty.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Enums.Status updatedStatus = (Enums.Status) cbStatus.getSelectedItem();

        if (specializationDAO.updateSpecialization(selectedSpecializationId, updatedName, updatedStatus)) {
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update specialization. The name may already exist.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSpecialization() {
        if (selectedSpecializationId == null) {
            JOptionPane.showMessageDialog(this, "Please select a specialization from the table to remove.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = tfSpecializationName.getText();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete '" + name
                        + "'?\nThis cannot be undone and may affect historical records.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (specializationDAO.deleteSpecialization(selectedSpecializationId)) {
                refreshList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove specialization. It might be in use by a doctor.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        tfSpecializationName.setText("");
        cbStatus.setSelectedItem(Enums.Status.Active);
        selectedSpecializationId = null;
        table.clearSelection();
    }
}