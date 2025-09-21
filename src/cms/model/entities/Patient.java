package cms.model.entities;

public class Patient {
    private int patientId;
    private int clinicId;
    private String name;
    private String dob; // use String (yyyy-mm-dd) for simplicity, can use java.sql.Date
    private String contact;
    private String history;

    private User user; // relationship to User

    public Patient() {}

    public Patient(int patientId, int clinicId, String name, String dob, String contact, String history) {
        this.patientId = patientId;
        this.clinicId = clinicId;
        this.name = name;
        this.dob = dob;
        this.contact = contact;
        this.history = history;
    }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getClinicId() { return clinicId; }
    public void setClinicId(int clinicId) { this.clinicId = clinicId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
}