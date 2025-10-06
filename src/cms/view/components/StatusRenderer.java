package cms.view.components;

import cms.model.entities.Enums;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected,
            boolean hasFocus,
            int row, int column) {
        // Call the parent method first
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Default appearance for non-status or null values
        setBackground(table.getBackground());
        setForeground(table.getForeground());
        setHorizontalAlignment(SwingConstants.CENTER);

        // Check if the value is a Status enum
        if (value instanceof Enums.Status status) {
            // Apply styling based on the status value
            switch (status) {
                case Active:
                    setBackground(new Color(34, 139, 34)); // Darker Green
                    setForeground(Color.WHITE);
                    setText("Active");
                    break;
                case Inactive:
                case Suspended:
                case Blocked:
                    setBackground(new Color(220, 20, 60)); // Crimson Red
                    setForeground(Color.WHITE);
                    // You can customize the text for each
                    setText(status.name()); // Will show "Inactive", "Suspended", or "Blocked"
                    break;
                default:
                    // Fallback for any other status you might add
                    setBackground(Color.LIGHT_GRAY);
                    setForeground(Color.BLACK);
                    break;
            }
        } else if (value instanceof Enums.AppointmentStatus status) {
            switch (status) {
                case Scheduled:
                    setBackground(new Color(255, 193, 7)); // Yellow
                    setForeground(Color.WHITE);
                    setText(status.name());
                    break;
                case Completed:
                    setBackground(new Color(34, 139, 34)); // Darker Green
                    setForeground(Color.WHITE);
                    setText(status.name());
                    break;
                case Cancelled:
                    setBackground(new Color(220, 20, 60)); // Crimson Red
                    setForeground(Color.WHITE);
                    setText(status.name());
                    break;

                default:
                    break;
            }

        }
        return this;
    }
}
