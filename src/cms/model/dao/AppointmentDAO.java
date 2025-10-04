package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Appointment;
import cms.utils.LoggerUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    /**
     * Books a new appointment.
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
     * @param clinicId The ID of the clinic.
     * @param date The date to fetch appointments for.
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
                     "ORDER BY a.appointment_date ASC";

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
                appt.setStatus(Appointment.Status.valueOf(rs.getString("status")));

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
}