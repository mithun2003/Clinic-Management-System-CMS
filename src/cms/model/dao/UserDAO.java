package cms.model.dao;

import cms.controller.AuthResult;
import cms.model.database.DBConnection;
import cms.model.entities.Clinic;
import cms.model.entities.Enums;
import cms.model.entities.User;
import cms.utils.LoggerUtil;
import cms.utils.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDAO {

    // Validate login and return User object if found
    public AuthResult validateLogin(String clinicCode, String username, String password) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();

            // --- STEP 1: Validate the Clinic First ---
            String clinicSql = "SELECT clinic_id, name, address, phone, status FROM clinics WHERE code = ?";
            Clinic clinic = null;
            try (PreparedStatement pstClinic = con.prepareStatement(clinicSql)) {
                pstClinic.setString(1, clinicCode);
                ResultSet rsClinic = pstClinic.executeQuery();

                if (!rsClinic.next()) {
                    // Clinic with the given code does not exist.
                    return new AuthResult(AuthResult.AuthStatus.CLINIC_NOT_FOUND, clinicCode);
                }

                // Clinic exists, now check its status.
                String clinicStatusStr = rsClinic.getString("status");
                if (!"Active".equalsIgnoreCase(clinicStatusStr)) {
                    return new AuthResult(AuthResult.AuthStatus.CLINIC_SUSPENDED);
                }

                // If we are here, the clinic is valid and active. Build the Clinic object.
                clinic = new Clinic();
                clinic.setClinicId(rsClinic.getInt("clinic_id"));
                clinic.setClinicCode(clinicCode); // We already have this
                clinic.setClinicName(rsClinic.getString("name"));
                clinic.setAddress(rsClinic.getString("address"));

                clinic.setPhone(rsClinic.getString("phone"));
                clinic.setStatus(Enums.Status.valueOf(clinicStatusStr));
            }

            // --- STEP 2: Validate the User Second ---
            String userSql = "SELECT user_id, name, username, password, role, status FROM users WHERE clinic_id = ? AND username = ?";
            try (PreparedStatement pstUser = con.prepareStatement(userSql)) {
                pstUser.setInt(1, clinic.getClinicId());
                pstUser.setString(2, username);
                ResultSet rsUser = pstUser.executeQuery();

                if (!rsUser.next()) {
                    // The clinic was valid, but this username doesn't exist in it.
                    return new AuthResult(AuthResult.AuthStatus.INVALID_CREDENTIALS);
                }

                // User exists, now check their status.
                String userStatusStr = rsUser.getString("status");
                if (!"Active".equalsIgnoreCase(userStatusStr)) {
                    return new AuthResult(AuthResult.AuthStatus.USER_BLOCKED);
                }

                // Finally, check the password.
                String storedHash = rsUser.getString("password");
                if (!PasswordUtils.checkPassword(password, storedHash)) {
                    return new AuthResult(AuthResult.AuthStatus.INVALID_CREDENTIALS);
                }

                // --- ALL CHECKS PASSED: SUCCESS ---
                User user = new User();
                user.setUserId(rsUser.getInt("user_id"));
                user.setClinicId(clinic.getClinicId());
                user.setName(rsUser.getString("name"));
                user.setUsername(rsUser.getString("username"));
                user.setPassword(storedHash); // Store the hash
                user.setRole(Enums.Role.valueOf(rsUser.getString("role").toUpperCase()));
                user.setStatus(Enums.Status.valueOf(userStatusStr));

                user.setClinic(clinic); // Set the relationship

                return new AuthResult(user);
            }

        } catch (Exception e) {
            LoggerUtil.logError("Failed to validate login for user: " + username, e);
            // Return a generic error to the UI for security in case of unexpected
            // exceptions
            return new AuthResult(AuthResult.AuthStatus.INVALID_CREDENTIALS);
        } finally {
            // Ensure the connection is closed in a single-connection model
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    LoggerUtil.logError("Failed to close connection in validateLogin.", e);
                }
            }
        }
    }

    // Add this method to get the total number of staff users
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) AS total_users FROM users";
        try (Connection con = DBConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
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

    public int addUser(User user) {
        String sql = "INSERT INTO users (clinic_id, name, username, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, user.getClinicId());
            pst.setString(2, user.getName());
            pst.setString(3, user.getUsername());
            pst.setString(4, user.getPassword()); // Assumes password is ALREADY HASHED
            pst.setString(5, user.getRole().name()); // Convert enum to string
            pst.setString(6, (user.getStatus() != null) ? user.getStatus().name() : "Active");
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new user_id
                    }
                }
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // This is a specific, expected error (duplicate username), so we log it as a
            // warning.
            LoggerUtil.logWarning("Attempted to insert a duplicate username: " + user.getUsername() + " for clinic ID: "
                    + user.getClinicId());
        } catch (Exception e) {
            // This is an unexpected database error.
            LoggerUtil.logError("Failed to add user: " + user.getUsername(), e);
        }
        return -1;
    }

    // New method to update an existing user
    public boolean updateUser(User user) {
        // Note: This example doesn't update the password. A separate "reset password"
        // flow is usually better.
        String sql = "UPDATE users SET name = ?, username = ?, role = ?, status = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            System.out.println(user.toString());
            System.out.println(user.getStatus());
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
        String sql = "SELECT u.user_id, u.name, u.username, u.role, u.status, d.specialization, d.status " +
                "FROM users u " +
                "LEFT JOIN doctors d ON u.user_id = d.user_id " +
                "WHERE u.clinic_id = ? AND u.role != 'ADMIN' " +
                "ORDER BY u.created_at DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
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
                user.setRole(Enums.Role.valueOf(rs.getString("role")));
                user.setStatus(Enums.Status.valueOf(rs.getString("status")));

                user.setSpecialization(rs.getString("specialization"));

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
     * @param userId            The ID of the user to update.
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
