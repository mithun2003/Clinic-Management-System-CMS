package cms.model.entities;

import java.time.LocalDateTime;

/**
 * Represents a scheduled appointment, linking a patient to a doctor.
 * This class maps to the 'appointments' table.
 */
public class Appointment {

    public enum Status {
        Scheduled,
        Completed,
        Cancelled
    }

    private int appointmentId;
    private int patientId;    // Foreign key to patients
    private int doctorId;     // Foreign key to doctors
    private int clinicId;     // Foreign key to clinics

    private LocalDateTime appointmentDate;
    private Status status;
    private String notes; // Initial notes from receptionist

    // --- Denormalized fields for easy display in UI tables ---
    // These are not columns in the 'appointments' table but are populated via JOINs.
    private String patientName;
    private String doctorName;

    public Appointment() {}

    // --- Getters and Setters ---

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public int getClinicId() { return clinicId; }
    public void setClinicId(int clinicId) { this.clinicId = clinicId; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
}