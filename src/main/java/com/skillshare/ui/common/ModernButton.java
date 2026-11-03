package com.skillshare.ui.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Modern rounded button with hover effects and multiple color variants.
 */
public class ModernButton extends JButton {

    public enum Style {
        PRIMARY, SECONDARY, DANGER, SUCCESS, OUTLINE, SIDEBAR
    }

    private final Style style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 8;

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        initButton();
    }

    public ModernButton(String text, Style style, int cornerRadius) {
        super(text);
        this.style = style;
        this.cornerRadius = cornerRadius;
        initButton();
    }

    private void initButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UITheme.FONT_BODY_BOLD);

        if (style == Style.SIDEBAR) {
            setHorizontalAlignment(LEADING);
            setPreferredSize(new Dimension(210, 42));
            setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        } else {
            setPreferredSize(new Dimension(getPreferredSize().width + 24, 38));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        Color bgColor;
        Color textColor;
        Color borderColor = null;

        if (!isEnabled()) {
            bgColor = new Color(241, 245, 249);
            textColor = new Color(148, 163, 184);
        } else {
            switch (style) {
                case PRIMARY:
                    bgColor = isPressed ? UITheme.PRIMARY_HOVER.darker() :
                              (isHovered ? UITheme.PRIMARY_HOVER : UITheme.PRIMARY);
                    textColor = Color.WHITE;
                    break;
                case SUCCESS:
                    bgColor = isPressed ? UITheme.SUCCESS.darker() :
                              (isHovered ? new Color(5, 150, 105) : UITheme.SUCCESS);
                    textColor = Color.WHITE;
                    break;
                case DANGER:
                    bgColor = isPressed ? UITheme.DANGER.darker() :
                              (isHovered ? new Color(220, 38, 38) : UITheme.DANGER);
                    textColor = Color.WHITE;
                    break;
                case SECONDARY:
                    bgColor = isPressed ? new Color(203, 213, 225) :
                              (isHovered ? new Color(226, 232, 240) : new Color(241, 245, 249));
                    textColor = UITheme.TEXT_DARK;
                    break;
                case OUTLINE:
                    bgColor = isPressed ? UITheme.PRIMARY_LIGHT.darker() :
                              (isHovered ? UITheme.PRIMARY_LIGHT : Color.WHITE);
                    textColor = UITheme.PRIMARY;
                    borderColor = isHovered ? UITheme.PRIMARY : new Color(203, 213, 225);
                    break;
                case SIDEBAR:
                default:
                    bgColor = isPressed ? UITheme.SIDEBAR_ACTIVE :
                              (isHovered ? UITheme.SIDEBAR_HOVER : new Color(0, 0, 0, 0));
                    textColor = isHovered ? Color.WHITE : UITheme.SIDEBAR_TEXT;
                    break;
            }
        }

        // Draw background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Draw border if needed
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        setForeground(textColor);
        g2.dispose();

        super.paintComponent(g);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
