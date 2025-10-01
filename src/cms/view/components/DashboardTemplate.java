package cms.view.components;

import cms.utils.TitleBarManager;
import javax.swing.*;
import java.awt.*;

public abstract class DashboardTemplate extends JFrame {

    // These are protected so child classes can add their specific buttons
    protected JPanel sidebar;
    protected JPanel mainNavPanel; // The panel for Home, Manage, etc.
    protected JButton btnLogout;   // The logout button is also common

    protected JPanel contentPanel;
    protected CardLayout cardLayout;

    public DashboardTemplate() {
        // --- Frame and Shell Setup ---
        setUndecorated(true);
        setSize(1000, 665);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Create the FULL sidebar structure here ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0, 128, 128));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        int sidebarWidth = 220;
        sidebar.setPreferredSize(new Dimension(sidebarWidth, 0));
        add(sidebar, BorderLayout.WEST);

        // --- Create the inner panel for main navigation buttons ---
        mainNavPanel = new JPanel();
        mainNavPanel.setLayout(new GridLayout(0, 1, 0, 15));
        mainNavPanel.setOpaque(false); // Make it transparent
        // Set a max size to prevent it from stretching too tall
//        mainNavPanel.setMaximumSize(new Dimension(sidebarWidth,0)); 

        // --- Create the common logout button ---
        btnLogout = new SidebarButton("🚪 Logout", true, SidebarButton.Align.CENTER);

        // --- Assemble the sidebar structure IN THE CORRECT ORDER ---
        sidebar.add(Box.createVerticalGlue()); // Top glue
        sidebar.add(mainNavPanel);             // The (currently empty) main nav panel
        sidebar.add(Box.createVerticalGlue()); // Middle glue
        sidebar.add(btnLogout);                // Logout button at the bottom
        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); // Bottom padding

        // --- Create Empty Content Area ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
    }

    // --- Post-Initialization Method (this remains the same) ---
    protected final void buildDashboard(String title) {
        JPanel titleBar = TitleBarManager.createTitleBar(this, title);
        add(titleBar, BorderLayout.NORTH);

        addSidebarButtons(); // This will now just add buttons to the existing mainNavPanel
        addContentPanels();
        addListeners();
        setDefaultView();
    }

    // Abstract methods to be implemented by child classes
    protected abstract void addSidebarButtons();
    protected abstract void addContentPanels();
    protected abstract void addListeners();
    protected abstract void setDefaultView();
}