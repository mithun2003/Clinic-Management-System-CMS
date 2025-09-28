package cms.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;

/**
 * A custom text field that displays a text-based icon (emoji) on the left and
 * placeholder text when empty. Supports both JTextField and JPasswordField.
 */
public class PlaceholderTextField extends JPanel {

    private final JLabel iconLabel;
    private final JTextComponent textComponent;
    private final String placeholder;
    private final boolean isPassword;

    public PlaceholderTextField(String placeholder, String iconText) {
        this(placeholder, iconText, false); // Default to a regular text field
    }

    public PlaceholderTextField(String placeholder, String iconText, boolean isPassword) {
        this.placeholder = placeholder;
        this.isPassword = isPassword;

        // --- Component Setup ---
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // --- Icon Label (using text/emoji) ---
        this.iconLabel = new JLabel(iconText);
        // Use an emoji-compatible font for the icon
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        iconLabel.setForeground(Color.GRAY);
        add(iconLabel, BorderLayout.WEST);

        // --- Text Field or Password Field ---
        if (isPassword) {
            this.textComponent = new JPasswordField();
        } else {
            this.textComponent = new JTextField();
        }
        textComponent.setOpaque(false);
        textComponent.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 8));
        textComponent.setFont(new Font("Arial", Font.PLAIN, 16));
        add(textComponent, BorderLayout.CENTER);

        // Add placeholder logic
        addPlaceholderFunctionality();
    }

    private void addPlaceholderFunctionality() {
        // ... (The placeholder logic remains exactly the same as before) ...
        showPlaceholder();
        textComponent.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderShowing()) {
                    hidePlaceholder();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    showPlaceholder();
                }
            }
        });
    }

    private boolean isPlaceholderShowing() {
        if (isPassword) {
            return String.valueOf(((JPasswordField) textComponent).getPassword()).equals(placeholder);
        } else {
            return textComponent.getText().equals(placeholder);
        }
    }

    private void showPlaceholder() {
        if (isPassword) {
            ((JPasswordField) textComponent).setEchoChar((char) 0);
        }
        textComponent.setForeground(Color.GRAY);
        textComponent.setText(placeholder);
    }

    private void hidePlaceholder() {
        if (isPassword) {
            ((JPasswordField) textComponent).setEchoChar('•');
        }
        textComponent.setForeground(Color.BLACK);
        textComponent.setText("");
    }

    public String getText() {
        if (isPlaceholderShowing()) {
            return "";
        }
        if (isPassword) {
            return String.valueOf(((JPasswordField) textComponent).getPassword());
        } else {
            return textComponent.getText();
        }
    }
}
