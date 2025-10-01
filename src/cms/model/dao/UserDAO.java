package cms.model.dao;

import cms.controller.AuthResult;
import cms.model.database.DBConnection;
import cms.model.entities.Clinic;
import cms.model.entities.User;
import cms.utils.LoggerUtil;
import cms.utils.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDAO {

    // Validate login and return User object if found
    public AuthResult validateLogin(String clinicCode, String username,
                                    String password) {
        String sql = "SELECT u.user_id, u.clinic_id, u.name, u.username, u.password, u.role, u.status, "
                     + "c.name AS clinic_name, c.code AS clinic_code, c.address, c.phone, c.status AS clinic_status "
                     + "FROM users u "
                     + "JOIN clinics c ON u.clinic_id = c.clinic_id "
                     + "WHERE c.code = ? "
                     + "AND u.username = ? "
                     + "AND u.status = 'Active' "
                     + "AND c.status = 'Active'";

        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, clinicCode);
            pst.setString(2, username);

            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                // No user found, so it's a clear case of invalid credentials.
                return new AuthResult(AuthResult.AuthStatus.CLINIC_NOT_FOUND, clinicCode);
            }

            String storedHash = rs.getString("password");
            if (!PasswordUtils.checkPassword(password, storedHash)) {
                // Password did not match.
                return new AuthResult(AuthResult.AuthStatus.INVALID_CREDENTIALS);
            }

            String clinicStatusStr = rs.getString("clinic_status");
            if (!"Active".equalsIgnoreCase(clinicStatusStr)) {
                return new AuthResult(AuthResult.AuthStatus.CLINIC_SUSPENDED);
            }

            String userStatusStr = rs.getString("status"); // Assuming 'status' in 'users' table
            if (!"Active".equalsIgnoreCase(userStatusStr)) {
                return new AuthResult(AuthResult.AuthStatus.USER_BLOCKED);
            }

            // 1. Create a Clinic object from the ResultSet
            Clinic clinic = new Clinic();
            clinic.setClinicId(rs.getInt("clinic_id"));
            clinic.setClinicCode(rs.getString("clinic_code"));
            clinic.setClinicName(rs.getString("clinic_name"));
            clinic.setAddress(rs.getString("address")); // Set other details too
            clinic.setPhone(rs.getString("phone"));
            clinic.setStatus(Clinic.Status.valueOf(rs.getString("clinic_status")));

            // 2. Create the User object
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setClinicId(rs.getInt("clinic_id"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setStatus(User.Status.valueOf(rs.getString("status")));

            // Convert role string to enum
            user.setRole(User.Role.valueOf(rs.getString("role").toUpperCase()));

            user.setClinic(clinic);

            return new AuthResult(user);

        } catch (Exception e) {
            LoggerUtil.logError("Failed to validate login for user: " + username, e);
            return new AuthResult(AuthResult.AuthStatus.INVALID_CREDENTIALS);

        }
    }

    // Add this method to get the total number of staff users
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) AS total_users FROM users";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total_users");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total user count.", e);
        }
        return 0;
    }

    // Add this method to count staff members within a specific clinic
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

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (clinic_id, name, username, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, user.getClinicId());
            pst.setString(2, user.getName());
            pst.setString(3, user.getUsername());
            pst.setString(4, user.getPassword()); // Assumes password is ALREADY HASHED
            pst.setString(5, user.getRole().name()); // Convert enum to string
            return pst.executeUpdate() > 0;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // This is a specific, expected error (duplicate username), so we log it as a warning.
            LoggerUtil.logWarning("Attempted to insert a duplicate username: " + user.getUsername() + " for clinic ID: " + user.getClinicId());
            return false;
        } catch (Exception e) {
            // This is an unexpected database error.
            LoggerUtil.logError("Failed to add user: " + user.getUsername(), e);
            return false;
        }
    }

    // New method to update an existing user
    public boolean updateUser(User user) {
        // Note: This example doesn't update the password. A separate "reset password"
        // flow is usually better.
        String sql = "UPDATE users SET name = ?, username = ?, role = ?, status = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, user.getName());
            pst.setString(2, user.getUsername());
            pst.setString(3, user.getRole().name());
            pst.setString(4, user.getStatus().name());
            pst.setInt(5, user.getUserId());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update status for user ID: " + user.getUserId(), e);
            return false;
        }
    }

    // New method to delete a user
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to delete user with ID: " + userId, e);
            return false;
        }
    }

    // You already have getUsersByClinicId for the list
    // Let's add pagination to it
    public List<User> getPaginatedUsersByClinicId(int clinicId, int page,
                                                  int pageSize) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, name, username, role, status, created_at, updated_at FROM users WHERE clinic_id = ? AND role != 'ADMIN' ORDER BY created_at DESC LIMIT ? OFFSET ?";
        int offset = ( page - 1 ) * pageSize;
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            pst.setInt(2, pageSize);
            pst.setInt(3, offset);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setStatus(User.Status.valueOf(rs.getString("status")));
                userList.add(user);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch paginated users for clinic ID: " + clinicId, e);
            // On failure, return an empty list to prevent the UI from crashing.
            return Collections.emptyList();
        }
        return userList;
    }

    /**
     * Updates ONLY the password for a specific user.
     *
     * @param userId The ID of the user to update.
     * @param newHashedPassword The new, already hashed password.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUserPassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, newHashedPassword);
            pst.setInt(2, userId);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update password for user ID: " + userId, e);
            return false;
        }
    }
}
