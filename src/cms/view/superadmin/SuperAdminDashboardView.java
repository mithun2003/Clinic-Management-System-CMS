package cms.view.superadmin;

import cms.model.entities.SuperAdmin;
import cms.utils.TitleBarManager;
import javax.swing.*;
import java.awt.*;

public class SuperAdminDashboardView extends JFrame {

    private final SuperAdmin loggedInSuperAdmin;

    // Sidebar buttons
    private final SidebarButton btnHome, btnManageClinics, btnReports, btnLogout;

    // Content switching
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private int mouseX, mouseY;

    public SuperAdminDashboardView(SuperAdmin sa) {
        this.loggedInSuperAdmin = sa;

        // ✅ Undecorated for modern look
        setUndecorated(true);
        setSize(1000, 665);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main layout
        setLayout(new BorderLayout());

        // === Custom Top Title Bar ===
//        JPanel titleBar = new JPanel(new BorderLayout());
//        titleBar.setBackground(new Color(0, 102, 102));
//        JLabel lblTitle = new JLabel(" Super Admin Dashboard - " + sa.getName());
//        lblTitle.setForeground(Color.WHITE);
//        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
//        titleBar.add(lblTitle, BorderLayout.WEST);
//        // --- Window Control Buttons Panel ---
//        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//        controlPanel.setOpaque(false); // Make it transparent
//
//        // 👇 1. MINIMIZE BUTTON
//        JButton btnMinimize = new JButton("—");
//        styleControlButton(btnMinimize, new Color(0, 102, 102));
//        btnMinimize.addActionListener(e -> setState(JFrame.ICONIFIED));
//
//        // 👇 2. MAXIMIZE/RESTORE BUTTON
//        JButton btnMaximize = new JButton("◻"); // You can use icons later
//        styleControlButton(btnMaximize, new Color(0, 102, 102));
//        btnMaximize.addActionListener(e -> {
//            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
//                setExtendedState(JFrame.NORMAL); // Restore
//            } else {
//                setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize
//            }
//        });
//
//        // 3. CLOSE BUTTON (from your existing code)
//        JButton btnClose = new JButton("X");
//        styleControlButton(btnClose, new Color(220, 53, 69));  // Danger color for close
//
//        // Add listeners for hover effects on the close button
//        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                btnClose.setBackground(new Color(200, 35, 51)); // Darker red on hover
//            }
//
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                btnClose.setBackground(new Color(220, 53, 69)); // Back to normal
//            }
//        });
//        btnClose.addActionListener(e -> System.exit(0));
//
//        // Add buttons to the control panel
//        controlPanel.add(btnMinimize);
//        controlPanel.add(btnMaximize);
//        controlPanel.add(btnClose);
//
        ////        JButton btnClose = new JButton("X");
////        btnClose.setFocusPainted(false);
////        btnClose.setBorderPainted(false);
////        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
////        btnClose.setBackground(new Color(200, 0, 0));
////        btnClose.setForeground(Color.WHITE);
////        btnClose.addActionListener(e -> System.exit(0));
//        titleBar.add(controlPanel, BorderLayout.EAST);
//        add(titleBar, BorderLayout.NORTH);

JPanel titleBar = TitleBarManager.createTitleBar(this, "Super Admin Dashboard - " + sa.getName());
        add(titleBar, BorderLayout.NORTH);

        // === Sidebar ===
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0, 128, 128));
        sidebar.setLayout(new GridLayout(6, 1, 5, 10));

        btnHome = new SidebarButton("🏠 Home");
        btnManageClinics = new SidebarButton("🏥 Manage Clinics");
        btnReports = new SidebarButton("📊 Reports");
        btnLogout = new SidebarButton("🚪 Logout", true, SidebarButton.Align.CENTER);

        int fixedWidth = 200; // choose a suitable width
        sidebar.setPreferredSize(new Dimension(fixedWidth, getHeight()));
        sidebar.setMinimumSize(new Dimension(fixedWidth, 0));
        sidebar.setMaximumSize(new Dimension(fixedWidth, Integer.MAX_VALUE));

        sidebar.add(btnHome);;
        sidebar.add(btnManageClinics);
        sidebar.add(btnReports);
        sidebar.add(new JLabel()); // filler
        sidebar.add(btnLogout);

        btnHome.selectInSidebar();

        add(sidebar, BorderLayout.WEST);

        // === Content Area with CardLayout ===
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        ClinicPanel clinicPanel = new ClinicPanel();
        HomePagePanel homePanel = new HomePagePanel(loggedInSuperAdmin);
        ReportsPanel reportsPanel = new ReportsPanel();

        // Add Panels
        contentPanel.add(homePanel, "Home");
        contentPanel.add(clinicPanel, "ManageClinics");
        contentPanel.add(reportsPanel, "Reports");

        add(contentPanel, BorderLayout.CENTER);

        // === Button Actions ===
        btnHome.addActionListener(e -> {
            cardLayout.show(contentPanel, "Home");
            homePanel.HomePagePanelLoads(loggedInSuperAdmin);
        });
        btnManageClinics.addActionListener(e -> {
            cardLayout.show(contentPanel, "ManageClinics");
            clinicPanel.refreshClinics(1);
        });
        btnReports.addActionListener(e -> {
            cardLayout.show(contentPanel, "Reports");
            reportsPanel.loadReportData();
        });
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(() -> new SuperAdminLoginView().setVisible(true));
                dispose();
            }
        });
    }
}
