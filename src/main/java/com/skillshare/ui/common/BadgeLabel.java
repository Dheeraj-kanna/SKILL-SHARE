package com.skillshare.ui.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * Modern pill badge label for status (Pending, Accepted, Rejected) or skill tags.
 */
public class BadgeLabel extends JLabel {
    private Color badgeBackground;
    private Color badgeForeground;
    private int cornerRadius = 12;

    public BadgeLabel(String text, Color bg, Color fg) {
        super(text);
        this.badgeBackground = bg;
        this.badgeForeground = fg;
        initBadge();
    }

    private void initBadge() {
        setOpaque(false);
        setFont(UITheme.FONT_SMALL_BOLD);
        setForeground(badgeForeground);
        setHorizontalAlignment(CENTER);
        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
    }

    public static BadgeLabel createStatusBadge(String status) {
        if ("Accepted".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
            return new BadgeLabel(status, UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_TEXT);
        } else if ("Rejected".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
            return new BadgeLabel(status, UITheme.DANGER_LIGHT, UITheme.DANGER_TEXT);
        } else if ("Pending".equalsIgnoreCase(status)) {
            return new BadgeLabel(status, UITheme.WARNING_LIGHT, UITheme.WARNING_TEXT);
        } else if ("Scheduled".equalsIgnoreCase(status)) {
            return new BadgeLabel(status, UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
        }
        return new BadgeLabel(status, new Color(241, 245, 249), UITheme.TEXT_MUTED);
    }

    public static BadgeLabel createSkillBadge(String skillName, boolean isTeach) {
        if (isTeach) {
            return new BadgeLabel(skillName, new Color(224, 231, 255), new Color(67, 56, 202));
        } else {
            return new BadgeLabel(skillName, new Color(243, 232, 255), new Color(126, 34, 206));
        }
    }

    public void setColors(Color bg, Color fg) {
        this.badgeBackground = bg;
        this.badgeForeground = fg;
        setForeground(fg);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        g2.setColor(badgeBackground);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
