package com.skillshare.ui.matches;

import com.skillshare.model.SkillMatch;
import com.skillshare.model.Student;
import com.skillshare.service.ExchangeService;
import com.skillshare.service.MatchingService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

/**
 * Screen displaying peer matches who can teach skills the current student wants to learn.
 */
public class FindMatchesPanel extends JPanel {
    private final MainFrame mainFrame;
    private final MatchingService matchingService;
    private final ExchangeService exchangeService;

    private JPanel gridContainer;
    private JLabel matchCountLabel;

    public FindMatchesPanel(MainFrame mainFrame, MatchingService matchingService, ExchangeService exchangeService) {
        this.mainFrame = mainFrame;
        this.matchingService = matchingService;
        this.exchangeService = exchangeService;

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

        JLabel pageTitle = new JLabel("Find Skill Matches");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_DARK);

        matchCountLabel = new JLabel("Finding peers who can teach the skills you want to learn...");
        matchCountLabel.setFont(UITheme.FONT_BODY);
        matchCountLabel.setForeground(UITheme.TEXT_MUTED);

        headerInfo.add(pageTitle);
        headerInfo.add(Box.createVerticalStrut(2));
        headerInfo.add(matchCountLabel);

        ModernButton refreshBtn = new ModernButton("🔄 Refresh Matches", ModernButton.Style.OUTLINE);
        refreshBtn.addActionListener(e -> refreshData());

        headerCard.add(headerInfo, BorderLayout.CENTER);
        headerCard.add(refreshBtn, BorderLayout.EAST);

        contentPanel.add(headerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Dynamic matches grid container
        gridContainer = new JPanel();
        gridContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 18));
        gridContainer.setBackground(UITheme.BG_MAIN);

        contentPanel.add(gridContainer);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UITheme.BG_MAIN);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        gridContainer.removeAll();
        matchCountLabel.setText("Searching MySQL database for complementary skill matches...");

        SwingWorker<List<SkillMatch>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<SkillMatch> doInBackground() throws Exception {
                return matchingService.findMatches(current.getStudentId());
            }

            @Override
            protected void done() {
                try {
                    List<SkillMatch> matches = get();
                    gridContainer.removeAll();

                    if (matches.isEmpty()) {
                        matchCountLabel.setText("0 matches found.");
                        ModernCard emptyCard = new ModernCard(new BorderLayout(), 12);
                        emptyCard.setPreferredSize(new Dimension(500, 160));
                        JLabel emptyLabel = new JLabel(
                                "<html><div style='text-align: center; padding: 20px;'>" +
                                "<h3>No matching peers found right now</h3>" +
                                "<p style='color: #64748b;'>Make sure you have added skills you <b>Want to Learn</b> in the <b>My Skills</b> tab.<br>" +
                                "As other campus students list skills they can teach, matches will appear here automatically.</p>" +
                                "</div></html>",
                                JLabel.CENTER
                        );
                        emptyCard.add(emptyLabel, BorderLayout.CENTER);
                        gridContainer.add(emptyCard);
                    } else {
                        matchCountLabel.setText(matches.size() + " campus peer(s) found who can teach what you want to learn:");
                        for (SkillMatch m : matches) {
                            gridContainer.add(new MatchCard(m, FindMatchesPanel.this::handleSendRequest));
                        }
                    }

                    gridContainer.revalidate();
                    gridContainer.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    matchCountLabel.setText("Error loading matches: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleSendRequest(SkillMatch match) {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        // In case multiple skills match, let user pick which one or choose the first
        String[] skills = match.getMatchingSkill().split(", ");
        String selectedSkill = skills[0];
        if (skills.length > 1) {
            selectedSkill = (String) JOptionPane.showInputDialog(
                    this,
                    "Select which skill you would like to request to learn from " + match.getStudentName() + ":",
                    "Select Skill",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    skills,
                    skills[0]
            );
            if (selectedSkill == null) return; // Cancelled
        }

        final String finalSkill = selectedSkill;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                exchangeService.sendRequest(current.getStudentId(), match.getStudentId(), finalSkill);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            FindMatchesPanel.this,
                            "Exchange request for '" + finalSkill + "' sent to " + match.getStudentName() + " successfully!",
                            "Request Sent",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshData();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(
                            FindMatchesPanel.this,
                            "Failed to send request: " + cause.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
}

