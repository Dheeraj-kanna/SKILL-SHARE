package com.skillshare.ui.matches;

import com.skillshare.model.SkillMatch;
import com.skillshare.ui.common.BadgeLabel;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Visual card representing a matching student, skills they teach, and instant request trigger.
 */
public class MatchCard extends ModernCard {
    private final SkillMatch match;
    private final Consumer<SkillMatch> onRequest;

    public MatchCard(SkillMatch match, Consumer<SkillMatch> onRequest) {
        super(12);
        this.match = match;
        this.onRequest = onRequest;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(14, 12));
        setPreferredSize(new Dimension(340, 210));
        setMinimumSize(new Dimension(300, 200));

        // Top: Student avatar icon, Name, Department & Mutual badge
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(UITheme.FONT_TITLE);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(match.getStudentName());
        nameLabel.setFont(UITheme.FONT_SECTION);
        nameLabel.setForeground(UITheme.TEXT_DARK);

        JLabel deptLabel = new JLabel(match.getDepartment());
        deptLabel.setFont(UITheme.FONT_SMALL);
        deptLabel.setForeground(UITheme.TEXT_MUTED);

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(2));
        namePanel.add(deptLabel);

        topPanel.add(avatarLabel, BorderLayout.WEST);
        topPanel.add(namePanel, BorderLayout.CENTER);

        if (match.isMutualMatch()) {
            BadgeLabel mutualBadge = new BadgeLabel("★ Mutual Match", UITheme.SUCCESS_LIGHT, UITheme.SUCCESS_TEXT);
            topPanel.add(mutualBadge, BorderLayout.EAST);
        }

        // Center: Matching skills breakdown
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel matchHeader = new JLabel("Can teach you:");
        matchHeader.setFont(UITheme.FONT_SMALL_BOLD);
        matchHeader.setForeground(UITheme.PRIMARY);

        JPanel teachBadges = new FlowLayout(FlowLayout.LEFT, 6, 4) == null ? new JPanel() : new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        teachBadges.setOpaque(false);
        String[] skills = match.getMatchingSkill().split(", ");
        for (String s : skills) {
            teachBadges.add(BadgeLabel.createSkillBadge(s, true));
        }

        JLabel learnHeader = new JLabel("Also looking to learn:");
        learnHeader.setFont(UITheme.FONT_SMALL);
        learnHeader.setForeground(UITheme.TEXT_MUTED);

        JPanel learnBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        learnBadges.setOpaque(false);
        if (match.getAllLearnSkills().isEmpty()) {
            JLabel noneLbl = new JLabel("No requests yet");
            noneLbl.setFont(UITheme.FONT_SMALL);
            noneLbl.setForeground(UITheme.TEXT_MUTED);
            learnBadges.add(noneLbl);
        } else {
            int count = 0;
            for (String s : match.getAllLearnSkills()) {
                if (count++ >= 3) break;
                learnBadges.add(BadgeLabel.createSkillBadge(s, false));
            }
        }

        centerPanel.add(matchHeader);
        centerPanel.add(teachBadges);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(learnHeader);
        centerPanel.add(learnBadges);

        // Bottom: Action button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        ModernButton actionBtn;
        if (match.isRequestAlreadySent()) {
            actionBtn = new ModernButton("✓ Request Pending", ModernButton.Style.SECONDARY);
            actionBtn.setEnabled(false);
        } else {
            actionBtn = new ModernButton("Send Exchange Request", ModernButton.Style.PRIMARY);
            actionBtn.addActionListener(e -> {
                if (onRequest != null) {
                    onRequest.accept(match);
                }
            });
        }
        bottomPanel.add(actionBtn, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

