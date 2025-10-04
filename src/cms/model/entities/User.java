package cms.model.entities;

import java.time.LocalDateTime;

public class User {


    private int userId;
    private int clinicId;
    private String name;
    private String username;
    private String password;
    private Enums.Role role; //ADMIN, DOCTOR, RECEPTIONIST
    private Enums.Status status;
    private LocalDateTime createdAt; // maps to DB created_at
    private LocalDateTime updatedAt; // maps to DB updated_at

    // Relationship
    private Clinic clinic;   // each user belongs to one clinic
    private String specialization;

    public User() {}

    public User(int userId, int clinicId, String name, String username,
                String password, Enums.Role role, Enums.Status status,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.clinicId = clinicId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getClinicId() { return clinicId; }
    public void setClinicId(int clinicId) { this.clinicId = clinicId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Enums.Role getRole() { return role; }
    public void setRole(Enums.Role role) { this.role = role; }

    public Enums.Status getStatus() { return status; }
    public void setStatus(Enums.Status status) { this.status = status; }

    public Clinic getClinic() { return clinic; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

     public String getSpecialization() { return specialization; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }
}