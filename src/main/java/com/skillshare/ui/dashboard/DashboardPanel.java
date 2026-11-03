package com.skillshare.ui.dashboard;

import com.skillshare.model.Skill;
import com.skillshare.model.SkillMatch;
import com.skillshare.model.Student;
import com.skillshare.service.ExchangeService;
import com.skillshare.service.MatchingService;
import com.skillshare.service.ReviewService;
import com.skillshare.service.SessionService;
import com.skillshare.service.SkillService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.BadgeLabel;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.StatCard;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

/**
 * Modern Dashboard view presenting key metrics, quick actions, recommended peers and recent activity.
 */
public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SkillService skillService;
    private final MatchingService matchingService;
    private final ExchangeService exchangeService;
    private final SessionService sessionService;
    private final ReviewService reviewService;

    private JLabel welcomeLabel;
    private JLabel subtitleLabel;
    private StatCard teachCard;
    private StatCard learnCard;
    private StatCard matchesCard;
    private StatCard requestsCard;
    private StatCard sessionsCard;

    private JPanel matchesListPanel;
    private JPanel recentSkillsPanel;

    public DashboardPanel(MainFrame mainFrame, SkillService skillService, MatchingService matchingService,
                          ExchangeService exchangeService, SessionService sessionService, ReviewService reviewService) {
        this.mainFrame = mainFrame;
        this.skillService = skillService;
        this.matchingService = matchingService;
        this.exchangeService = exchangeService;
        this.sessionService = sessionService;
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

        // 1. Header Banner
        ModernCard headerCard = new ModernCard(new BorderLayout(16, 0), 14);
        headerCard.setCardBackground(Color.WHITE);
        headerCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 90));

        JPanel headerTextPanel = new JPanel();
        headerTextPanel.setLayout(new BoxLayout(headerTextPanel, BoxLayout.Y_AXIS));
        headerTextPanel.setOpaque(false);

        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(UITheme.FONT_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_DARK);

        subtitleLabel = new JLabel("Here is what's happening with your campus skill exchanges today.");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerTextPanel.add(welcomeLabel);
        headerTextPanel.add(Box.createVerticalStrut(4));
        headerTextPanel.add(subtitleLabel);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        headerButtons.setOpaque(false);

        ModernButton findMatchesBtn = new ModernButton("🔍 Find Matches", ModernButton.Style.PRIMARY);
        findMatchesBtn.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_MATCHES));

        ModernButton addSkillBtn = new ModernButton("+ Add Skill", ModernButton.Style.OUTLINE);
        addSkillBtn.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_SKILLS));

        headerButtons.add(addSkillBtn);
        headerButtons.add(findMatchesBtn);

        headerCard.add(headerTextPanel, BorderLayout.CENTER);
        headerCard.add(headerButtons, BorderLayout.EAST);

        contentPanel.add(headerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Metrics Statistics Row (5 cards)
        JPanel statsRow = new JPanel(new GridLayout(1, 5, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

        teachCard = new StatCard("💡", "Skills Offered", "0", "Can teach", UITheme.PRIMARY);
        learnCard = new StatCard("🎯", "Skills Wanted", "0", "Want to learn", UITheme.ACCENT_PURPLE);
        matchesCard = new StatCard("🤝", "Matches Found", "0", "Potential peers", UITheme.SUCCESS);
        requestsCard = new StatCard("📬", "Pending Requests", "0", "Needs action", UITheme.WARNING);
        sessionsCard = new StatCard("📅", "Upcoming Sessions", "0", "Scheduled", UITheme.ACCENT_BLUE);

        statsRow.add(teachCard);
        statsRow.add(learnCard);
        statsRow.add(matchesCard);
        statsRow.add(requestsCard);
        statsRow.add(sessionsCard);

        contentPanel.add(statsRow);
        contentPanel.add(Box.createVerticalStrut(22));

        // 3. Lower Section: 2 Columns (Recommended Matches & Recent Activity)
        JPanel twoColPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        twoColPanel.setOpaque(false);

        // Left Column: Recommended Matches Preview
        ModernCard matchesSection = new ModernCard(new BorderLayout(0, 12), 12);
        JPanel matchHeader = new JPanel(new BorderLayout());
        matchHeader.setOpaque(false);

        JLabel matchTitle = new JLabel("Recommended Peer Matches");
        matchTitle.setFont(UITheme.FONT_SECTION);
        matchTitle.setForeground(UITheme.TEXT_DARK);

        ModernButton viewAllMatches = new ModernButton("View All →", ModernButton.Style.OUTLINE);
        viewAllMatches.setFont(UITheme.FONT_SMALL_BOLD);
        viewAllMatches.setPreferredSize(new Dimension(100, 28));
        viewAllMatches.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_MATCHES));

        matchHeader.add(matchTitle, BorderLayout.WEST);
        matchHeader.add(viewAllMatches, BorderLayout.EAST);
        matchesSection.add(matchHeader, BorderLayout.NORTH);

        matchesListPanel = new JPanel();
        matchesListPanel.setLayout(new BoxLayout(matchesListPanel, BoxLayout.Y_AXIS));
        matchesListPanel.setOpaque(false);
        matchesSection.add(matchesListPanel, BorderLayout.CENTER);

        // Right Column: Recently Added Skills
        ModernCard recentSection = new ModernCard(new BorderLayout(0, 12), 12);
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel recentTitle = new JLabel("Campus Skill Activity");
        recentTitle.setFont(UITheme.FONT_SECTION);
        recentTitle.setForeground(UITheme.TEXT_DARK);

        ModernButton manageSkillsBtn = new ModernButton("My Skills →", ModernButton.Style.OUTLINE);
        manageSkillsBtn.setFont(UITheme.FONT_SMALL_BOLD);
        manageSkillsBtn.setPreferredSize(new Dimension(100, 28));
        manageSkillsBtn.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_SKILLS));

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(manageSkillsBtn, BorderLayout.EAST);
        recentSection.add(recentHeader, BorderLayout.NORTH);

        recentSkillsPanel = new JPanel();
        recentSkillsPanel.setLayout(new BoxLayout(recentSkillsPanel, BoxLayout.Y_AXIS));
        recentSkillsPanel.setOpaque(false);
        recentSection.add(recentSkillsPanel, BorderLayout.CENTER);

        twoColPanel.add(matchesSection);
        twoColPanel.add(recentSection);

        contentPanel.add(twoColPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UITheme.BG_MAIN);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        welcomeLabel.setText("Welcome back, " + current.getName() + " 👋");
        subtitleLabel.setText("Department: " + current.getDepartment() + " | Email: " + current.getEmail());

        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                int id = current.getStudentId();
                int teachCount = skillService.countTeachingSkills(id);
                int learnCount = skillService.countLearningSkills(id);
                List<SkillMatch> matches = matchingService.findMatches(id);
                int pendingRequests = exchangeService.countPendingIncoming(id);
                int upcomingSessions = sessionService.countUpcomingSessions(id);
                List<Skill> recentSkills = skillService.getRecentSkills(8);

                return new DashboardData(teachCount, learnCount, matches, pendingRequests, upcomingSessions, recentSkills);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    teachCard.setValue(String.valueOf(data.teachCount));
                    learnCard.setValue(String.valueOf(data.learnCount));
                    matchesCard.setValue(String.valueOf(data.matches.size()));
                    requestsCard.setValue(String.valueOf(data.pendingRequests));
                    sessionsCard.setValue(String.valueOf(data.upcomingSessions));

                    // Populate recommended matches preview (top 3)
                    matchesListPanel.removeAll();
                    if (data.matches.isEmpty()) {
                        JLabel emptyLbl = new JLabel("<html>No matches yet. Add learning interests in <b>My Skills</b> to find peer matches!</html>");
                        emptyLbl.setFont(UITheme.FONT_BODY);
                        emptyLbl.setForeground(UITheme.TEXT_MUTED);
                        emptyLbl.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
                        matchesListPanel.add(emptyLbl);
                    } else {
                        int limit = Math.min(3, data.matches.size());
                        for (int i = 0; i < limit; i++) {
                            SkillMatch m = data.matches.get(i);
                            matchesListPanel.add(createMatchPreviewItem(m));
                            if (i < limit - 1) {
                                matchesListPanel.add(Box.createVerticalStrut(10));
                            }
                        }
                    }
                    matchesListPanel.revalidate();
                    matchesListPanel.repaint();

                    // Populate recent skills
                    recentSkillsPanel.removeAll();
                    if (data.recentSkills.isEmpty()) {
                        JLabel emptyLbl = new JLabel("No skills listed on campus yet. Be the first!");
                        emptyLbl.setFont(UITheme.FONT_BODY);
                        emptyLbl.setForeground(UITheme.TEXT_MUTED);
                        emptyLbl.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
                        recentSkillsPanel.add(emptyLbl);
                    } else {
                        for (Skill s : data.recentSkills) {
                            recentSkillsPanel.add(createSkillItem(s));
                            recentSkillsPanel.add(Box.createVerticalStrut(6));
                        }
                    }
                    recentSkillsPanel.revalidate();
                    recentSkillsPanel.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createMatchPreviewItem(SkillMatch m) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLbl = new JLabel(m.getStudentName() + " (" + m.getDepartment() + ")");
        nameLbl.setFont(UITheme.FONT_BODY_BOLD);
        nameLbl.setForeground(UITheme.TEXT_DARK);

        JLabel teachLbl = new JLabel("Can teach you: " + m.getMatchingSkill());
        teachLbl.setFont(UITheme.FONT_SMALL);
        teachLbl.setForeground(UITheme.PRIMARY);

        info.add(nameLbl);
        info.add(Box.createVerticalStrut(2));
        info.add(teachLbl);

        item.add(info, BorderLayout.CENTER);

        if (m.isMutualMatch()) {
            BadgeLabel mutualBadge = new BadgeLabel("★ Mutual Match", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_TEXT);
            item.add(mutualBadge, BorderLayout.EAST);
        }

        return item;
    }

    private JPanel createSkillItem(Skill s) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel nameLbl = new JLabel(s.getSkillName());
        nameLbl.setFont(UITheme.FONT_BODY);
        nameLbl.setForeground(UITheme.TEXT_DARK);

        BadgeLabel typeBadge = BadgeLabel.createSkillBadge(
                s.isTeach() ? "Offering to Teach" : "Wanting to Learn",
                s.isTeach()
        );

        item.add(nameLbl, BorderLayout.WEST);
        item.add(typeBadge, BorderLayout.EAST);
        return item;
    }

    private static class DashboardData {
        final int teachCount;
        final int learnCount;
        final List<SkillMatch> matches;
        final int pendingRequests;
        final int upcomingSessions;
        final List<Skill> recentSkills;

        DashboardData(int teachCount, int learnCount, List<SkillMatch> matches,
                      int pendingRequests, int upcomingSessions, List<Skill> recentSkills) {
            this.teachCount = teachCount;
            this.learnCount = learnCount;
            this.matches = matches;
            this.pendingRequests = pendingRequests;
            this.upcomingSessions = upcomingSessions;
            this.recentSkills = recentSkills;
        }
    }
}
