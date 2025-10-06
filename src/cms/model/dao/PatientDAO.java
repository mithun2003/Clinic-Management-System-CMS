package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Patient;
import cms.utils.LoggerUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    /**
     * Adds a new patient to the database.
     * 
     * @param patient The Patient object to add.
     * @return true if the patient was added successfully, false otherwise.
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (clinic_id, name, dob, gender, phone, address, blood_group, allergies) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, patient.getClinicId());
            pst.setString(2, patient.getName());
            pst.setDate(3, java.sql.Date.valueOf(patient.getDob())); // Convert LocalDate to sql.Date
            pst.setString(4, patient.getGender());
            pst.setString(5, patient.getPhone());
            pst.setString(6, patient.getAddress());
            pst.setString(7, patient.getBloodGroup());
            pst.setString(8, patient.getAllergies());

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            LoggerUtil.logWarning("Attempted to insert a patient with a duplicate phone number: " + patient.getPhone());
            return false;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to add patient: " + patient.getName(), e);
            return false;
        }
    }

    /**
     * Updates an existing patient's details in the database.
     * 
     * @param patient The Patient object with updated details.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, dob = ?, gender = ?, phone = ?, " +
                "address = ?, blood_group = ?, allergies = ? WHERE patient_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, patient.getName());
            pst.setDate(2, java.sql.Date.valueOf(patient.getDob()));
            pst.setString(3, patient.getGender());
            pst.setString(4, patient.getPhone());
            pst.setString(5, patient.getAddress());
            pst.setString(6, patient.getBloodGroup());
            pst.setString(7, patient.getAllergies());
            pst.setInt(8, patient.getPatientId());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update patient with ID: " + patient.getPatientId(), e);
            return false;
        }
    }

    /**
     * Fetches a list of all patients for a specific clinic.
     * 
     * @param clinicId The ID of the clinic.
     * @return A list of Patient objects.
     */
    public List<Patient> getPatientsByClinic(int clinicId) {
        List<Patient> patientList = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE clinic_id = ? ORDER BY name ASC";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                patientList.add(mapResultSetToPatient(rs));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch patients for clinic ID: " + clinicId, e);
        }
        return patientList;
    }

    /**
     * Searches for patients by name or phone number within a specific clinic.
     * 
     * @param clinicId   The ID of the clinic.
     * @param searchTerm The name or phone number to search for.
     * @return A list of matching Patient objects.
     */
    public List<Patient> searchPatients(int clinicId, String searchTerm) {
        List<Patient> patientList = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE clinic_id = ? AND (name LIKE ? OR phone LIKE ?) ORDER BY name ASC";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            pst.setString(2, "%" + searchTerm + "%"); // Use wildcards for partial matches
            pst.setString(3, "%" + searchTerm + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                patientList.add(mapResultSetToPatient(rs));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to search for patients with term: " + searchTerm, e);
        }
        return patientList;
    }

    // Helper method to map a ResultSet row to a Patient object to avoid code
    // duplication
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setClinicId(rs.getInt("clinic_id"));
        p.setName(rs.getString("name"));
        if (rs.getDate("dob") != null) {
            p.setDob(rs.getDate("dob").toLocalDate());
        }
        p.setGender(rs.getString("gender"));
        p.setPhone(rs.getString("phone"));
        p.setAddress(rs.getString("address"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setAllergies(rs.getString("allergies"));
        return p;
    }

    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, patientId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToPatient(rs); // Use your existing helper method
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch patient with ID: " + patientId, e);
        }
        return null;
    }

    /**
     * Gets the total number of patients for a specific clinic.
     * 
     * @param clinicId The ID of the clinic.
     * @return The total count.
     */
    public int getTotalPatientsByClinic(int clinicId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE clinic_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total patient count for clinic ID: " + clinicId, e);
        }
        return 0;
    }

    /**
     * Fetches a paginated list of patients for a specific clinic.
     * 
     * @param clinicId The ID of the clinic.
     * @param page     The page number to fetch.
     * @param pageSize The number of records per page.
     * @return A list of Patient objects.
     */
    public List<Patient> getPaginatedPatientsByClinic(int clinicId, int page, int pageSize) {
        List<Patient> patientList = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE clinic_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            pst.setInt(2, pageSize);
            pst.setInt(3, offset);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                patientList.add(mapResultSetToPatient(rs)); // Assuming you have this helper
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch paginated patients for clinic ID: " + clinicId, e);
        }
        return patientList;
    }
}