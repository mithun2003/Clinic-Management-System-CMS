package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.User;
import cms.utils.PasswordUtils;

import java.sql.*;

public class UserDAO {

    // Validate login and return User object if found
    public User validateLogin(String clinicCode, String username, String password) {
        String sql = "SELECT u.user_id, u.clinic_id, u.name, u.username, u.password, u.role, "
                + "c.name AS clinic_name, c.code AS clinic_code "
                + "FROM users u "
                + "JOIN clinics c ON u.clinic_id = c.clinic_id "
                + "WHERE c.code = ? "
                + "AND u.username = ? "
                + "AND u.password = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, clinicCode);
            pst.setString(2, username);
            pst.setString(3, PasswordUtils.hashPassword(password));

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setClinicId(rs.getInt("clinic_id"));
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    // Add this method to get the total number of staff users
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) AS total_users FROM users";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total_users");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // In UserDAO.java
public void addUser(User user) {
    String sql = "INSERT INTO users (clinic_id, name, username, password, role) VALUES (?, ?, ?, ?, ?)";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, user.getClinicId());
        pst.setString(2, user.getName());
        pst.setString(3, user.getUsername());
        pst.setString(4, user.getPassword()); // Should already be hashed
        pst.setString(5, user.getRole());
        pst.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
