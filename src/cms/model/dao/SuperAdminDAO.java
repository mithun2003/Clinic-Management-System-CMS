package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.SuperAdmin;
import cms.model.entities.User;
import cms.utils.LoggerUtil;
import cms.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SuperAdminDAO {

    public SuperAdmin validateLogin(String username, String password) {
        String sql = "SELECT * FROM super_admins WHERE username=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                System.out.println("User input: " + password);
                System.out.println("Stored hash: " + storedHash);
                System.out.println("Check result: " + PasswordUtils.checkPassword(password, storedHash));
                if (PasswordUtils.checkPassword(password, storedHash)) {
                    SuperAdmin sa = new SuperAdmin();
                    sa.setSuperAdminId(rs.getInt("super_admin_id"));
                    sa.setName(rs.getString("name"));
                    sa.setUsername(rs.getString("username"));
                    return sa;
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to validate Super Admin login for username: " + username, e);
        }
        return null;
    }

    public void addAdmin(User user) {
        String sql = "INSERT INTO users (clinic_id, name, username, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, user.getClinicId());
            pst.setString(2, user.getName());
            pst.setString(3, user.getUsername());
            pst.setString(4, user.getPassword()); // Should already be hashed
            pst.setString(5, user.getRole().name());
            pst.executeUpdate();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // This is a specific, expected error (duplicate username), so we log it as a warning.
            LoggerUtil.logWarning("Attempted to insert a duplicate username: " + user.getUsername() + " for clinic ID: " + user.getClinicId());
        } catch (Exception e) {
            LoggerUtil.logError("Failed to add user: " + user.getUsername(), e);

        }
    }
}
