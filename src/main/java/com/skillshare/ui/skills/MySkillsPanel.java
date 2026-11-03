package com.skillshare.ui.skills;

import com.skillshare.model.Skill;
import com.skillshare.model.Student;
import com.skillshare.service.SkillService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.BadgeLabel;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Screen for managing the student's skills:
 * - Skills I Can Teach (with add, view, delete)
 * - Skills I Want to Learn (with add, view, delete)
 */
public class MySkillsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SkillService skillService;

    private JTable teachTable;
    private DefaultTableModel teachModel;
    private List<Skill> teachSkills;

    private JTable learnTable;
    private DefaultTableModel learnModel;
    private List<Skill> learnSkills;

    public MySkillsPanel(MainFrame mainFrame, SkillService skillService) {
        this.mainFrame = mainFrame;
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

        // 1. Top Header Banner
        ModernCard headerCard = new ModernCard(new BorderLayout(16, 0), 12);
        headerCard.setCardBackground(Color.WHITE);
        headerCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));

        JPanel headerInfo = new JPanel();
        headerInfo.setLayout(new BoxLayout(headerInfo, BoxLayout.Y_AXIS));
        headerInfo.setOpaque(false);

        JLabel pageTitle = new JLabel("My Skills & Interests");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_DARK);

        JLabel pageSub = new JLabel("Manage what you can teach to peers and what you want to learn from them.");
        pageSub.setFont(UITheme.FONT_BODY);
        pageSub.setForeground(UITheme.TEXT_MUTED);

        headerInfo.add(pageTitle);
        headerInfo.add(Box.createVerticalStrut(2));
        headerInfo.add(pageSub);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        headerActions.setOpaque(false);

        ModernButton addTeachBtn = new ModernButton("+ Teach Skill", ModernButton.Style.PRIMARY);
        addTeachBtn.addActionListener(e -> openAddDialog("TEACH"));

        ModernButton addLearnBtn = new ModernButton("+ Learn Skill", ModernButton.Style.OUTLINE);
        addLearnBtn.addActionListener(e -> openAddDialog("LEARN"));

        headerActions.add(addTeachBtn);
        headerActions.add(addLearnBtn);

        headerCard.add(headerInfo, BorderLayout.CENTER);
        headerCard.add(headerActions, BorderLayout.EAST);

        contentPanel.add(headerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Two-Column Layout for TEACH vs LEARN tables
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setOpaque(false);

        // --- Left Table: Skills I Can Teach ---
        ModernCard teachCard = new ModernCard(new BorderLayout(0, 12), 12);
        JPanel teachHeader = new JPanel(new BorderLayout());
        teachHeader.setOpaque(false);

        JLabel teachTitle = new JLabel("💡 Skills I Can Teach");
        teachTitle.setFont(UITheme.FONT_SECTION);
        teachTitle.setForeground(UITheme.TEXT_DARK);

        ModernButton deleteTeachBtn = new ModernButton("Delete Selected", ModernButton.Style.DANGER);
        deleteTeachBtn.setFont(UITheme.FONT_SMALL_BOLD);
        deleteTeachBtn.setPreferredSize(new Dimension(125, 30));
        deleteTeachBtn.addActionListener(e -> handleDeleteSkill(teachTable, teachSkills));

        teachHeader.add(teachTitle, BorderLayout.WEST);
        teachHeader.add(deleteTeachBtn, BorderLayout.EAST);
        teachCard.add(teachHeader, BorderLayout.NORTH);

        teachModel = new DefaultTableModel(new String[]{"ID", "Skill Name", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teachTable = createStyledTable(teachModel);
        teachCard.add(new JScrollPane(teachTable), BorderLayout.CENTER);

        // --- Right Table: Skills I Want to Learn ---
        ModernCard learnCard = new ModernCard(new BorderLayout(0, 12), 12);
        JPanel learnHeader = new JPanel(new BorderLayout());
        learnHeader.setOpaque(false);

        JLabel learnTitle = new JLabel("🎯 Skills I Want to Learn");
        learnTitle.setFont(UITheme.FONT_SECTION);
        learnTitle.setForeground(UITheme.TEXT_DARK);

        ModernButton deleteLearnBtn = new ModernButton("Delete Selected", ModernButton.Style.DANGER);
        deleteLearnBtn.setFont(UITheme.FONT_SMALL_BOLD);
        deleteLearnBtn.setPreferredSize(new Dimension(125, 30));
        deleteLearnBtn.addActionListener(e -> handleDeleteSkill(learnTable, learnSkills));

        learnHeader.add(learnTitle, BorderLayout.WEST);
        learnHeader.add(deleteLearnBtn, BorderLayout.EAST);
        learnCard.add(learnHeader, BorderLayout.NORTH);

        learnModel = new DefaultTableModel(new String[]{"ID", "Skill Name", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        learnTable = createStyledTable(learnModel);
        learnCard.add(new JScrollPane(learnTable), BorderLayout.CENTER);

        tablesPanel.add(teachCard);
        tablesPanel.add(learnCard);

        contentPanel.add(tablesPanel);

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

        // Hide ID column from view while keeping data accessible
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        return table;
    }

    public void refreshData() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                teachSkills = skillService.getTeachingSkills(current.getStudentId());
                learnSkills = skillService.getLearningSkills(current.getStudentId());
                return null;
            }

            @Override
            protected void done() {
                // Populate TEACH table
                teachModel.setRowCount(0);
                if (teachSkills != null) {
                    for (Skill s : teachSkills) {
                        teachModel.addRow(new Object[]{s.getSkillId(), s.getSkillName(), "Teaching"});
                    }
                }

                // Populate LEARN table
                learnModel.setRowCount(0);
                if (learnSkills != null) {
                    for (Skill s : learnSkills) {
                        learnModel.addRow(new Object[]{s.getSkillId(), s.getSkillName(), "Learning"});
                    }
                }
            }
        };
        worker.execute();
    }

    private void openAddDialog(String type) {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        AddSkillDialog dlg = new AddSkillDialog(mainFrame, skillService, current.getStudentId(), type, this::refreshData);
        dlg.setVisible(true);
    }

    private void handleDeleteSkill(JTable table, List<Skill> list) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a skill to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        int skillId = (int) table.getValueAt(selectedRow, 0);
        String skillName = (String) table.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete '" + skillName + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return skillService.deleteSkill(skillId, current.getStudentId());
                }

                @Override
                protected void done() {
                    refreshData();
                }
            };
            worker.execute();
        }
    }
}
