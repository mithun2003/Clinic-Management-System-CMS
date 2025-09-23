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

    private static final Dimension NORMAL_CARD_SIZE = new Dimension(240, 180);
    private static final Dimension MAX_CARD_SIZE = new Dimension(545, 200);

    public HomePagePanel(SuperAdmin sa) {
        this.HomePagePanelLoads(sa);
    }

    public void HomePagePanelLoads(SuperAdmin sa) {
        this.loggedInSuperAdmin = sa;
        this.clinicDAO = new ClinicDAO();
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        // === Main Content Panel ===
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

// Constraints for GridBag
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 40, 0);

// Add Welcome Label
        // === Welcome Header ===
        JLabel welcomeLabel = new JLabel("Welcome back, " + loggedInSuperAdmin.getName() + "!", JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0, 102, 102));

        gbc.gridy++;

        // === Stats Cards Panel ===
//        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 60));
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20)); // 1 row, 3 columns

        statsPanel.setBackground(Color.WHITE);

// Create your stat cards
        totalClinicsCard = new StatCardPanel("Total Clinics", "🏥");
        activeClinicsCard = new StatCardPanel("Active Clinics", "✔️");
        totalUsersCard = new StatCardPanel("Total Staff Users", "👥");

// Make each card a fixed size
        totalClinicsCard.setPreferredSize(NORMAL_CARD_SIZE);
        activeClinicsCard.setPreferredSize(NORMAL_CARD_SIZE);
        totalUsersCard.setPreferredSize(NORMAL_CARD_SIZE);
// Add cards into statsPanel
        statsPanel.add(totalClinicsCard);
        statsPanel.add(activeClinicsCard);
        statsPanel.add(totalUsersCard);

// === Center Aligner Panel ===
        JPanel centerAligner = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        centerAligner.setBackground(Color.WHITE);
        centerAligner.add(statsPanel);

// === Add Components to Main Panel ===
        add(welcomeLabel, BorderLayout.NORTH);
        contentPanel.add(statsPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
        // 👇 Add resize listener to auto-resize cards
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Dimension size = getSize();
                boolean isLarge = size.width > 900; // threshold for "full screen"

                Dimension newSize = isLarge ? MAX_CARD_SIZE : NORMAL_CARD_SIZE;
                totalClinicsCard.setPreferredSize(newSize);
                activeClinicsCard.setPreferredSize(newSize);
                totalUsersCard.setPreferredSize(newSize);

                statsPanel.revalidate(); // refresh layout
                statsPanel.repaint();
            }
        });
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
