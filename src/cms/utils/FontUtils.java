package cms.utils;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for creating and manipulating Font objects.
 * Provides methods for applying common typographic styles like letter spacing.
 */
public class FontUtils {

    // Define common font names as constants for consistency
    public static final String FONT_FAMILY_UI = "Arial";
    public static final String FONT_FAMILY_EMOJI = "Segoe UI Emoji";

    /**
     * Creates a standard UI font with a specified style and size.
     *
     * @param style Font.PLAIN, Font.BOLD, etc.
     * @param size  The font size.
     * @return A new Font object.
     */
    public static Font getUiFont(int style, int size) {
        return new Font(FONT_FAMILY_UI, style, size);
    }

    /**
     * Creates a font capable of rendering emojis.
     *
     * @param style Font.PLAIN, Font.BOLD, etc.
     * @param size  The font size.
     * @return A new Font object.
     */
    public static Font getEmojiFont(int style, int size) {
        return new Font(FONT_FAMILY_EMOJI, style, size);
    }

    /**
     * Applies letter spacing (tracking) to a given font.
     * This makes text, especially in titles and buttons, look more professional.
     *
     * @param baseFont      The original font.
     * @param trackingValue The letter spacing value (e.g., 0.05f for 5% spacing).
     * @return A new Font object with the letter spacing applied.
     */
    public static Font applyLetterSpacing(Font baseFont, float trackingValue) {
        // Create a new, type-safe map from the original font's attributes
        Map<TextAttribute, Object> attributes = new HashMap<>(baseFont.getAttributes());
        
        // Add the tracking (letter spacing) attribute
        attributes.put(TextAttribute.TRACKING, trackingValue);
        
        // Return a new font derived from these modified attributes
        return baseFont.deriveFont(attributes);
    }

    /**
     * A convenience method to create a UI font with letter spacing in one step.
     *
     * @param style         Font.PLAIN, Font.BOLD, etc.
     * @param size          The font size.
     * @param trackingValue The letter spacing value.
     * @return A new, styled Font object.
     */
    public static Font createSpacedFont(int style, int size, float trackingValue) {
        Font baseFont = getUiFont(style, size);
        return applyLetterSpacing(baseFont, trackingValue);
    }
}