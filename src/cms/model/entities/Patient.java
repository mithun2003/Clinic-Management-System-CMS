package cms.model.entities;

import java.time.LocalDate;

/**
 * Represents a patient record in the clinic.
 * This class is a Plain Old Java Object (POJO) that maps to the 'patients' table.
 */
public class Patient {

    private int patientId;
    private int clinicId;

    // Basic Information
    private String name;
    private LocalDate dob; // Use LocalDate for dates
    private String gender;

    // Contact Information
    private String phone;
    private String address;

    // Medical Information
    private String bloodGroup;
    private String allergies;

    // No-argument constructor (required by some frameworks)
    public Patient() {}

    // --- Getters and Setters ---

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getClinicId() { return clinicId; }
    public void setClinicId(int clinicId) { this.clinicId = clinicId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    /**
     * Provides a string representation of the patient, useful for debugging
     * and for displaying in UI components like JComboBox.
     * @return The patient's name.
     */
    @Override
    public String toString() {
        return this.name; // This is very useful for dropdown menus
    }
}