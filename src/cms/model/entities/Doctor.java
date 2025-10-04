package cms.model.entities;

/**
 * Represents a doctor's specific profile, extending the base User entity.
 * This class maps to the 'doctors' table.
 */
public class Doctor {

    private int doctorId;
    private int userId; // Foreign key to the 'users' table

    // --- Information from the 'users' table (populated via a JOIN) ---
    private String name; // The doctor's full name

    // --- Information from the 'doctors' table ---
    private String specialization;
    private double consultationFee;
    private String schedule;
    private Enums.Status status;

    // Relationship to the base User object (optional, but can be useful)
    private User user;

    public Doctor() {}

    // --- Getters and Setters ---

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public Enums.Status getStatus() { return status; }
    public void setStatus(Enums.Status status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }


    /**
     * Provides a string representation of the doctor, which is essential for
     * displaying in UI components like JComboBox.
     * @return The doctor's name.
     */
    @Override
    public String toString() {
        return this.name; // Display the doctor's name in dropdowns
    }
}