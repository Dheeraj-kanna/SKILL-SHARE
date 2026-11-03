package com.skillshare.ui.sessions;

import com.skillshare.model.ExchangeRequest;
import com.skillshare.model.Session;
import com.skillshare.service.ExchangeService;
import com.skillshare.service.SessionService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Dialog to schedule a session for an accepted exchange.
 */
public class ScheduleSessionDialog extends JDialog {
    private final SessionService sessionService;
    private final ExchangeService exchangeService;
    private final int currentStudentId;
    private final Runnable onSuccess;

    private JComboBox<ExchangeItem> exchangeCombo;
    private ModernTextField dateField;
    private ModernTextField timeField;
    private ModernTextField locationField;
    private JLabel statusLabel;
    private ModernButton scheduleButton;

    public static class ExchangeItem {
        final int exchangeId;
        final String displayText;

        public ExchangeItem(int exchangeId, String displayText) {
            this.exchangeId = exchangeId;
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    public ScheduleSessionDialog(JFrame parent, SessionService sessionService, ExchangeService exchangeService,
                                 int currentStudentId, Runnable onSuccess) {
        super(parent, "Schedule Learning Session", true);
        this.sessionService = sessionService;
        this.exchangeService = exchangeService;
        this.currentStudentId = currentStudentId;
        this.onSuccess = onSuccess;

        initUI();
        loadAcceptedExchanges();
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

        JLabel titleLbl = new JLabel("Schedule a Learning Session");
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 24, 12, 24);
        form.add(titleLbl, gbc);

        // Exchange Dropdown
        JLabel exLbl = new JLabel("Select Accepted Exchange");
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

        // Date Field
        JLabel dateLbl = new JLabel("Session Date (YYYY-MM-DD)");
        dateLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 3;
        gbc.insets = new Insets(2, 24, 2, 24);
        form.add(dateLbl, gbc);

        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateField = new ModernTextField(tomorrow);
        dateField.setText(tomorrow);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 24, 10, 24);
        form.add(dateField, gbc);

        // Time Field
        JLabel timeLbl = new JLabel("Session Time (HH:MM or HH:MM:SS)");
        timeLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 5;
        gbc.insets = new Insets(2, 24, 2, 24);
        form.add(timeLbl, gbc);

        timeField = new ModernTextField("14:00");
        timeField.setText("14:00");
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 24, 10, 24);
        form.add(timeField, gbc);

        // Location Field
        JLabel locLbl = new JLabel("Meeting Location");
        locLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 7;
        gbc.insets = new Insets(2, 24, 2, 24);
        form.add(locLbl, gbc);

        locationField = new ModernTextField("Campus Library - Room 204 or Google Meet link");
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 24, 8, 24);
        form.add(locationField, gbc);

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 24, 8, 24);
        form.add(statusLabel, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.Style.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());

        scheduleButton = new ModernButton("Schedule Session", ModernButton.Style.PRIMARY);
        scheduleButton.addActionListener(e -> handleSchedule());

        btnPanel.add(cancelBtn);
        btnPanel.add(scheduleButton);

        gbc.gridy = 10;
        gbc.insets = new Insets(4, 24, 16, 24);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void loadAcceptedExchanges() {
        SwingWorker<List<ExchangeRequest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExchangeRequest> doInBackground() throws Exception {
                return exchangeService.getAcceptedExchanges(currentStudentId);
            }

            @Override
            protected void done() {
                try {
                    List<ExchangeRequest> list = get();
                    exchangeCombo.removeAllItems();
                    if (list.isEmpty()) {
                        statusLabel.setText("No accepted exchanges found to schedule.");
                        scheduleButton.setEnabled(false);
                    } else {
                        for (ExchangeRequest ex : list) {
                            String partner = (ex.getRequesterId() == currentStudentId) ? ex.getReceiverName() : ex.getRequesterName();
                            exchangeCombo.addItem(new ExchangeItem(
                                    ex.getExchangeId(),
                                    partner + " - " + ex.getSkillName()
                            ));
                        }
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load exchanges: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleSchedule() {
        ExchangeItem selected = (ExchangeItem) exchangeCombo.getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an accepted exchange.");
            return;
        }

        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String loc = locationField.getText().trim();

        if (date.isEmpty() || time.isEmpty() || loc.isEmpty()) {
            statusLabel.setText("All session details are required.");
            return;
        }

        scheduleButton.setEnabled(false);
        scheduleButton.setText("Scheduling...");
        statusLabel.setText(" ");

        SwingWorker<Session, Void> worker = new SwingWorker<>() {
            @Override
            protected Session doInBackground() throws Exception {
                return sessionService.scheduleSession(selected.exchangeId, date, time, loc);
            }

            @Override
            protected void done() {
                try {
                    get();
                    dispose();
                    if (onSuccess != null) onSuccess.run();
                } catch (Exception ex) {
                    scheduleButton.setEnabled(true);
                    scheduleButton.setText("Schedule Session");
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setText(cause.getMessage());
                }
            }
        };
        worker.execute();
    }
}

