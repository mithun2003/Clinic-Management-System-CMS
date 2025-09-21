package cms.model.entities;

public class User {
    private int userId;
    private int clinicId;
    private String name;
    private String username;
    private String password;
    private String role; // PRIMARY_ADMIN, ADMIN, DOCTOR, RECEPTIONIST

    // Relationship
    private Clinic clinic;   // each user belongs to one clinic

    public User() {}

    public User(int userId, int clinicId, String name, String username, String password, String role) {
        this.userId = userId;
        this.clinicId = clinicId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
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

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Clinic getClinic() { return clinic; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }
}