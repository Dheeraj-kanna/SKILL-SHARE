package com.skillshare.ui.reviews;

import com.skillshare.model.ExchangeRequest;
import com.skillshare.model.Review;
import com.skillshare.service.ReviewService;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernTextField;
import com.skillshare.ui.common.StarRatingBar;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Dialog to submit a 1-5 star review and comment for an accepted exchange.
 */
public class SubmitReviewDialog extends JDialog {
    private final ReviewService reviewService;
    private final int currentStudentId;
    private final Runnable onSuccess;

    private JComboBox<ReviewExchangeItem> exchangeCombo;
    private StarRatingBar ratingBar;
    private JTextArea commentArea;
    private JLabel statusLabel;
    private ModernButton submitButton;

    public static class ReviewExchangeItem {
        final int exchangeId;
        final String displayText;

        public ReviewExchangeItem(int exchangeId, String displayText) {
            this.exchangeId = exchangeId;
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    public SubmitReviewDialog(JFrame parent, ReviewService reviewService, int currentStudentId, Runnable onSuccess) {
        super(parent, "Submit Peer Review", true);
        this.reviewService = reviewService;
        this.currentStudentId = currentStudentId;
        this.onSuccess = onSuccess;

        initUI();
        loadEligibleExchanges();
    }

    private void initUI() {
        setSize(480, 420);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 24, 4, 24);
        gbc.gridx = 0;

        JLabel titleLbl = new JLabel("Rate & Review Your Exchange Partner");
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 24, 12, 24);
        form.add(titleLbl, gbc);

        // Exchange Dropdown
        JLabel exLbl = new JLabel("Select Exchange");
        exLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 24, 2, 24);
        form.add(exLbl, gbc);

        exchangeCombo = new JComboBox<>();
        exchangeCombo.setFont(UITheme.FONT_BODY);
        exchangeCombo.setPreferredSize(new Dimension(400, 38));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 24, 10, 24);
        form.add(exchangeCombo, gbc);

        // Rating
        JLabel rateLbl = new JLabel("Rating (Click to set 1-5 Stars)");
        rateLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 24, 2, 24);
        form.add(rateLbl, gbc);

        ratingBar = new StarRatingBar(5, true);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 24, 10, 24);
        form.add(ratingBar, gbc);

        // Comment
        JLabel comLbl = new JLabel("Feedback Comment");
        comLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 5;
        gbc.insets = new Insets(2, 24, 2, 24);
        form.add(comLbl, gbc);

        commentArea = new JTextArea(4, 30);
        commentArea.setFont(UITheme.FONT_BODY);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane comScroll = new JScrollPane(commentArea);
        comScroll.setPreferredSize(new Dimension(400, 75));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 24, 8, 24);
        form.add(comScroll, gbc);

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 24, 8, 24);
        form.add(statusLabel, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.Style.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());

        submitButton = new ModernButton("Submit Review", ModernButton.Style.PRIMARY);
        submitButton.addActionListener(e -> handleSubmit());

        btnPanel.add(cancelBtn);
        btnPanel.add(submitButton);

        gbc.gridy = 8;
        gbc.insets = new Insets(4, 24, 16, 24);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void loadEligibleExchanges() {
        SwingWorker<List<ExchangeRequest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExchangeRequest> doInBackground() throws Exception {
                return reviewService.getExchangesAvailableForReview(currentStudentId);
            }

            @Override
            protected void done() {
                try {
                    List<ExchangeRequest> list = get();
                    exchangeCombo.removeAllItems();
                    if (list.isEmpty()) {
                        statusLabel.setText("No pending exchanges waiting for your review.");
                        submitButton.setEnabled(false);
                    } else {
                        for (ExchangeRequest ex : list) {
                            String partner = (ex.getRequesterId() == currentStudentId) ? ex.getReceiverName() : ex.getRequesterName();
                            exchangeCombo.addItem(new ReviewExchangeItem(
                                    ex.getExchangeId(),
                                    partner + " - " + ex.getSkillName()
                            ));
                        }
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load eligible exchanges: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleSubmit() {
        ReviewExchangeItem selected = (ReviewExchangeItem) exchangeCombo.getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an exchange to review.");
            return;
        }

        int rating = ratingBar.getRating();
        if (rating < 1 || rating > 5) {
            statusLabel.setText("Please select a rating between 1 and 5 stars.");
            return;
        }

        String comment = commentArea.getText().trim();

        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");
        statusLabel.setText(" ");

        SwingWorker<Review, Void> worker = new SwingWorker<>() {
            @Override
            protected Review doInBackground() throws Exception {
                return reviewService.submitReview(selected.exchangeId, currentStudentId, rating, comment);
            }

            @Override
            protected void done() {
                try {
                    get();
                    dispose();
                    if (onSuccess != null) onSuccess.run();
                } catch (Exception ex) {
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit Review");
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setText(cause.getMessage());
                }
            }
        };
        worker.execute();
    }
}

