package cms.model.entities;

public class Doctor {
    private int doctorId;
    private int userId; // link to User
    private String specialty;
    private String schedule;
    
    private User user; // relationship to User


    public Doctor() {}

    public Doctor(int doctorId, int userId, String specialty, String schedule) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.specialty = specialty;
        this.schedule = schedule;
    }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}