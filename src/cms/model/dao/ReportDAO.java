package cms.model.dao;

import cms.model.database.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDAO {

    // Fetches a map of {Clinic Name -> Patient Count}
    public Map<String, Integer> getPatientCountPerClinic() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT c.name, COUNT(p.patient_id) as patient_count " +
                     "FROM clinics c LEFT JOIN patients p ON c.clinic_id = p.clinic_id " +
                     "GROUP BY c.clinic_id, c.name ORDER BY patient_count DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("patient_count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Fetches a map of {Role -> User Count}
    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT role, COUNT(user_id) as user_count FROM users GROUP BY role";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("role"), rs.getInt("user_count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Fetches a map of {Month -> New Patient Count}
    public Map<String, Integer> getNewPatientsPerMonth() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(patient_id) as new_patients " +
                     "FROM patients GROUP BY month ORDER BY month ASC LIMIT 12"; // Last 12 months
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("month"), rs.getInt("new_patients"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}