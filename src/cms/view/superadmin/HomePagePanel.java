package cms.view.superadmin;

import cms.model.dao.ClinicDAO;
import cms.model.dao.UserDAO;
import cms.model.entities.SuperAdmin;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class HomePagePanel extends JPanel {

    private SuperAdmin loggedInSuperAdmin = null;
    private ClinicDAO clinicDAO;
    private UserDAO userDAO;

    // 👇 Keep references to the StatCardPanel components
    private StatCardPanel totalClinicsCard;
    private StatCardPanel activeClinicsCard;
    private StatCardPanel totalUsersCard;
    
    public HomePagePanel(SuperAdmin sa){
        this.HomePagePanelLoads(sa);
    }

    public void HomePagePanelLoads(SuperAdmin sa) {
        this.loggedInSuperAdmin = sa;
        this.clinicDAO = new ClinicDAO();
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // === Welcome Header ===
        JLabel welcomeLabel = new JLabel("Welcome back, " + loggedInSuperAdmin.getName() + "!", JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0, 102, 102));

        // === Stats Cards Panel ===
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        statsPanel.setPreferredSize(new Dimension(700, 140));
;

        // 👇 Create instances of your new StatCardPanel
        totalClinicsCard = new StatCardPanel("Total Clinics", "🏥");
        activeClinicsCard = new StatCardPanel("Active Clinics", "✔️");
        totalUsersCard = new StatCardPanel("Total Staff Users", "👥");

        // 👇 Add the ENTIRE card panel, not just the label
        statsPanel.add(totalClinicsCard);
        statsPanel.add(activeClinicsCard);
        statsPanel.add(totalUsersCard);

        // === Center Aligner Panel (This is the new part) ===
        // This panel uses FlowLayout to center its content.
        JPanel centerAligner = new JPanel(new GridBagLayout());
        centerAligner.setBackground(Color.WHITE); // Match the background
        centerAligner.add(statsPanel);

        // === Add Components to Main Panel ===
        add(welcomeLabel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);

        // Load the data from the database
        refreshData();
    }

    // This method can be called to refresh the stats
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            // 👇 Update the value of each card
            totalClinicsCard.setValue(String.valueOf(clinicDAO.getTotalClinics()));
            activeClinicsCard.setValue(String.valueOf(clinicDAO.getTotalClinics(ClinicDAO.Status.Active)));
            totalUsersCard.setValue(String.valueOf(userDAO.getTotalUserCount()));
        });
    }

    // 👇 The StatCardPanel inner class (or put it in a separate file)
    private class StatCardPanel extends JPanel {

        private JLabel valueLabel;

        public StatCardPanel(String title, String icon) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    new EmptyBorder(15, 20, 15, 20)
            ));
            setBackground(new Color(248, 249, 250));

            valueLabel = new JLabel("0", JLabel.LEFT);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 36));

            JLabel titleLabel = new JLabel(icon + " " + title, JLabel.LEFT);
            titleLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            titleLabel.setForeground(Color.GRAY);

            add(valueLabel, BorderLayout.CENTER);
            add(titleLabel, BorderLayout.NORTH);
        }

        public void setValue(String value) {
            valueLabel.setText(value);
        }
    }
}
