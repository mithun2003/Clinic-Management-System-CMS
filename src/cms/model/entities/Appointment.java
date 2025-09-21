package cms.model.entities;

public class Appointment {
    private int apptId;
    private int clinicId;
    private int patientId;
    private int doctorId;
    private String apptDate;
    private String apptTime;
    private String status; // Scheduled, Completed, Cancelled

    public Appointment() {}

    public Appointment(int apptId, int clinicId, int patientId, int doctorId,
                       String apptDate, String apptTime, String status) {
        this.apptId = apptId;
        this.clinicId = clinicId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.apptDate = apptDate;
        this.apptTime = apptTime;
        this.status = status;
    }

    // Getters + setters...
}