package com.skillshare.ui.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Modern rounded container panel with subtle card borders.
 */
public class ModernCard extends JPanel {
    private int cornerRadius = 12;
    private Color backgroundColor = UITheme.CARD_BG;
    private Color borderColor = UITheme.CARD_BORDER;

    public ModernCard() {
        this(12);
    }

    public ModernCard(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    public ModernCard(LayoutManager layout) {
        this(layout, 12);
    }

    public ModernCard(LayoutManager layout, int cornerRadius) {
        super(layout);
        this.cornerRadius = cornerRadius;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    public void setCardBackground(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
