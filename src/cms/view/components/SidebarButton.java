package cms.view.components;

import cms.utils.FontUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Custom sidebar button with underline hover/selection effect and optional
 * "danger" variant (for logout).
 */
public class SidebarButton extends JButton {

    private static SidebarButton selectedButton = null;
    private final boolean isDanger;

    // Alignment option (default LEFT)
    public enum Align {
        LEFT, CENTER, RIGHT
    }
    private Align textAlign;

    public SidebarButton(String text) {
        this(text, false, Align.LEFT);
    }

    public SidebarButton(String text, boolean isDanger) {
        this(text, isDanger, isDanger ? Align.CENTER : Align.LEFT);
    }

    public SidebarButton(String text, boolean isDanger, Align align) {
        super(text);
        this.isDanger = isDanger;
        this.textAlign = align;
        initStyles();
    }

    private void initStyles() {
        setFocusPainted(false);
        setBorderPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Font baseFont = FontUtils.getEmojiFont(Font.BOLD, 18);
        setFont(FontUtils.applyLetterSpacing(baseFont, 0.05f));

        // Alignment
        switch (textAlign) {
            case CENTER ->{
                setHorizontalAlignment(SwingConstants.CENTER);
                setAlignmentX(Component.CENTER_ALIGNMENT);
                break;
            }
                
            case RIGHT ->
                setHorizontalAlignment(SwingConstants.RIGHT);
            default ->
                setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (isDanger) {
            setContentAreaFilled(true);
            setOpaque(true);
            setBackground(new Color(220, 53, 69));
            setForeground(Color.WHITE);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(new Color(200, 35, 51));
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setBackground(new Color(220, 53, 69));
                }
            });
        } else {
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(Color.WHITE);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (SidebarButton.this != selectedButton) {
                        repaint();
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (SidebarButton.this != selectedButton) {
                        repaint();
                    }
                }
            });

            addActionListener(_ -> {
                selectInSidebar();
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isDanger && (this == selectedButton || getModel().isRollover())) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics(getFont());

            int textWidth = fm.stringWidth(getText());
            Insets insets = getInsets();
            int textX;

            // Align underline with text alignment
            switch (textAlign) {
                case CENTER ->
                    textX = (getWidth() - textWidth) / 2;
                case RIGHT ->
                    textX = getWidth() - insets.right - textWidth;
                default ->
                    textX = insets.left;
            }

            int textY = getHeight() / 2 + fm.getAscent() / 2 - 2;
            int underlineY = textY + 5; // spacing between text and underline
            g2.fillRect(textX, underlineY, textWidth, this == selectedButton ? 3 : 1);
        }
    }

    // Allow changing alignment dynamically
    public void setTextAlign(Align align) {
        this.textAlign = align;
        switch (align) {
            case CENTER ->
                setHorizontalAlignment(SwingConstants.CENTER);
            case RIGHT ->
                setHorizontalAlignment(SwingConstants.RIGHT);
            default ->
                setHorizontalAlignment(SwingConstants.LEFT);
        }
        repaint();
    }
//
    // Programmatically select this button in the sidebar

    public void selectInSidebar() {
        if (isDanger) {
            return; // don't select/logout visually
        }
        if (selectedButton != null && selectedButton != this) {
            selectedButton.repaint();
        }
        selectedButton = this;
        repaint();
    }
}
