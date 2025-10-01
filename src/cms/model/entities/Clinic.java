package cms.model.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Clinic {

    /**
     * Enum to represent the operational status of a clinic. Ensures that only
     * valid statuses can be used.
     */
    public enum Status {
        Active,
        Suspended
    }

    private int clinicId;
    private String clinicCode;     // maps to DB `code`
    private String clinicName;     // maps to DB `name`
    private String email;          // maps to DB `email`
    private String phone;          // maps to DB `phone`
    private String address;        // maps to DB `address`
    private Status status;         // maps to DB `status`
    private LocalDateTime createdAt; // maps to DB created_at
    private LocalDateTime updatedAt; // maps to DB updated_at

    // Relationships
    private List<User> users;
    private List<Patient> patients;
    private List<Appointment> appointments;

    public Clinic() {}

    public Clinic(int clinicId, String clinicCode, String clinicName,
            String email, String phone, String address, Status status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.clinicId = clinicId;
        this.clinicCode = clinicCode;
        this.clinicName = clinicName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters / Setters
    public int getClinicId() { return clinicId; }
    public void setClinicId(int clinicId) { this.clinicId = clinicId; }

    public String getClinicCode() { return clinicCode; }
    public void setClinicCode(String clinicCode) { this.clinicCode = clinicCode; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
}