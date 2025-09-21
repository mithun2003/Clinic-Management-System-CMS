package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.SuperAdmin;
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
            e.printStackTrace();
        }
        return null;
    }
}
