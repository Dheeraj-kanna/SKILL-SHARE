package com.skillshare.ui.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

/**
 * Modern rounded password field with placeholder and focus highlighting.
 */
public class ModernPasswordField extends JPasswordField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 8;

    public ModernPasswordField() {
        this("");
    }

    public ModernPasswordField(String placeholder) {
        super();
        this.placeholder = placeholder;
        initField();
    }

    private void initField() {
        setOpaque(false);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.TEXT_DARK);
        setCaretColor(UITheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(getPreferredSize().width, 38));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.applyAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(isEnabled() ? Color.WHITE : new Color(248, 250, 252));
        g2.fillRoundRect(1, 1, width - 2, height - 2, cornerRadius, cornerRadius);

        // Border
        if (isFocused) {
            g2.setColor(UITheme.PRIMARY);
            g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);
        } else {
            g2.setColor(UITheme.CARD_BORDER);
            g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);

        // Placeholder
        if (getPassword().length == 0 && !placeholder.isEmpty() && !isFocused) {
            Graphics2D gPlaceholder = (Graphics2D) g.create();
            UITheme.applyAntiAliasing(gPlaceholder);
            gPlaceholder.setColor(UITheme.TEXT_MUTED);
            gPlaceholder.setFont(getFont());
            Insets insets = getInsets();
            int textY = (height - gPlaceholder.getFontMetrics().getHeight()) / 2 + gPlaceholder.getFontMetrics().getAscent();
            gPlaceholder.drawString(placeholder, insets.left, textY);
            gPlaceholder.dispose();
        }
    }
}
