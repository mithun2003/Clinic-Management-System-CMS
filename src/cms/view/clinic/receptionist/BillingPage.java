package cms.view.clinic.receptionist;

import cms.model.dao.BillingDAO;
import cms.model.entities.Bill;
import cms.model.entities.Enums; // Assuming BillingStatus is in your Enums file
import cms.view.components.StatusRenderer;
import cms.view.components.UIStyler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillingPage extends JPanel {

    // --- UI Components ---
    private JTable billsTable;
    private DefaultTableModel model;
    private JButton btnMarkAsPaid, btnViewReceipt;
    private JComboBox<Enums.BillingStatus> cbStatusFilter;

    // --- State & DAO ---
    private final int clinicId;
    private final BillingDAO billingDAO;

    public BillingPage(int clinicId) {
        this.clinicId = clinicId;
        this.billingDAO = new BillingDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        initComponents();
        initListeners();
    }

    private void initComponents() {
        // --- Top Panel: Filter Controls ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Filter Bills by Status"));
        cbStatusFilter = new JComboBox<>(Enums.BillingStatus.values());
        cbStatusFilter.setSelectedItem(Enums.BillingStatus.Unpaid); // Default to showing Unpaid bills
        topPanel.add(new JLabel("Show Bills:"));
        topPanel.add(cbStatusFilter);

        // --- Center Panel: Table of Bills ---
        model = new DefaultTableModel(
                // 👇 Added a "Status" column
                new String[] { "Bill ID", "Patient", "Doctor", "Appointment Date", "Amount (USD)", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(model);
        styleTable(billsTable);
        JScrollPane scrollPane = new JScrollPane(billsTable);

        // --- Action Panel ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnMarkAsPaid = new JButton("Mark Selected as Paid");
        btnViewReceipt = new JButton("View Receipt");
        UIStyler.styleButton(btnMarkAsPaid, new Color(40, 167, 69)); // Green
        UIStyler.styleButton(btnViewReceipt, new Color(108, 117, 125)); // Gray
        actionPanel.add(btnViewReceipt);
        actionPanel.add(btnMarkAsPaid);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void initListeners() {
        btnMarkAsPaid.addActionListener(_ -> markAsPaid());
        btnViewReceipt.addActionListener(_ -> viewReceipt());

        // Add a listener to the filter combo box
        cbStatusFilter.addActionListener(_ -> refreshBillList());
    }

    // Public method to load data when the panel is shown
    public void refreshBillList() {
        Enums.BillingStatus selectedStatus = (Enums.BillingStatus) cbStatusFilter.getSelectedItem();

        model.setRowCount(0); // Clear table
        List<Bill> bills = billingDAO.getBillsByStatus(clinicId, selectedStatus.name());
        for (Bill bill : bills) {
            model.addRow(new Object[] {
                    bill.getBillId(),
                    bill.getPatientName(),
                    bill.getDoctorName(),
                    bill.getAppointmentDate(),
                    String.format("%.2f", bill.getAmount()),
                    bill.getStatus() // Add the status to the row
            });
        }

        // The "Mark as Paid" button should only be enabled when viewing Unpaid bills
        btnMarkAsPaid.setVisible(selectedStatus == Enums.BillingStatus.Unpaid);
    }

    private void markAsPaid() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an unpaid bill to mark as paid.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) model.getValueAt(selectedRow, 0);
        String patientName = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm payment received for " + patientName + "?",
                "Confirm Payment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (billingDAO.updateBillStatus(billId, Enums.BillingStatus.Paid)) {
                JOptionPane.showMessageDialog(this, "Payment recorded successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshBillList(); // Refresh the list
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update payment status.", "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewReceipt() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view its receipt.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // In a real app, you would generate a PDF or a printable JDialog here.
        // For now, we'll show a simple JOptionPane.
        String patient = model.getValueAt(selectedRow, 1).toString();
        String doctor = model.getValueAt(selectedRow, 2).toString();
        LocalDateTime apptDate = (LocalDateTime) model.getValueAt(selectedRow, 3);
        String amount = model.getValueAt(selectedRow, 4).toString();

        Enums.BillingStatus status = (Enums.BillingStatus) model.getValueAt(selectedRow, 5);
        String receiptText = "<html><h2>Clinic Receipt</h2>" +
                "<p><b>Patient:</b> " + patient + "</p>" +
                "<p><b>Doctor:</b> " + doctor + "</p>" +
                "<p><b>Date:</b> " + apptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</p>" +
                "<hr><p><b>Total Amount:</b> ₹" + amount + "</p>" +
                "<p><b>Status:</b> " + status.name() + "</p></html>";
        JOptionPane.showMessageDialog(this, receiptText, "Receipt Preview", JOptionPane.INFORMATION_MESSAGE);

    }

    private void styleTable(JTable table) {
        UIStyler.styleTable(table); // Use your reusable styler
       UIStyler.centerAlignColumns(table, 0);
        UIStyler.setDateTimeColumn(table, 3);
        UIStyler.rightAlignColumns(table, 4);
        UIStyler.setStatusColumn(table, 5);
    }
}