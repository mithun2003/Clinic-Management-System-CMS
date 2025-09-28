package cms.view.clinic;


import cms.view.login.StaffLoginView;
import cms.view.login.StaffLoginView;
import javax.swing.SwingUtilities;

public class StaffMain {
    public static void main(String[] args) {
        // This is the starting point for the staff application.
        SwingUtilities.invokeLater(() -> {
            new StaffLoginView().setVisible(true);
        });
    }
}