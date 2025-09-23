package cms.utils;

import javax.swing.*;
import java.awt.*;

public class TitleBarManager {

    private static int mouseX, mouseY;

    /**
     * Creates a styled, draggable custom title bar for any top-level window (JFrame or JDialog).
     *
     * @param window The Window (JFrame or JDialog) this title bar will be added to.
     * @param title  The text to display in the title bar.
     * @return A JPanel representing the custom title bar.
     */
    public static JPanel createTitleBar(Window window, String title) {
        // --- Main Title Bar Panel ---
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(0, 102, 102));
        titleBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        // --- Title Label ---
        JLabel lblTitle = new JLabel(" " + title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(lblTitle, BorderLayout.WEST);

        // --- Window Control Buttons Panel ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false);

        // --- Buttons ---
        JButton btnMinimize = new JButton("—");
        JButton btnMaximize = new JButton("◻");
        JButton btnClose = new JButton("X");

        // --- Style and Add Actions ---
        styleControlButton(btnMinimize, new Color(0, 102, 102), new Color(0, 128, 128));
        btnMinimize.addActionListener(e -> {
            if (window instanceof JFrame) {
                ((JFrame) window).setState(JFrame.ICONIFIED);
            }
        });

        styleControlButton(btnMaximize, new Color(0, 102, 102), new Color(0, 128, 128));
        btnMaximize.addActionListener(e -> {
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(JFrame.NORMAL);
                } else {
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            }
        });

        styleControlButton(btnClose, new Color(220, 53, 69), new Color(200, 35, 51));
        // This action works for both JFrame and JDialog
        btnClose.addActionListener(e -> window.dispose());

        // --- Add Buttons to Control Panel (Only for JFrames) ---
        // JDialogs should not have minimize or maximize buttons.
        if (window instanceof JFrame) {
            controlPanel.add(btnMinimize);
            controlPanel.add(btnMaximize);
        }
        controlPanel.add(btnClose);

        titleBar.add(controlPanel, BorderLayout.EAST);

        // Add dragging functionality to the entire title bar
        addDragListeners(window, titleBar);

        return titleBar;
    }

    private static void styleControlButton(JButton btn, Color background, Color hoverBackground) {
        // ... (your existing styling code for buttons) ...
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
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

    private static void addDragListeners(Window window, JComponent component) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
        });
        component.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                window.setLocation(evt.getXOnScreen() - mouseX, evt.getYOnScreen() - mouseY);
            }
        });
    }
}