package com.skillshare.ui.profile;

import com.skillshare.config.AppConfig;
import com.skillshare.model.Student;
import com.skillshare.service.AuthService;
import com.skillshare.service.SkillService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.ModernPasswordField;
import com.skillshare.ui.common.ModernTextField;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

/**
 * Student profile management and database settings panel.
 */
public class ProfilePanel extends JPanel {
    private final MainFrame mainFrame;
    private final AuthService authService;
    private final SkillService skillService;

    private JLabel avatarLabel;
    private JLabel nameDisplay;
    private JLabel emailDisplay;
    private JLabel deptDisplay;

    private ModernTextField editNameField;
    private ModernTextField editDeptField;
    private JLabel profileStatusLabel;

    private ModernPasswordField curPwdField;
    private ModernPasswordField newPwdField;
    private JLabel pwdStatusLabel;

    public ProfilePanel(MainFrame mainFrame, AuthService authService, SkillService skillService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.skillService = skillService;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // 1. Profile Summary Card
        ModernCard summaryCard = new ModernCard(new BorderLayout(20, 0), 12);
        summaryCard.setCardBackground(Color.WHITE);
        summaryCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

        avatarLabel = new JLabel("🎓");
        avatarLabel.setFont(UITheme.FONT_TITLE.deriveFont(44f));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        nameDisplay = new JLabel("Student Name");
        nameDisplay.setFont(UITheme.FONT_TITLE);
        nameDisplay.setForeground(UITheme.TEXT_DARK);

        emailDisplay = new JLabel("student@college.edu");
        emailDisplay.setFont(UITheme.FONT_BODY);
        emailDisplay.setForeground(UITheme.TEXT_MUTED);

        deptDisplay = new JLabel("Department: Computer Science");
        deptDisplay.setFont(UITheme.FONT_SMALL_BOLD);
        deptDisplay.setForeground(UITheme.PRIMARY);

        namePanel.add(nameDisplay);
        namePanel.add(Box.createVerticalStrut(4));
        namePanel.add(emailDisplay);
        namePanel.add(Box.createVerticalStrut(2));
        namePanel.add(deptDisplay);

        summaryCard.add(avatarLabel, BorderLayout.WEST);
        summaryCard.add(namePanel, BorderLayout.CENTER);

        contentPanel.add(summaryCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Settings Row: Profile Details & Password Change
        JPanel settingsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        settingsRow.setOpaque(false);

        // --- Left: Edit Profile Info ---
        ModernCard infoCard = new ModernCard(new GridBagLayout(), 12);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 12, 4, 12);
        gbc.gridx = 0;

        JLabel editTitle = new JLabel("Edit Profile Details");
        editTitle.setFont(UITheme.FONT_SECTION);
        editTitle.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 12, 12, 12);
        infoCard.add(editTitle, gbc);

        JLabel nameLbl = new JLabel("Full Name");
        nameLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 12, 2, 12);
        infoCard.add(nameLbl, gbc);

        editNameField = new ModernTextField();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 12, 8, 12);
        infoCard.add(editNameField, gbc);

        JLabel deptLbl = new JLabel("Department");
        deptLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 12, 2, 12);
        infoCard.add(deptLbl, gbc);

        editDeptField = new ModernTextField();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 12, 10, 12);
        infoCard.add(editDeptField, gbc);

        profileStatusLabel = new JLabel(" ");
        profileStatusLabel.setFont(UITheme.FONT_SMALL);
        profileStatusLabel.setForeground(UITheme.SUCCESS);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 12, 6, 12);
        infoCard.add(profileStatusLabel, gbc);

        ModernButton saveInfoBtn = new ModernButton("Save Profile Changes", ModernButton.Style.PRIMARY);
        saveInfoBtn.addActionListener(e -> handleUpdateProfile());
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 12, 12, 12);
        infoCard.add(saveInfoBtn, gbc);

        // --- Right: Change Password ---
        ModernCard pwdCard = new ModernCard(new GridBagLayout(), 12);
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.fill = GridBagConstraints.HORIZONTAL;
        pgbc.insets = new Insets(4, 12, 4, 12);
        pgbc.gridx = 0;

        JLabel pwdTitle = new JLabel("Security & Password");
        pwdTitle.setFont(UITheme.FONT_SECTION);
        pwdTitle.setForeground(UITheme.TEXT_DARK);
        pgbc.gridy = 0;
        pgbc.insets = new Insets(8, 12, 12, 12);
        pwdCard.add(pwdTitle, pgbc);

        JLabel curLbl = new JLabel("Current Password");
        curLbl.setFont(UITheme.FONT_BODY_BOLD);
        pgbc.gridy = 1;
        pgbc.insets = new Insets(2, 12, 2, 12);
        pwdCard.add(curLbl, pgbc);

        curPwdField = new ModernPasswordField("Enter current password");
        pgbc.gridy = 2;
        pgbc.insets = new Insets(0, 12, 8, 12);
        pwdCard.add(curPwdField, pgbc);

        JLabel newLbl = new JLabel("New Password (min 4 chars)");
        newLbl.setFont(UITheme.FONT_BODY_BOLD);
        pgbc.gridy = 3;
        pgbc.insets = new Insets(2, 12, 2, 12);
        pwdCard.add(newLbl, pgbc);

        newPwdField = new ModernPasswordField("Enter new password");
        pgbc.gridy = 4;
        pgbc.insets = new Insets(0, 12, 10, 12);
        pwdCard.add(newPwdField, pgbc);

        pwdStatusLabel = new JLabel(" ");
        pwdStatusLabel.setFont(UITheme.FONT_SMALL);
        pwdStatusLabel.setForeground(UITheme.DANGER);
        pgbc.gridy = 5;
        pgbc.insets = new Insets(0, 12, 6, 12);
        pwdCard.add(pwdStatusLabel, pgbc);

        ModernButton updatePwdBtn = new ModernButton("Update Password", ModernButton.Style.PRIMARY);
        updatePwdBtn.addActionListener(e -> handleUpdatePassword());
        pgbc.gridy = 6;
        pgbc.insets = new Insets(4, 12, 12, 12);
        pwdCard.add(updatePwdBtn, pgbc);

        settingsRow.add(infoCard);
        settingsRow.add(pwdCard);

        contentPanel.add(settingsRow);
        contentPanel.add(Box.createVerticalStrut(20));

        // 3. MySQL Database Information Card
        ModernCard dbCard = new ModernCard(new BorderLayout(16, 8), 12);
        dbCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 90));

        JPanel dbInfo = new JPanel();
        dbInfo.setLayout(new BoxLayout(dbInfo, BoxLayout.Y_AXIS));
        dbInfo.setOpaque(false);

        JLabel dbTitle = new JLabel("Connected Database Information");
        dbTitle.setFont(UITheme.FONT_SECTION);
        dbTitle.setForeground(UITheme.TEXT_DARK);

        JLabel dbDetail = new JLabel("Target: " + AppConfig.getDbUrl() + " | User: " + AppConfig.getDbUser());
        dbDetail.setFont(UITheme.FONT_SMALL);
        dbDetail.setForeground(UITheme.TEXT_MUTED);

        dbInfo.add(dbTitle);
        dbInfo.add(Box.createVerticalStrut(2));
        dbInfo.add(dbDetail);

        ModernButton logoutBtn = new ModernButton("🚪 Log Out", ModernButton.Style.DANGER);
        logoutBtn.addActionListener(e -> mainFrame.handleLogout());

        dbCard.add(dbInfo, BorderLayout.CENTER);
        dbCard.add(logoutBtn, BorderLayout.EAST);

        contentPanel.add(dbCard);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        nameDisplay.setText(current.getName());
        emailDisplay.setText(current.getEmail());
        deptDisplay.setText("Department: " + current.getDepartment());

        editNameField.setText(current.getName());
        editDeptField.setText(current.getDepartment());
        profileStatusLabel.setText(" ");
        pwdStatusLabel.setText(" ");
        curPwdField.setText("");
        newPwdField.setText("");
    }

    private void handleUpdateProfile() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        String name = editNameField.getText().trim();
        String dept = editDeptField.getText().trim();

        if (name.isEmpty() || dept.isEmpty()) {
            profileStatusLabel.setForeground(UITheme.DANGER);
            profileStatusLabel.setText("Name and department cannot be empty.");
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                authService.updateProfile(current.getStudentId(), name, dept);
                current.setName(name);
                current.setDepartment(dept);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    profileStatusLabel.setForeground(UITheme.SUCCESS);
                    profileStatusLabel.setText("Profile updated successfully!");
                    refreshData();
                    mainFrame.updateSidebarStudentInfo(current);
                } catch (Exception ex) {
                    profileStatusLabel.setForeground(UITheme.DANGER);
                    profileStatusLabel.setText("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleUpdatePassword() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        String curPwd = new String(curPwdField.getPassword());
        String newPwd = new String(newPwdField.getPassword());

        if (curPwd.isEmpty() || newPwd.isEmpty()) {
            pwdStatusLabel.setForeground(UITheme.DANGER);
            pwdStatusLabel.setText("Please enter both current and new passwords.");
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                authService.updatePassword(current.getStudentId(), curPwd, newPwd);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    pwdStatusLabel.setForeground(UITheme.SUCCESS);
                    pwdStatusLabel.setText("Password changed successfully!");
                    curPwdField.setText("");
                    newPwdField.setText("");
                } catch (Exception ex) {
                    pwdStatusLabel.setForeground(UITheme.DANGER);
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    pwdStatusLabel.setText(cause.getMessage());
                }
            }
        };
        worker.execute();
    }
}

