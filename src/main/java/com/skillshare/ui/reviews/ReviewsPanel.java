package com.skillshare.ui.reviews;

import com.skillshare.model.Review;
import com.skillshare.model.Student;
import com.skillshare.service.ReviewService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.StarRatingBar;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Screen displaying Ratings and Feedback Reviews (both received and given).
 */
public class ReviewsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final ReviewService reviewService;

    private JTabbedPane tabbedPane;
    private JLabel avgRatingLabel;

    private JTable receivedTable;
    private DefaultTableModel receivedModel;

    private JTable givenTable;
    private DefaultTableModel givenModel;

    public ReviewsPanel(MainFrame mainFrame, ReviewService reviewService) {
        this.mainFrame = mainFrame;
        this.reviewService = reviewService;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header Card
        ModernCard headerCard = new ModernCard(new BorderLayout(16, 0), 12);
        headerCard.setCardBackground(Color.WHITE);
        headerCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));

        JPanel headerInfo = new JPanel();
        headerInfo.setLayout(new BoxLayout(headerInfo, BoxLayout.Y_AXIS));
        headerInfo.setOpaque(false);

        JLabel pageTitle = new JLabel("Reviews & Ratings");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_DARK);

        avgRatingLabel = new JLabel("Average campus rating: ★ 0.0 / 5.0");
        avgRatingLabel.setFont(UITheme.FONT_BODY);
        avgRatingLabel.setForeground(UITheme.WARNING.darker());

        headerInfo.add(pageTitle);
        headerInfo.add(Box.createVerticalStrut(2));
        headerInfo.add(avgRatingLabel);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        headerActions.setOpaque(false);

        ModernButton submitBtn = new ModernButton("✍ Write Review", ModernButton.Style.PRIMARY);
        submitBtn.addActionListener(e -> openSubmitDialog());

        ModernButton refreshBtn = new ModernButton("🔄 Refresh", ModernButton.Style.OUTLINE);
        refreshBtn.addActionListener(e -> refreshData());

        headerActions.add(refreshBtn);
        headerActions.add(submitBtn);

        headerCard.add(headerInfo, BorderLayout.CENTER);
        headerCard.add(headerActions, BorderLayout.EAST);

        contentPanel.add(headerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);

        // 1. Reviews Received
        JPanel receivedPanel = new JPanel(new BorderLayout());
        receivedPanel.setBackground(Color.WHITE);
        receivedPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        receivedModel = new DefaultTableModel(new String[]{"ID", "From Student", "Skill Exchanged", "Rating", "Feedback Comment"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        receivedTable = createStyledTable(receivedModel);
        receivedPanel.add(new JScrollPane(receivedTable), BorderLayout.CENTER);
        tabbedPane.addTab("⭐ Reviews Received", receivedPanel);

        // 2. Reviews Given
        JPanel givenPanel = new JPanel(new BorderLayout());
        givenPanel.setBackground(Color.WHITE);
        givenPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        givenModel = new DefaultTableModel(new String[]{"ID", "Student Reviewed", "Skill Exchanged", "Rating", "Your Feedback"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        givenTable = createStyledTable(givenModel);
        givenPanel.add(new JScrollPane(givenTable), BorderLayout.CENTER);
        tabbedPane.addTab("📝 Reviews Given", givenPanel);

        ModernCard containerCard = new ModernCard(new BorderLayout(), 12);
        containerCard.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.add(containerCard);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(UITheme.FONT_BODY);
        table.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        table.setSelectionForeground(UITheme.PRIMARY);
        table.setShowVerticalLines(false);
        table.setGridColor(UITheme.CARD_BORDER);

        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);

        return table;
    }

    public void refreshData() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Review> received;
            private List<Review> given;
            private double avg;

            @Override
            protected Void doInBackground() throws Exception {
                received = reviewService.getReviewsReceived(current.getStudentId());
                given = reviewService.getReviewsGiven(current.getStudentId());
                avg = reviewService.getAverageRating(current.getStudentId());
                return null;
            }

            @Override
            protected void done() {
                avgRatingLabel.setText(String.format("Average peer rating: ★ %.1f / 5.0 (%d reviews)", avg, (received != null ? received.size() : 0)));

                // Populate Received
                receivedModel.setRowCount(0);
                if (received != null) {
                    for (Review r : received) {
                        receivedModel.addRow(new Object[]{
                                r.getReviewId(),
                                r.getReviewerName(),
                                r.getSkillName(),
                                "★ " + r.getRating() + "/5",
                                r.getComment()
                        });
                    }
                }

                // Populate Given
                givenModel.setRowCount(0);
                if (given != null) {
                    for (Review r : given) {
                        givenModel.addRow(new Object[]{
                                r.getReviewId(),
                                r.getReviewedStudentName(),
                                r.getSkillName(),
                                "★ " + r.getRating() + "/5",
                                r.getComment()
                        });
                    }
                }
            }
        };
        worker.execute();
    }

    private void openSubmitDialog() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        SubmitReviewDialog dlg = new SubmitReviewDialog(mainFrame, reviewService, current.getStudentId(), this::refreshData);
        dlg.setVisible(true);
    }
}

