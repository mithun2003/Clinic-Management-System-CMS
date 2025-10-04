package cms.view.clinic.admin;

import cms.model.dao.DoctorDAO;
import cms.model.dao.SpecializationDAO;
import cms.model.entities.Doctor;
import cms.model.entities.Enums;
import cms.utils.TitleBarManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DoctorDetailsDialog extends JDialog {

    // --- UI Components ---
    private JComboBox<String> cbSpecialization;
    private JTextField tfFee;
    private JTextArea taSchedule;
     private JComboBox<Enums.Status> cbStatus;
    private JButton btnSave;

    // --- State & DAO ---
    private final int userId;
    private final int clinicId;
    private final DoctorDAO doctorDAO;
    private Doctor result = null; // To hold the result

    public DoctorDetailsDialog(Frame parent, int userId, int clinicId) {
        super(parent, "Doctor Specific Details", true);
        this.userId = userId;
        this.clinicId = clinicId;
        this.doctorDAO = new DoctorDAO();

        initComponents();
        loadExistingData(); // Load data if this doctor already has details
    }

    private void initComponents() {
        setUndecorated(true);
        setSize(500, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // --- Custom Title Bar ---
        JPanel titleBar = TitleBarManager.createTitleBar(this, "Doctor Details");
        add(titleBar, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Specialization
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Specialization:"), gbc);
        List<String> specializations = new SpecializationDAO().getActiveSpecializationsForClinic(clinicId);
        cbSpecialization = new JComboBox<>(specializations.toArray(new String[0]));
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(cbSpecialization, gbc);
        
        // Consultation Fee
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Consultation Fee:"), gbc);
        tfFee = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(tfFee, gbc);

        // Schedule
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Schedule (e.g., Mon-Fri 9am-5pm):"), gbc);
        taSchedule = new JTextArea(4, 20);
        taSchedule.setLineWrap(true);
        taSchedule.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridheight = 2;
        formPanel.add(new JScrollPane(taSchedule), gbc);

        //Status
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Doctor Status:"), gbc);
        cbStatus = new JComboBox<>(new Enums.Status[] { Enums.Status.Active, Enums.Status.Inactive });
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(cbStatus, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save Details");
        btnSave.addActionListener(_ -> saveDetails());
        buttonPanel.add(btnSave);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadExistingData() {
        Doctor existingDoctor = doctorDAO.getDoctorByUserId(userId);
        System.out.println(existingDoctor.getStatus());
        if (existingDoctor != null) {
            cbSpecialization.setSelectedItem(existingDoctor.getSpecialization());
            tfFee.setText(String.valueOf(existingDoctor.getConsultationFee()));
            taSchedule.setText(existingDoctor.getSchedule());
            cbStatus.setSelectedItem(existingDoctor.getStatus());
        }
    }

    private void saveDetails() {
        // --- Validation ---
        String feeText = tfFee.getText();
        double fee = 0.0;
        try {
            if (!feeText.isEmpty()) {
                fee = Double.parseDouble(feeText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the consultation fee.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Create result object ---
        result = new Doctor();
        result.setUserId(this.userId);
        result.setSpecialization((String) cbSpecialization.getSelectedItem());
        result.setConsultationFee(fee);
        result.setSchedule(taSchedule.getText());
        result.setStatus((Enums.Status) cbStatus.getSelectedItem());
        
        dispose(); // Close the dialog
    }
    
    // Public method for the calling class to get the result
    public Doctor getResult() {
        return result;
    }
}