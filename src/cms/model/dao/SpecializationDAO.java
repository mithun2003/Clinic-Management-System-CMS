package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Enums;
import cms.utils.LoggerUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecializationDAO {

    public static class SpecializationDTO {

        private final int id;
        private final String name;
        private final Enums.Status status;

        public SpecializationDTO(int id, String name, Enums.Status status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Enums.Status getStatus() {
            return status;
        }
    }

    // Fetches ALL specializations for management purposes
    public List<SpecializationDTO> getSpecializationsForClinic(int clinicId) {
        List<SpecializationDTO> list = new ArrayList<>();
        // 👇 Select the new status column
        String sql = "SELECT specialization_id, name, status FROM specializations WHERE clinic_id = ? ORDER BY name ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new SpecializationDTO(rs.getInt("specialization_id"), rs.getString("name"), Enums.Status.valueOf(rs.getString("status"))));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch specializations for clinic: " + clinicId, e);
        }
        return list;
    }

    public List<String> getActiveSpecializationsForClinic(int clinicId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM specializations WHERE clinic_id = ? AND status = 'Active' ORDER BY name ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch active specializations for clinic: " + clinicId, e);
        }
        return list;
    }

    public boolean addSpecialization(int clinicId, String name) {
        String sql = "INSERT INTO specializations (clinic_id, name) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            pst.setString(2, name);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to add specialization '" + name + "' for clinic: " + clinicId, e);
            return false;
        }
    }

    public boolean updateSpecialization(int specializationId, String newName,
                                        Enums.Status newStatus) {
        String sql = "UPDATE specializations SET name = ?, status = ? WHERE specialization_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, newName);
            pst.setString(2, newStatus.name());
            pst.setInt(3, specializationId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update specialization with ID: " + specializationId, e);
            return false;
        }
    }

     public boolean deleteSpecialization(int specializationId) {
        String sql = "DELETE FROM specializations WHERE specialization_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, specializationId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to delete specialization with ID: " + specializationId, e);
            return false;
        }
    }
}
