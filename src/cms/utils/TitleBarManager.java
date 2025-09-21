package cms.utils;

import javax.swing.*;
import java.awt.*;

public class TitleBarManager {

    private static int mouseX, mouseY;

    /**
     * Creates and returns a styled, drag able custom title bar.
     *
     * @param frame The JFrame this title bar will be added to.
     * @param title The text to display in the title bar.
     * @return A JPanel representing the custom title bar.
     */
    public static JPanel createTitleBar(JFrame frame, String title) {
        // --- Main Title Bar Panel ---
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(0, 102, 102)); // Teal background
        titleBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        // --- Title Label ---
        JLabel lblTitle = new JLabel(" " + title); // Leading space for padding
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(lblTitle, BorderLayout.WEST);

        // --- Window Control Buttons Panel ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false); // Make it transparent

        // --- Buttons ---
        JButton btnMinimize = new JButton("—");
        JButton btnMaximize = new JButton("◻");
        JButton btnClose = new JButton("X");

        // Style the buttons
        styleControlButton(btnMinimize, new Color(0, 102, 102), new Color(0, 128, 128));
        styleControlButton(btnMaximize, new Color(0, 102, 102), new Color(0, 128, 128));
        styleControlButton(btnClose, new Color(220, 53, 69), new Color(200, 35, 51)); // Danger red

        // Add actions to buttons
        btnMinimize.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
        btnMaximize.addActionListener(e -> {
            if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                frame.setExtendedState(JFrame.NORMAL);
            } else {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        btnClose.addActionListener(e -> frame.dispose()); // Use dispose() to close the window

        // Add buttons to the control panel
        controlPanel.add(btnMinimize);
        controlPanel.add(btnMaximize);
        controlPanel.add(btnClose);

        titleBar.add(controlPanel, BorderLayout.EAST);

        // Add dragging functionality to the entire title bar
        addDragListeners(frame, titleBar);

        return titleBar;
    }

    /**
     * A helper method to style the window control buttons.
     */
    private static void styleControlButton(JButton btn, Color background, Color hoverBackground) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
        btn.setBackground(background);
        btn.setForeground(Color.WHITE);
        btn.setMargin(new Insets(0, 15, 0, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverBackground);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(background);
            }
        });
    }

    /**
     * Adds mouse listeners to a component to make its parent frame draggable.
     */
    private static void addDragListeners(JFrame frame, JComponent component) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
        });
        component.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                frame.setLocation(evt.getXOnScreen() - mouseX, evt.getYOnScreen() - mouseY);
            }
        });
    }
}