package com.skillshare.ui.requests;

import com.skillshare.model.ExchangeRequest;
import com.skillshare.model.Student;
import com.skillshare.service.ExchangeService;
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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Screen for managing Exchange Requests:
 * - Received Requests (with Accept and Reject action buttons)
 * - Sent Requests
 * - Accepted Exchanges
 * - Rejected Requests
 */
public class ExchangeRequestsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final ExchangeService exchangeService;

    private JTabbedPane tabbedPane;

    private JTable receivedTable;
    private DefaultTableModel receivedModel;
    private List<ExchangeRequest> receivedList;

    private JTable sentTable;
    private DefaultTableModel sentModel;

    private JTable acceptedTable;
    private DefaultTableModel acceptedModel;

    private JTable rejectedTable;
    private DefaultTableModel rejectedModel;

    public ExchangeRequestsPanel(MainFrame mainFrame, ExchangeService exchangeService) {
        this.mainFrame = mainFrame;
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

        JLabel pageTitle = new JLabel("Exchange Requests");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_DARK);

        JLabel pageSub = new JLabel("Review incoming skill requests, monitor sent invites, and manage accepted exchanges.");
        pageSub.setFont(UITheme.FONT_BODY);
        pageSub.setForeground(UITheme.TEXT_MUTED);

        headerInfo.add(pageTitle);
        headerInfo.add(Box.createVerticalStrut(2));
        headerInfo.add(pageSub);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        headerActions.setOpaque(false);

        ModernButton refreshBtn = new ModernButton("🔄 Refresh", ModernButton.Style.OUTLINE);
        refreshBtn.addActionListener(e -> refreshData());
        headerActions.add(refreshBtn);

        headerCard.add(headerInfo, BorderLayout.CENTER);
        headerCard.add(headerActions, BorderLayout.EAST);

        contentPanel.add(headerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Tabbed Pane for Received / Sent / Accepted / Rejected
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);

        // 1. Received Tab
        JPanel receivedPanel = createReceivedPanel();
        tabbedPane.addTab("📥 Received Requests", receivedPanel);

        // 2. Sent Tab
        JPanel sentPanel = createSentPanel();
        tabbedPane.addTab("📤 Sent Requests", sentPanel);

        // 3. Accepted Tab
        JPanel acceptedPanel = createAcceptedPanel();
        tabbedPane.addTab("✅ Accepted Exchanges", acceptedPanel);

        // 4. Rejected Tab
        JPanel rejectedPanel = createRejectedPanel();
        tabbedPane.addTab("❌ Rejected Exchanges", rejectedPanel);

        ModernCard containerCard = new ModernCard(new BorderLayout(), 12);
        containerCard.add(tabbedPane, BorderLayout.CENTER);

        contentPanel.add(containerCard);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createReceivedPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        ModernButton acceptBtn = new ModernButton("✓ Accept Request", ModernButton.Style.SUCCESS);
        acceptBtn.addActionListener(e -> handleRespond(true));

        ModernButton rejectBtn = new ModernButton("✕ Reject Request", ModernButton.Style.DANGER);
        rejectBtn.addActionListener(e -> handleRespond(false));

        actions.add(rejectBtn);
        actions.add(acceptBtn);

        panel.add(actions, BorderLayout.NORTH);

        receivedModel = new DefaultTableModel(new String[]{"ID", "From Student", "Department", "Skill Requested", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        receivedTable = createStyledTable(receivedModel);
        panel.add(new JScrollPane(receivedTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        sentModel = new DefaultTableModel(new String[]{"ID", "Sent To", "Department", "Skill", "Current Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        sentTable = createStyledTable(sentModel);
        panel.add(new JScrollPane(sentTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAcceptedPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel infoLbl = new JLabel("You can schedule learning sessions or submit reviews for accepted exchanges.");
        infoLbl.setFont(UITheme.FONT_SMALL);
        infoLbl.setForeground(UITheme.TEXT_MUTED);

        ModernButton scheduleBtn = new ModernButton("📅 Schedule Session", ModernButton.Style.PRIMARY);
        scheduleBtn.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_SESSIONS));

        topBar.add(infoLbl, BorderLayout.WEST);
        topBar.add(scheduleBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        acceptedModel = new DefaultTableModel(new String[]{"ID", "Partner Student", "Department", "Skill Exchanged", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        acceptedTable = createStyledTable(acceptedModel);
        panel.add(new JScrollPane(acceptedTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRejectedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        rejectedModel = new DefaultTableModel(new String[]{"ID", "Student", "Department", "Skill", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        rejectedTable = createStyledTable(rejectedModel);
        panel.add(new JScrollPane(rejectedTable), BorderLayout.CENTER);

        return panel;
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
            private List<ExchangeRequest> incoming;
            private List<ExchangeRequest> sent;
            private List<ExchangeRequest> accepted;
            private List<ExchangeRequest> rejected;

            @Override
            protected Void doInBackground() throws Exception {
                incoming = exchangeService.getIncomingRequests(current.getStudentId());
                sent = exchangeService.getSentRequests(current.getStudentId());
                accepted = exchangeService.getAcceptedExchanges(current.getStudentId());
                rejected = exchangeService.getRejectedExchanges(current.getStudentId());
                return null;
            }

            @Override
            protected void done() {
                receivedList = incoming;

                // 1. Populate Received
                receivedModel.setRowCount(0);
                if (incoming != null) {
                    for (ExchangeRequest r : incoming) {
                        receivedModel.addRow(new Object[]{r.getExchangeId(), r.getRequesterName(), r.getRequesterDepartment(), r.getSkillName(), r.getStatus()});
                    }
                }

                // 2. Populate Sent
                sentModel.setRowCount(0);
                if (sent != null) {
                    for (ExchangeRequest r : sent) {
                        sentModel.addRow(new Object[]{r.getExchangeId(), r.getReceiverName(), r.getReceiverDepartment(), r.getSkillName(), r.getStatus()});
                    }
                }

                // 3. Populate Accepted
                acceptedModel.setRowCount(0);
                if (accepted != null) {
                    for (ExchangeRequest r : accepted) {
                        String partner = (r.getRequesterId() == current.getStudentId()) ? r.getReceiverName() : r.getRequesterName();
                        String dept = (r.getRequesterId() == current.getStudentId()) ? r.getReceiverDepartment() : r.getRequesterDepartment();
                        acceptedModel.addRow(new Object[]{r.getExchangeId(), partner, dept, r.getSkillName(), r.getStatus()});
                    }
                }

                // 4. Populate Rejected
                rejectedModel.setRowCount(0);
                if (rejected != null) {
                    for (ExchangeRequest r : rejected) {
                        String partner = (r.getRequesterId() == current.getStudentId()) ? r.getReceiverName() : r.getRequesterName();
                        String dept = (r.getRequesterId() == current.getStudentId()) ? r.getReceiverDepartment() : r.getRequesterDepartment();
                        rejectedModel.addRow(new Object[]{r.getExchangeId(), partner, dept, r.getSkillName(), r.getStatus()});
                    }
                }
            }
        };
        worker.execute();
    }

    private void handleRespond(boolean accept) {
        int selectedRow = receivedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an incoming request to " + (accept ? "accept" : "reject") + ".", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student current = mainFrame.getCurrentStudent();
        if (current == null) return;

        int exchangeId = (int) receivedTable.getValueAt(selectedRow, 0);
        String status = (String) receivedTable.getValueAt(selectedRow, 4);

        if (!"Pending".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This request has already been " + status.toLowerCase() + ".", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return exchangeService.respondToRequest(exchangeId, current.getStudentId(), accept);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(
                                ExchangeRequestsPanel.this,
                                "Request " + (accept ? "accepted" : "rejected") + " successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        refreshData();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            ExchangeRequestsPanel.this,
                            "Failed to update request: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
}

