package cms.model.dao;

import cms.model.database.DBConnection;
import cms.utils.LoggerUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDAO {

    // Fetches a map of {Clinic Name -> Patient Count}
    public Map<String, Integer> getPatientCountPerClinic() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT c.name, COUNT(p.patient_id) as patient_count "
                     + "FROM clinics c LEFT JOIN patients p ON c.clinic_id = p.clinic_id "
                     + "GROUP BY c.clinic_id, c.name ORDER BY patient_count DESC";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("patient_count"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch patient count per clinic.", e);
        }
        return data;
    }

    // Fetches a map of {Role -> User Count}
    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT role, COUNT(user_id) as user_count FROM users GROUP BY role";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("role"), rs.getInt("user_count"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch user count by role.", e);
        }
        return data;
    }

    // Fetches a map of {Month -> New Patient Count}
    public Map<String, Integer> getNewPatientsPerMonth() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(patient_id) as new_patients "
                     + "FROM patients GROUP BY month ORDER BY month ASC LIMIT 12"; // Last 12 months
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("month"), rs.getInt("new_patients"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch new patient count per month.", e);
        }
        return data;
    }

    // Fetches the number of patients for a specific clinic
    public int getPatientCountForClinic(int clinicId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE clinic_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get patient count for clinic ID: " + clinicId, e);
        }
        return 0;
    }

    // Fetches today's appointment count for a specific clinic
    public int getTodaysAppointmentCountForClinic(int clinicId) {
        // CURDATE() is a MySQL function to get the current date
        String sql = "SELECT COUNT(*) FROM appointments WHERE clinic_id = ? AND DATE(appointment_date) = CURDATE()";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get today's appointment count for clinic ID: " + clinicId, e);
        }
        return 0;
    }

    // Fetches the total number of staff members in a clinic (excluding ADMINs)
    public int getUserCountByClinic(int clinicId, boolean excludeAdmins) {
        String sql = "SELECT COUNT(*) FROM users WHERE clinic_id = ?";
        if (excludeAdmins) {
            sql += " AND role != 'ADMIN'";
        }
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to count users for clinic ID: " + clinicId, e);
        }
        return 0;
    }

    /**
     * Fetches the number of appointments per day for the last 7 days for a
     * specific clinic.
     *
     * @param clinicId The ID of the clinic.
     * @return A map of {Date -> Appointment Count}.
     */
    public Map<String, Integer> getAppointmentsLast7Days(int clinicId) {
        Map<String, Integer> data = new LinkedHashMap<>();
        // This query groups appointments by date for the last 7 days for a specific clinic.
        String sql = "SELECT DATE(appointment_date) as day, COUNT(appointment_id) as count "
                     + "FROM appointments "
                     + "WHERE clinic_id = ? AND appointment_date >= CURDATE() - INTERVAL 6 DAY "
                     + "GROUP BY day ORDER BY day ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("day"), rs.getInt("count"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get appointments for last 7 days for clinic ID: " + clinicId, e);
        }
        return data;
    }

    /**
     * Fetches the number of new patients registered per day for the last 30
     * days for a specific clinic.
     *
     * @param clinicId The ID of the clinic.
     * @return A map of {Date -> New Patient Count}.
     */
    public Map<String, Integer> getNewPatientsLast30Days(int clinicId) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT DATE(created_at) as day, COUNT(patient_id) as count "
                     + "FROM patients "
                     + "WHERE clinic_id = ? AND created_at >= CURDATE() - INTERVAL 29 DAY "
                     + "GROUP BY day ORDER BY day ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("day"), rs.getInt("count"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get new patients for last 30 days for clinic ID: " + clinicId, e);
        }
        return data;
    }

    /**
     * Fetches the count of each staff role (excluding ADMIN) for a specific
     * clinic.
     *
     * @param clinicId The ID of the clinic.
     * @return A map of {Role -> Staff Count}.
     */
    public Map<String, Integer> getStaffCountByRoleForClinic(int clinicId) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT role, COUNT(user_id) as count FROM users "
                     + "WHERE clinic_id = ? "
                     + "GROUP BY role";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("role"), rs.getInt("count"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get staff count by role for clinic ID: " + clinicId, e);
        }
        return data;
    }

    /**
     * Fetches the performance of each doctor in a specific clinic, ranked by
     * the number of completed appointments in the last 30 days.
     *
     * @param clinicId The ID of the clinic.
     * @return A map of {Doctor Name -> Completed Appointment Count}.
     */
    public Map<String, Integer> getDoctorPerformance(int clinicId) {
        // Use LinkedHashMap to preserve the order from the SQL query (sorted by performance)
        Map<String, Integer> data = new LinkedHashMap<>();

        // SQL query to join appointments, doctors, and users tables
        String sql = "SELECT u.name, COUNT(a.appointment_id) as appointment_count "
                     + "FROM appointments a "
                     + "JOIN doctors d ON a.doctor_id = d.doctor_id "
                     + "JOIN users u ON d.user_id = u.user_id "
                     + "WHERE a.clinic_id = ? "
                     + "AND a.status = 'Completed' "
                     + "AND a.appointment_date >= CURDATE() - INTERVAL 29 DAY "
                     + "GROUP BY u.user_id, u.name "
                     + "ORDER BY appointment_count DESC";

        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            // Set the clinic_id parameter for the WHERE clause
            pst.setInt(1, clinicId);

            ResultSet rs = pst.executeQuery();

            // Loop through the results and populate the map
            while (rs.next()) {
                String doctorName = rs.getString("name");
                int appointmentCount = rs.getInt("appointment_count");
                data.put(doctorName, appointmentCount);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get doctor performance for clinic ID: " + clinicId, e);
        }

        return data;
    }
}
