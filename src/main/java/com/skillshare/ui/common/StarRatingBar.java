package com.skillshare.ui.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Interactive or read-only 5-star rating bar component.
 */
public class StarRatingBar extends JPanel {
    private int rating = 0;
    private final boolean interactive;
    private final JLabel[] starLabels = new JLabel[5];

    public StarRatingBar(int initialRating, boolean interactive) {
        this.rating = Math.max(0, Math.min(5, initialRating));
        this.interactive = interactive;

        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        setOpaque(false);

        for (int i = 0; i < 5; i++) {
            final int starIndex = i + 1;
            JLabel label = new JLabel(starIndex <= rating ? "★" : "☆");
            label.setFont(UITheme.FONT_SECTION);
            label.setForeground(starIndex <= rating ? UITheme.WARNING : new Color(203, 213, 225));

            if (interactive) {
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        setRating(starIndex);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        highlightStars(starIndex);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        highlightStars(rating);
                    }
                });
            }

            starLabels[i] = label;
            add(label);
        }
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int r) {
        this.rating = Math.max(0, Math.min(5, r));
        highlightStars(this.rating);
    }

    private void highlightStars(int count) {
        for (int i = 0; i < 5; i++) {
            boolean active = (i < count);
            starLabels[i].setText(active ? "★" : "☆");
            starLabels[i].setForeground(active ? UITheme.WARNING : new Color(203, 213, 225));
        }
    }
}
