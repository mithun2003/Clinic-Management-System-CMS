package cms.view.clinic;


import cms.view.login.ClinicLoginView;
import javax.swing.SwingUtilities;

public class StaffMain {
    public static void main(String[] args) {
        // This is the starting point for the staff application.
        SwingUtilities.invokeLater(() -> {
            new ClinicLoginView().setVisible(true);
        });
    }
}