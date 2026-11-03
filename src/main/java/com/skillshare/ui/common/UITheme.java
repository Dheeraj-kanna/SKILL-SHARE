package com.skillshare.ui.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.UIManager;

/**
 * Global design tokens, palette, typography, and graphics configuration.
 */
public class UITheme {

    // Primary Colors
    public static final Color PRIMARY = new Color(79, 70, 229);       // #4f46e5 (Indigo)
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202); // #4338ca
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255);// #eef2ff

    // Sidebar & Dark Theme Colors
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);     // #0f172a (Dark Slate/Navy)
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);  // #1e293b
    public static final Color SIDEBAR_ACTIVE = new Color(49, 46, 129);// #312e81
    public static final Color SIDEBAR_TEXT = new Color(226, 232, 240); // #e2e8f0
    public static final Color SIDEBAR_TEXT_MUTED = new Color(148, 163, 184); // #94a3b8

    // Backgrounds & Surface
    public static final Color BG_MAIN = new Color(248, 250, 252);     // #f8fafc
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BORDER = new Color(226, 232, 240); // #e2e8f0
    public static final Color CARD_HOVER_BORDER = new Color(199, 210, 254); // #c7d2fe

    // Text Colors
    public static final Color TEXT_DARK = new Color(15, 23, 42);      // #0f172a
    public static final Color TEXT_BODY = new Color(51, 65, 85);      // #334155
    public static final Color TEXT_MUTED = new Color(100, 116, 139);  // #64748b
    public static final Color TEXT_LIGHT = new Color(248, 250, 252);

    // Accent Colors
    public static final Color SUCCESS = new Color(16, 185, 129);     // #10b981 (Emerald)
    public static final Color SUCCESS_LIGHT = new Color(209, 250, 229);
    public static final Color SUCCESS_TEXT = new Color(6, 95, 70);

    public static final Color WARNING = new Color(245, 158, 11);     // #f59e0b (Amber)
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);
    public static final Color WARNING_TEXT = new Color(146, 64, 14);

    public static final Color DANGER = new Color(239, 68, 68);       // #ef4444 (Rose/Red)
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);
    public static final Color DANGER_TEXT = new Color(153, 27, 27);

    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);

    // Typography
    private static final String FONT_FAMILY = "Segoe UI, -apple-system, BlinkMacSystemFont, Helvetica Neue, Arial, sans-serif";

    public static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font FONT_SECTION = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    public static final Font FONT_STAT_NUM = new Font(Font.SANS_SERIF, Font.BOLD, 28);
    public static final Font FONT_LOGO = new Font(Font.SANS_SERIF, Font.BOLD, 20);

    public static void applyAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static void setupLookAndFeel() {
        try {
            // Try FlatLaf Light if available in classpath
            Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            Object instance = flatLafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel((javax.swing.LookAndFeel) instance);
        } catch (Exception e) {
            try {
                // Fallback to System Look & Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Set global font defaults
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Button.font", FONT_BODY_BOLD);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.font", FONT_BODY_BOLD);
    }
}
