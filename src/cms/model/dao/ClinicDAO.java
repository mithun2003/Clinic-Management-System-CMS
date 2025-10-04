package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Clinic;
import cms.model.entities.Enums;
import cms.utils.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClinicDAO {

    // Add new clinic
    public int addClinic(Clinic clinic) {
        String sql = "INSERT INTO clinics (code, name, email, phone, address, status) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); // We need to get the generated ID back
                PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, clinic.getClinicCode());
            pst.setString(2, clinic.getClinicName());
            pst.setString(3, clinic.getEmail());
            pst.setString(4, clinic.getPhone());
            pst.setString(5, clinic.getAddress());

            // First, get the status object.
            Enums.Status status = clinic.getStatus();
            // If the status object is null, default to Active. Otherwise, get its name.
            String statusStringToSave = (status != null) ? status.name() : Enums.Status.Active.name();
            // Now, set the parameter
            pst.setString(6, statusStringToSave);

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the new clinic_id
                    }
                }
            }
            LoggerUtil.logError("Failed to add clinic: " + clinic.getClinicName(),
                    new Exception("Insert failed, no rows affected."));
            return -1;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // This exception is thrown for duplicate unique keys (like code or email)
            LoggerUtil.logWarning("Attempted to insert a duplicate clinic: " + clinic.getClinicCode());
            return -2; // Special code for duplicate entry
        } catch (Exception e) {
            LoggerUtil.logError("Failed to add clinic: " + clinic.getClinicName(), e);
            return -1; // General failure
        }

    }

    // Update clinic details
    public void updateClinic(Clinic clinic) {
        String sql = "UPDATE clinics SET code=?, name=?, email=?, phone=?, address=?, status=? WHERE clinic_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, clinic.getClinicCode());
            pst.setString(2, clinic.getClinicName());
            pst.setString(3, clinic.getEmail());
            pst.setString(4, clinic.getPhone());
            pst.setString(5, clinic.getAddress());
            pst.setString(6, clinic.getStatus().name());
            pst.setInt(7, clinic.getClinicId());
            pst.executeUpdate();
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update clinic with ID: " + clinic.getClinicId(), e);
        }
    }

    // Delete clinic by ID
    public void deleteClinic(int clinicId) {
        String sql = "DELETE FROM clinics WHERE clinic_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            pst.executeUpdate();
        } catch (Exception e) {
            LoggerUtil.logError("Failed to delete clinic with ID: " + clinicId, e);
        }
    }

    // Fetch all clinics
    public List<Clinic> getAllClinics() {
        List<Clinic> clinics = new ArrayList<>();
        String sql = "SELECT clinic_id, code, name, email, phone, address, status, created_at, updated_at FROM clinics";
        try (Connection con = DBConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                clinics.add(getClinicDetails(rs));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch all clinics.", e);
        }
        return clinics;
    }

    // Find single clinic by ID
    public Clinic getClinicById(int clinicId) {
        String sql = "SELECT clinic_id, code, name, email, phone, address, status, created_at, updated_at FROM clinics WHERE clinic_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return getClinicDetails(rs);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch clinic with ID: " + clinicId, e);
        }
        return null;
    }

    private Clinic getClinicDetails(ResultSet rs) throws SQLException {
        Clinic c = new Clinic();
        c.setClinicId(rs.getInt("clinic_id"));
        c.setClinicCode(rs.getString("code"));
        c.setClinicName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setStatus(Enums.Status.valueOf(rs.getString("status")));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return c;
    }

    // Fetch paginated clinics
    public List<Clinic> getClinicsPage(int page, int pageSize) {
        List<Clinic> clinics = new ArrayList<>();
        String sql = "SELECT clinic_id, code, name, email, phone, address, status, created_at, updated_at "
                + "FROM clinics ORDER BY created_at DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, pageSize);
            pst.setInt(2, offset);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                clinics.add(getClinicDetails(rs));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch paginated clinics.", e);
        }
        return clinics;
    }

    public enum Status {
        Active, Suspended
    }

    public int getTotalClinics() {
        // Default → no filter, get all
        String sql = "SELECT COUNT(*) AS total FROM clinics";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total clinic count.", e);
        }
        return 0;
    }

    public int getTotalClinics(Status status) {
        // Filtered by status
        String sql = "SELECT COUNT(*) AS total FROM clinics WHERE status = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status.name());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get clinic count by status: " + status, e);
        }
        return 0;
    }

    /**
     * Checks if a clinic has at least one user with the 'ADMIN' role.
     *
     * @param clinicId The ID of the clinic to check.
     * @return true if an admin exists, false otherwise.
     */
    public boolean hasAdmin(int clinicId) {
        String sql = "SELECT COUNT(*) FROM users WHERE clinic_id = ? AND role = 'ADMIN'";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to check if clinic has an admin: " + clinicId, e);
        }
        return false;
    }
}
