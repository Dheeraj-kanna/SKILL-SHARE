package com.skillshare.ui.sessions;

import com.skillshare.model.Session;
import com.skillshare.model.Student;
import com.skillshare.service.ExchangeService;
import com.skillshare.service.SessionService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.BadgeLabel;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernCard;
import com.skillshare.ui.common.UITheme;
import com.skillshare.util.DateUtil;

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
 * Screen for tracking Upcoming and Past Learning Sessions.
 */
public class SessionsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SessionService sessionService;
    private final ExchangeService exchangeService;

    private JTabbedPane tabbedPane;
    private JTable upcomingTable;
    private DefaultTableModel upcomingModel;

    private JTable pastTable;
    private DefaultTableModel pastModel;

    public SessionsPanel(MainFrame mainFrame, SessionService sessionService, ExchangeService exchangeService) {
        this.mainFrame = mainFrame;
        this.sessionService = sessionService;
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

        JLabel pageTitle = new JLabel("Learning Sessions");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_DARK);

        JLabel pageSub = new JLabel("Schedule meeting times, view upcoming peer sessions, and track past learning history.");
        pageSub.setFont(UITheme.FONT_BODY);
        pageSub.setForeground(UITheme.TEXT_MUTED);

        headerInfo.add(pageTitle);
        headerInfo.add(Box.createVerticalStrut(2));
        headerInfo.add(pageSub);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        headerActions.setOpaque(false);

        ModernButton scheduleBtn = new ModernButton("+ Schedule Session", ModernButton.Style.PRIMARY);
        scheduleBtn.addActionListener(e -> openScheduleDialog());

        ModernButton refreshBtn = new ModernButton("🔄 Refresh", ModernButton.Style.OUTLINE);
        refreshBtn.addActionListener(e -> refreshData());

        headerActions.add(refreshBtn);
        headerActions.add(scheduleBtn);

        headerCard.add(headerInfo, BorderLayout.CENTER);
        headerCard.add(headerActions, BorderLayout.EAST);

        contentPanel.add(headerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);

        // Upcoming Tab
        JPanel upcomingPanel = new JPanel(new BorderLayout());
        upcomingPanel.setBackground(Color.WHITE);
        upcomingPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        upcomingModel = new DefaultTableModel(new String[]{"ID", "Partner Student", "Skill Exchanged", "Date", "Time", "Location", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        upcomingTable = createStyledTable(upcomingModel);
        upcomingPanel.add(new JScrollPane(upcomingTable), BorderLayout.CENTER);
        tabbedPane.addTab("⏰ Upcoming Sessions", upcomingPanel);

        // Past Tab
        JPanel pastPanel = new JPanel(new BorderLayout());
        pastPanel.setBackground(Color.WHITE);
        pastPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        pastModel = new DefaultTableModel(new String[]{"ID", "Partner Student", "Skill Exchanged", "Date", "Time", "Location", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        pastTable = createStyledTable(pastModel);
        pastPanel.add(new JScrollPane(pastTable), BorderLayout.CENTER);
        tabbedPane.addTab("📜 Previous Sessions", pastPanel);

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

        return table;
    }

    public void refreshData() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Session> upcoming;
            private List<Session> past;

            @Override
            protected Void doInBackground() throws Exception {
                upcoming = sessionService.getUpcomingSessions(current.getStudentId());
                past = sessionService.getPastSessions(current.getStudentId());
                return null;
            }

            @Override
            protected void done() {
                // Populate upcoming
                upcomingModel.setRowCount(0);
                if (upcoming != null) {
                    for (Session s : upcoming) {
                        upcomingModel.addRow(new Object[]{
                                s.getSessionId(),
                                s.getPartnerName(),
                                s.getSkillName(),
                                DateUtil.formatFriendlyDate(s.getSessionDate()),
                                DateUtil.formatFriendlyTime(s.getSessionTime()),
                                s.getLocation(),
                                s.getStatus()
                        });
                    }
                }

                // Populate past
                pastModel.setRowCount(0);
                if (past != null) {
                    for (Session s : past) {
                        pastModel.addRow(new Object[]{
                                s.getSessionId(),
                                s.getPartnerName(),
                                s.getSkillName(),
                                DateUtil.formatFriendlyDate(s.getSessionDate()),
                                DateUtil.formatFriendlyTime(s.getSessionTime()),
                                s.getLocation(),
                                s.getStatus()
                        });
                    }
                }
            }
        };
        worker.execute();
    }

    private void openScheduleDialog() {
        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        ScheduleSessionDialog dlg = new ScheduleSessionDialog(
                mainFrame, sessionService, exchangeService, current.getStudentId(), this::refreshData
        );
        dlg.setVisible(true);
    }
}

