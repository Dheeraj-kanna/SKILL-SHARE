package com.skillshare.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Metric card displaying a statistic with title, value, icon and accent color.
 */
public class StatCard extends ModernCard {
    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;

    public StatCard(String icon, String title, String initialValue, String subtitle, Color accentColor) {
        super(12);
        setLayout(new BorderLayout(8, 8));
        setPreferredSize(new Dimension(190, 110));
        setMinimumSize(new Dimension(160, 105));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(UITheme.FONT_SECTION);
        iconLabel.setForeground(accentColor);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(iconLabel, BorderLayout.EAST);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(UITheme.FONT_STAT_NUM);
        valueLabel.setForeground(UITheme.TEXT_DARK);

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(accentColor.darker());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(valueLabel, BorderLayout.CENTER);
        centerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // Accent indicator bar on the left
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
    }

    public void setValue(String val) {
        valueLabel.setText(val);
    }

    public void setSubtitle(String sub) {
        subtitleLabel.setText(sub);
    }
}
