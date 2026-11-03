package com.skillshare.ui.skills;

import com.skillshare.model.Skill;
import com.skillshare.service.SkillService;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernTextField;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;

/**
 * Modal dialog for adding a new teaching skill or learning interest.
 */
public class AddSkillDialog extends JDialog {
    private final SkillService skillService;
    private final int studentId;
    private final Runnable onSuccess;

    private ModernTextField skillField;
    private JRadioButton teachRadio;
    private JRadioButton learnRadio;
    private JLabel statusLabel;
    private ModernButton saveButton;

    public AddSkillDialog(JFrame parent, SkillService skillService, int studentId, String defaultType, Runnable onSuccess) {
        super(parent, "Add New Skill", true);
        this.skillService = skillService;
        this.studentId = studentId;
        this.onSuccess = onSuccess;

        initUI(defaultType);
    }

    private void initUI(String defaultType) {
        setSize(420, 290);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 24, 6, 24);
        gbc.gridx = 0;

        JLabel titleLbl = new JLabel("Add Skill or Interest");
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 24, 12, 24);
        form.add(titleLbl, gbc);

        // Radio Buttons: TEACH vs LEARN
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        typePanel.setBackground(Color.WHITE);
        teachRadio = new JRadioButton("I can teach this", "TEACH".equalsIgnoreCase(defaultType));
        teachRadio.setFont(UITheme.FONT_BODY_BOLD);
        teachRadio.setBackground(Color.WHITE);

        learnRadio = new JRadioButton("I want to learn this", "LEARN".equalsIgnoreCase(defaultType));
        learnRadio.setFont(UITheme.FONT_BODY_BOLD);
        learnRadio.setBackground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(teachRadio);
        group.add(learnRadio);

        if (!"LEARN".equalsIgnoreCase(defaultType)) {
            teachRadio.setSelected(true);
        }

        typePanel.add(teachRadio);
        typePanel.add(learnRadio);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 24, 12, 24);
        form.add(typePanel, gbc);

        // Skill Name
        JLabel nameLbl = new JLabel("Skill Name");
        nameLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 24, 4, 24);
        form.add(nameLbl, gbc);

        skillField = new ModernTextField("e.g. Python, Public Speaking, Calculus, Piano");
        skillField.setPreferredSize(new Dimension(340, 38));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 24, 10, 24);
        form.add(skillField, gbc);

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 24, 8, 24);
        form.add(statusLabel, gbc);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.Style.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());

        saveButton = new ModernButton("Add Skill", ModernButton.Style.PRIMARY);
        saveButton.addActionListener(e -> handleSave());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveButton);

        gbc.gridy = 5;
        gbc.insets = new Insets(4, 24, 16, 24);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.CENTER);

        skillField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSave();
                }
            }
        });
    }

    private void handleSave() {
        String skillName = skillField.getText().trim();
        if (skillName.isEmpty()) {
            statusLabel.setText("Please enter a skill name.");
            return;
        }

        String skillType = teachRadio.isSelected() ? "TEACH" : "LEARN";

        saveButton.setEnabled(false);
        saveButton.setText("Adding...");
        statusLabel.setText(" ");

        SwingWorker<Skill, Void> worker = new SwingWorker<>() {
            @Override
            protected Skill doInBackground() throws Exception {
                return skillService.addSkill(studentId, skillName, skillType);
            }

            @Override
            protected void done() {
                try {
                    get();
                    dispose();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } catch (Exception ex) {
                    saveButton.setEnabled(true);
                    saveButton.setText("Add Skill");
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setText(cause.getMessage());
                }
            }
        };
        worker.execute();
    }
}
