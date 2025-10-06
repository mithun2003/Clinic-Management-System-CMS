package cms.model.entities;

import java.time.LocalDateTime;

/**
 * Represents a billing record for a specific appointment.
 * This class is primarily a Data Transfer Object (DTO) to hold data
 * fetched from the database for display in the UI.
 */
public class Bill {

    // --- Fields from the 'billing' table ---
    private int billId;
    private double amount;
    private Enums.BillingStatus status; // e.g., "Paid", "Unpaid"
    private LocalDateTime createdAt;
    
    // --- Denormalized fields (populated via JOINs for display) ---
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDate;

    // No-argument constructor
    public Bill() {}

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Enums.BillingStatus getStatus() { return status; }
    public void setStatus(Enums.BillingStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
}