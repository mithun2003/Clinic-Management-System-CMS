package cms.view.components; // Or cms.view.components

import cms.utils.FontUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * A centralized utility class for creating and styling common UI components
 * to ensure a consistent look and feel across the application.
 */
public final class UIStyler {

    // Private constructor to prevent instantiation of this utility class.
    private UIStyler() {
    }

    /**
     * Styles a JButton with a solid background color and a hover effect.
     *
     * @param button The button to style.
     * @param color  The base color for the button.
     */
    public static void styleButton(JButton button, Color color) {
        button.setFont(FontUtils.getEmojiFont(Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color darker = color.darker();

        // This is a simple way to handle hover without stacking listeners
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darker);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    /**
     * Creates a styled StatCardPanel nested class(with static), which is a reusable
     * component for dashboards.
     * This panel encapsulates a large value label and a title with an icon.
     */
    public static class StatCardPanel extends JPanel {
        private final JLabel valueLabel;

        public StatCardPanel(String title, String icon) {
            setLayout(new BorderLayout(0, 5)); // Added vertical gap
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    new EmptyBorder(15, 20, 15, 20)));
            setBackground(new Color(248, 249, 250));
            setPreferredSize(new Dimension(225, 120)); // Give it a consistent size

            valueLabel = new JLabel("0", JLabel.LEFT);
            valueLabel.setFont(FontUtils.getUiFont(Font.BOLD, 36));
            valueLabel.setForeground(new Color(50, 50, 50));

            JLabel titleLabel = new JLabel(icon + " " + title, JLabel.LEFT);
            titleLabel.setFont(FontUtils.getEmojiFont(Font.PLAIN, 16));
            titleLabel.setForeground(Color.GRAY);

            add(valueLabel, BorderLayout.CENTER);
            add(titleLabel, BorderLayout.SOUTH); // Swapped for better alignment
        }

        public void setValue(String value) {
            valueLabel.setText(value);
        }
    }

    /**
     * Applies a consistent, modern theme to a JTable.
     *
     * @param table The JTable to be styled.
     */
    public static void styleTable(JTable table) {
        styleTable(table, 30); // Default row height
    }

    public static void styleTable(JTable table, int rowHeight) {
        // --- General Table Styling ---
        table.setRowHeight(rowHeight);
        table.setFont(FontUtils.getUiFont(Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // table.setGridColor(new Color(220, 220, 220)); // Light gray grid lines
        // table.setShowGrid(true);

        // --- Header Styling ---
        JTableHeader header = table.getTableHeader();
        header.setFont(FontUtils.getUiFont(Font.BOLD, 14));
        header.setBackground(new Color(0, 102, 102)); // Teal background
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false); // Prevent users from dragging columns
        header.setBorder(BorderFactory.createLineBorder(new Color(0, 80, 80))); // A subtle border
    }

    /**
     * Center-aligns the text in specified columns of a JTable.
     *
     * @param table         The JTable to modify.
     * @param columnIndices A varargs array of column indices to center.
     */
    public static void centerAlignColumns(JTable table, int... columnIndices) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int index : columnIndices) {
            if (index >= 0 && index < table.getColumnCount()) {
                table.getColumnModel().getColumn(index).setCellRenderer(centerRenderer);
            }
        }
    }

    /**
     * Right-aligns the text in specified columns of a JTable.
     *
     * @param table         The JTable to modify.
     * @param columnIndices A varargs array of column indices to center.
     */
    public static void rightAlignColumns(JTable table, int... columnIndices) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        for (int index : columnIndices) {
            if (index >= 0 && index < table.getColumnCount()) {
                table.getColumnModel().getColumn(index).setCellRenderer(centerRenderer);
            }
        }
    }

    /**
     * Sets the preferred width for specified columns of a JTable.
     *
     * @param table  The JTable to modify.
     * @param widths A map where the key is the column index and the value is the
     *               preferred width.
     */
    public static void setColumnWidths(JTable table, Map<Integer, Integer> widths) {
        for (Map.Entry<Integer, Integer> entry : widths.entrySet()) {
            int index = entry.getKey();
            int width = entry.getValue();
            if (index >= 0 && index < table.getColumnCount()) {
                table.getColumnModel().getColumn(index).setPreferredWidth(width);
            }
        }
    }

    public static void hideColumn(JTable table, int... columnIndices) {
        for (int index : columnIndices) {
            if (index >= 0 && index < table.getColumnCount()) {
                table.getColumnModel().getColumn(index).setMinWidth(0);
                table.getColumnModel().getColumn(index).setMaxWidth(0);
                table.getColumnModel().getColumn(index).setWidth(0);
            }
        }
    }

    public static void setStatusColumn(JTable table, int... columnIndices) {
        for (int index : columnIndices) {
            if (index >= 0 && index < table.getColumnCount()) {
                table.getColumnModel().getColumn(index).setCellRenderer(new StatusRenderer());
            }
        }
    }

    /**
     * Applies a custom renderer for date/time columns.
     * 
     * @param table         The JTable to modify.
     * @param format        The date/time format string (e.g., "yyyy-MM-dd HH:mm").
     * @param columnIndices The column indices to apply the renderer to.
     */
    public static void HanldeDateTimeColumn(JTable table, String format, int... columnIndices) {
        for (int index : columnIndices) {
            if (index >= 0 && index < table.getColumnCount()) {
                table.getColumnModel().getColumn(index).setCellRenderer(new DateTimeRenderer(format));
            }
        }
    }

    // You can add convenience methods for common formats
    public static void setDateTimeColumn(JTable table, int... columnIndices) {
        HanldeDateTimeColumn(table, "yyyy-MM-dd HH:mm", columnIndices);
    }

    public static void setDateOnlyColumn(JTable table, int... columnIndices) {
        HanldeDateTimeColumn(table, "yyyy-MM-dd", columnIndices);
    }

    public static void setTimeOnlyColumn(JTable table, int... columnIndices) {
        HanldeDateTimeColumn(table, "HH:mm", columnIndices);
    }

    /**
     * A flexible TableCellRenderer for displaying various temporal objects
     * (LocalDateTime, LocalDate, LocalTime) in a user-friendly, centered format.
     */
    public static class DateTimeRenderer extends DefaultTableCellRenderer {

        private final DateTimeFormatter formatter;

        public DateTimeRenderer() {
            this("yyyy-MM-dd HH:mm");
        }

        /**
         * Constructor that accepts a custom format pattern.
         * 
         * @param format The date/time format pattern.
         */
        public DateTimeRenderer(String format) {
            super();
            this.formatter = DateTimeFormatter.ofPattern(format);
            setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setText("");

            if (value instanceof LocalDateTime) {
                setText(((LocalDateTime) value).format(formatter));
            } else if (value instanceof LocalDate) {
                setText(((LocalDate) value).format(formatter));
            } else if (value instanceof LocalTime) {
                setText(((LocalTime) value).format(formatter));
            } else if (value != null) {
                // Fallback for unexpected types
                setText(value.toString());
            }

            return this;
        }
    }

}