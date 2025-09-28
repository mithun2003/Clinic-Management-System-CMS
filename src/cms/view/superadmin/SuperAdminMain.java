import cms.view.login.SuperAdminLoginView;
import javax.swing.SwingUtilities;

public class SuperAdminMain {

    public static void main(String[] args) {
        // This is the starting point for the super admin application.
        SwingUtilities.invokeLater(() -> {
            new SuperAdminLoginView().setVisible(true);
        });
    }
}
