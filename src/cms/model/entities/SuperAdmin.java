package cms.model.entities;

public class SuperAdmin {
    private int superAdminId;
    private String name;
    private String username;
    private String password;

    public int getSuperAdminId() { return superAdminId; }
    public void setSuperAdminId(int superAdminId) { this.superAdminId = superAdminId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}