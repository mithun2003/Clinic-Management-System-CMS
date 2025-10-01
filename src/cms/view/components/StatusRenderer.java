package cms.view.components;

import cms.model.entities.Clinic;
import cms.model.entities.User;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A reusable renderer for displaying Status enums (like Clinic.Status or
 * User.Status) with different colors.
 */
public class StatusRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        // Call the parent method first to set up default rendering
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // Handle null values
        // Determine the type of status and apply color
        switch (value) {
        case null -> {
            setBackground(Color.WHITE);
            setForeground(Color.GRAY);
            return this;
        }
        case Clinic.Status status -> {
            switch (status) {
            case Active -> setBackground(new Color(0, 174, 0)); // Green
            case Suspended -> setBackground(new Color(255, 99, 71)); // Red
            default -> setBackground(Color.WHITE);
            }
        }
        case User.Status status -> {
            switch (status) {
            case Active -> setBackground(new Color(0, 174, 0)); // Green
            case Suspended -> setBackground(new Color(255, 99, 71)); // Red
            default -> setBackground(Color.WHITE);
            }
        }
        default -> {
            setBackground(Color.WHITE);
            setForeground(Color.GRAY);
            return this;
        }
        }
        // Ensure text is white on colored backgrounds
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }
}