package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Clinic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClinicDAO {

    // Add new clinic
    public void addClinic(Clinic clinic) {
        String sql = "INSERT INTO clinics (code, name, email, phone, address, status) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, clinic.getClinicCode());
            pst.setString(2, clinic.getClinicName());
            pst.setString(3, clinic.getEmail());
            pst.setString(4, clinic.getPhone());
            pst.setString(5, clinic.getAddress());
            pst.setString(6, clinic.getStatus() != null ? clinic.getStatus() : "Active"); // default Active
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
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
            pst.setString(6, clinic.getStatus());
            pst.setInt(7, clinic.getClinicId());

            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete clinic by ID
    public void deleteClinic(int clinicId) {
        String sql = "DELETE FROM clinics WHERE clinic_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, clinicId);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch all clinics
    public List<Clinic> getAllClinics() {
        List<Clinic> clinics = new ArrayList<>();
        String sql = "SELECT clinic_id, code, name, email, phone, address, status, created_at, updated_at FROM clinics";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Clinic c = new Clinic();
                c.setClinicId(rs.getInt("clinic_id"));
                c.setClinicCode(rs.getString("code"));
                c.setClinicName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                clinics.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                Clinic c = new Clinic();
                c.setClinicId(rs.getInt("clinic_id"));
                c.setClinicCode(rs.getString("code"));
                c.setClinicName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fetch paginated clinics
    public List<Clinic> getClinicsPage(int page, int pageSize) {
        List<Clinic> clinics = new ArrayList<>();
        String sql = "SELECT clinic_id, code, name, email, phone, address, status "
                + "FROM clinics ORDER BY clinic_id LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;

        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, pageSize);
            pst.setInt(2, offset);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("name"));
                Clinic c = new Clinic();
                c.setClinicId(rs.getInt("clinic_id"));
                c.setClinicCode(rs.getString("code"));
                c.setClinicName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                c.setStatus(rs.getString("status"));
                clinics.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clinics;
    }

    public enum Status {
        Active, Suspended
    }

    public int getTotalClinics() {
        // Default → no filter, get all
        String sql = "SELECT COUNT(*) AS total FROM clinics";

        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return 0;
    }
}
