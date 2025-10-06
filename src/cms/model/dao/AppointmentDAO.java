package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Appointment;
import cms.model.entities.Enums;
import cms.utils.LoggerUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    /**
     * Books a new appointment.
     * 
     * @param appointment The Appointment object to save.
     * @return true if booking was successful, false otherwise.
     */
    public boolean bookAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, clinic_id, appointment_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, appointment.getPatientId());
            pst.setInt(2, appointment.getDoctorId());
            pst.setInt(3, appointment.getClinicId());
            pst.setTimestamp(4, Timestamp.valueOf(appointment.getAppointmentDate()));
            pst.setString(5, appointment.getStatus().name());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to book appointment.", e);
            return false;
        }
    }

    /**
     * Fetches appointments for a specific day and clinic.
     * 
     * @param clinicId The ID of the clinic.
     * @param date     The date to fetch appointments for.
     * @return A list of Appointment objects.
     */
    public List<Appointment> getAppointmentsByDate(int clinicId, java.time.LocalDate date) {
        List<Appointment> appointmentList = new ArrayList<>();
        // Query joins with patients and users to get names for display
        String sql = "SELECT a.*, p.name as patient_name, u.name as doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users u ON d.user_id = u.user_id " +
                "WHERE a.clinic_id = ? AND DATE(a.appointment_date) = ? " +
                "ORDER BY a.appointment_date DESC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, clinicId);
            pst.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setPatientId(rs.getInt("patient_id"));
                appt.setDoctorId(rs.getInt("doctor_id"));
                appt.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                appt.setStatus(Enums.AppointmentStatus.valueOf(rs.getString("status")));

                // Set the names fetched from the join
                appt.setPatientName(rs.getString("patient_name"));
                appt.setDoctorName(rs.getString("doctor_name"));

                appointmentList.add(appt);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch appointments for date: " + date, e);
        }
        return appointmentList;
    }

    public boolean updateAppointmentStatus(int appointmentId, Enums.AppointmentStatus newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, newStatus.name());
            pst.setInt(2, appointmentId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update status for appointment ID: " + appointmentId, e);
            return false;
        }
    }

    /**
     * Fetches today's appointments for a specific doctor.
     * 
     * @param doctorId The ID of the doctor (from the 'doctors' table).
     * @return A list of Appointment objects.
     */
    public List<Appointment> getTodaysAppointmentsForDoctor(int doctorId, int page, int pageSize) {
        List<Appointment> appointmentList = new ArrayList<>();
        // This query gets today's appointments that are 'Scheduled' or 'Completed'
        String sql = "SELECT a.*,p.patient_id, p.name as patient_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "WHERE a.doctor_id = ? AND DATE(a.appointment_date) = CURDATE() " +
                "AND a.status = 'Scheduled' " +
                "ORDER BY a.appointment_date DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, doctorId);
            pst.setInt(2, pageSize);
            pst.setInt(3, offset);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setPatientName(rs.getString("patient_name"));
                appt.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                appt.setStatus(Enums.AppointmentStatus.valueOf(rs.getString("status")));
                appt.setPatientId(rs.getInt("patient_id"));

                appointmentList.add(appt);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch today's appointments for doctor ID: " + doctorId, e);
        }
        return appointmentList;
    }

    /**
     * Marks an appointment as 'Completed' and saves the doctor's notes.
     * 
     * @param appointmentId The ID of the appointment.
     * @param notes         The consultation notes from the doctor.
     * @return true if successful, false otherwise.
     */
    public boolean completeAppointment(int appointmentId, String notes) {
        // In a real system, 'notes' might go to a 'prescriptions' or 'visit_details'
        // table.
        // For now, we can update the notes in the appointments table.
        String sql = "UPDATE appointments SET status = 'Completed', notes = ? WHERE appointment_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, notes);
            pst.setInt(2, appointmentId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to complete appointment ID: " + appointmentId, e);
            return false;
        }
    }

    /**
     * Fetches a single, complete appointment record by its primary key.
     * This query joins with other tables to get all necessary display names.
     *
     * @param appointmentId The ID of the appointment to fetch.
     * @return An Appointment object with all details, or null if not found.
     */
    public Appointment getAppointmentById(int appointmentId) {
        Appointment appointment = null;
        // This query is similar to getAppointmentsByDate but for a single ID
        String sql = "SELECT a.*, p.name as patient_name, u.name as doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users u ON d.user_id = u.user_id " +
                "WHERE a.appointment_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, appointmentId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setClinicId(rs.getInt("clinic_id"));
                appointment.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                appointment.setStatus(Enums.AppointmentStatus.valueOf(rs.getString("status")));
                appointment.setNotes(rs.getString("notes")); // Include the notes

                // Set the denormalized names for easy display
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setDoctorName(rs.getString("doctor_name"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch appointment with ID: " + appointmentId, e);
        }

        return appointment;
    }

    public int getTodaysAppointmentCountForDoctor(int doctorId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND DATE(appointment_date) = CURDATE()";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, doctorId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to count appointments for doctor ID: " + doctorId, e);
        }
        return 0;
    }

    // In AppointmentDAO.java

    /**
     * Fetches all appointments for a specific doctor on a specific date.
     * 
     * @param doctorId The ID of the doctor (from the 'doctors' table).
     * @param date     The date to query.
     * @return A list of appointments for that doctor on that day.
     */
    public List<Appointment> getAppointmentsForDoctorByDate(int doctorId, LocalDate date) {
        List<Appointment> appointmentList = new ArrayList<>();
        // This query is more specific than the receptionist's version
        String sql = "SELECT a.appointment_id, a.patient_id, a.doctor_id, a.clinic_id, " +
                "a.appointment_date, a.status, a.notes, p.name as patient_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "WHERE a.doctor_id = ? AND DATE(a.appointment_date) = ? " +
                "ORDER BY a.appointment_date DESC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, doctorId);
            pst.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setAppointmentId(rs.getInt("appointment_id"));
                appt.setPatientName(rs.getString("patient_name"));
                appt.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                appt.setStatus(Enums.AppointmentStatus.valueOf(rs.getString("status")));
                appt.setNotes(rs.getString("notes"));

                appointmentList.add(appt);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch appointments for doctor ID " + doctorId + " on date " + date, e);
        }
        return appointmentList;
    }
}
