package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Doctor;
import cms.model.entities.Enums;
import cms.utils.LoggerUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /**
     * Adds a new doctor's details to the 'doctors' table.
     * 
     * @param doctor The Doctor object containing the user_id, specialization, fee,
     *               and schedule.
     * @return true if the record was added successfully, false otherwise.
     */
    public boolean addDoctorDetails(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, specialization, consultation_fee, schedule, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, doctor.getUserId());
            pst.setString(2, doctor.getSpecialization());
            pst.setDouble(3, doctor.getConsultationFee());
            pst.setString(4, doctor.getStatus().name() != null ? doctor.getStatus().name() : "Active");
            pst.setString(5, doctor.getSchedule());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to add doctor details for user ID: " + doctor.getUserId(), e);
            return false;
        }
    }

    /**
     * Updates the details for an existing doctor.
     * 
     * @param doctor The Doctor object with updated details.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDoctorDetails(Doctor doctor) {
        String sql = "UPDATE doctors SET specialization = ?, consultation_fee = ?, schedule = ?, status = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, doctor.getSpecialization());
            pst.setDouble(2, doctor.getConsultationFee());
            pst.setString(3, doctor.getSchedule());
            pst.setString(4, doctor.getStatus().name());
            pst.setInt(5, doctor.getUserId());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update doctor details for user ID: " + doctor.getUserId(), e);
            return false;
        }
    }

    /**
     * "Soft deletes" a doctor's profile by setting their status to 'Inactive'.
     * This is called when a user's role is changed from DOCTOR to something else.
     * 
     * @param userId The ID of the user.
     * @return true if successful, false otherwise.
     */
    public boolean setDoctorStatusInactive(int userId) {
        String sql = "UPDATE doctors SET status = 'Inactive' WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to set doctor status to inactive for user ID: " + userId, e);
            return false;
        }
    }

    /**
 * Fetches a single doctor's complete profile by their user_id,
 * including related user details.
 * 
 * @param userId The ID of the user.
 * @return A Doctor object populated with both doctor and user info, or null if not found.
 */
public Doctor getDoctorByUserId(int userId) {
    String sql = """
        SELECT d.*, u.name, u.role, u.username, u.status AS user_status
        FROM doctors d
        JOIN users u ON d.user_id = u.user_id
        WHERE d.user_id = ?
    """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Doctor doc = new Doctor();
            doc.setDoctorId(rs.getInt("doctor_id"));
            doc.setUserId(rs.getInt("user_id"));
            doc.setSpecialization(rs.getString("specialization"));
            doc.setConsultationFee(rs.getDouble("consultation_fee"));
            doc.setSchedule(rs.getString("schedule"));
            doc.setStatus(Enums.Status.valueOf(rs.getString("status")));
            doc.setName(rs.getString("name")); // for quick access

            // --- Build the User object ---
            // User user = new User();
            // user.setUserId(rs.getInt("user_id"));
            // user.setName(rs.getString("name"));
            // user.setRole(Enums.Role.valueOf(rs.getString("role")));
            // user.setUsername(rs.getString("username"));
            // user.setStatus(Enums.Status.valueOf(rs.getString("user_status")));

            // // Attach user to doctor
            // doc.setUser(user);

            return doc;
        }

    } catch (Exception e) {
        LoggerUtil.logError("Failed to get doctor details for user ID: " + userId, e);
    }

    return null;
}


    /**
     * Checks if a record already exists in the 'doctors' table for a given user_id.
     * Useful for deciding whether to INSERT or UPDATE details.
     * 
     * @param userId The ID of the user.
     * @return true if a doctor record exists, false otherwise.
     */
    public boolean doctorExists(int userId) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to check if doctor exists for user ID: " + userId, e);
        }
        return false;
    }

    /**
     * Fetches a list of all ACTIVE doctors for a specific clinic.
     * Used to populate dropdowns in the Appointment Booking panel.
     * 
     * @param clinicId The ID of the clinic.
     * @return A list of Doctor objects.
     */
    public List<Doctor> getActiveDoctorsByClinic(int clinicId) {
        List<Doctor> doctorList = new ArrayList<>();
        // Join with users table to get the name and filter by clinic
        String sql = "SELECT d.doctor_id, d.user_id, d.specialization, d.consultation_fee, u.name " +
                "FROM doctors d " +
                "JOIN users u ON d.user_id = u.user_id " +
                "WHERE u.clinic_id = ? AND u.status = 'Active' AND d.status = 'Active' " +
                "ORDER BY u.name ASC";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Doctor doc = new Doctor();
                doc.setDoctorId(rs.getInt("doctor_id"));
                doc.setUserId(rs.getInt("user_id"));
                doc.setName(rs.getString("name"));
                doc.setSpecialization(rs.getString("specialization"));
                doc.setConsultationFee(rs.getDouble("consultation_fee"));
                doctorList.add(doc);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch active doctors for clinic ID: " + clinicId, e);
        }
        return doctorList;
    }
}