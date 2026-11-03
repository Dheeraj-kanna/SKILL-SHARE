package com.skillshare.ui.auth;

import com.skillshare.model.Student;
import com.skillshare.service.AuthService;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.ModernPasswordField;
import com.skillshare.ui.common.ModernTextField;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Registration panel for new student sign up.
 */
public class RegisterPanel extends JPanel {
    private final AuthService authService;
    private final AuthDialog parentDialog;

    private ModernTextField nameField;
    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private ModernTextField deptField;
    private JLabel statusLabel;
    private ModernButton registerButton;

    public RegisterPanel(AuthService authService, AuthDialog parentDialog) {
        this.authService = authService;
        this.parentDialog = parentDialog;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 40, 4, 40);
        gbc.gridx = 0;

        // Title & subtitle
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitleLabel = new JLabel("Join the campus skill exchange community");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 0;
        gbc.insets = new Insets(10, 40, 2, 40);
        contentPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 15, 40);
        contentPanel.add(subtitleLabel, gbc);

        // Student Name
        JLabel nameLbl = new JLabel("Full Name");
        nameLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 40, 2, 40);
        contentPanel.add(nameLbl, gbc);

        nameField = new ModernTextField("e.g. Alex Johnson");
        nameField.setPreferredSize(new Dimension(300, 36));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 40, 8, 40);
        contentPanel.add(nameField, gbc);

        // Email
        JLabel emailLbl = new JLabel("College Email");
        emailLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 4;
        gbc.insets = new Insets(2, 40, 2, 40);
        contentPanel.add(emailLbl, gbc);

        emailField = new ModernTextField("alex@college.edu");
        emailField.setPreferredSize(new Dimension(300, 36));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 40, 8, 40);
        contentPanel.add(emailField, gbc);

        // Password
        JLabel pwdLbl = new JLabel("Password (min 4 characters)");
        pwdLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 6;
        gbc.insets = new Insets(2, 40, 2, 40);
        contentPanel.add(pwdLbl, gbc);

        passwordField = new ModernPasswordField("Choose a secure password");
        passwordField.setPreferredSize(new Dimension(300, 36));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 40, 8, 40);
        contentPanel.add(passwordField, gbc);

        // Department
        JLabel deptLbl = new JLabel("Department / Major");
        deptLbl.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = 8;
        gbc.insets = new Insets(2, 40, 2, 40);
        contentPanel.add(deptLbl, gbc);

        deptField = new ModernTextField("e.g. Computer Science");
        deptField.setPreferredSize(new Dimension(300, 36));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 40, 8, 40);
        contentPanel.add(deptField, gbc);

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 40, 8, 40);
        contentPanel.add(statusLabel, gbc);

        // Register Button
        registerButton = new ModernButton("Create Account", ModernButton.Style.PRIMARY);
        registerButton.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 11;
        gbc.insets = new Insets(0, 40, 10, 40);
        contentPanel.add(registerButton, gbc);

        // Back to login link
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchPanel.setBackground(Color.WHITE);
        JLabel hasAccLbl = new JLabel("Already have an account?");
        hasAccLbl.setFont(UITheme.FONT_SMALL);
        hasAccLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel loginLink = new JLabel("Sign In");
        loginLink.setFont(UITheme.FONT_SMALL_BOLD);
        loginLink.setForeground(UITheme.PRIMARY);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentDialog.showLogin();
            }
        });

        switchPanel.add(hasAccLbl);
        switchPanel.add(loginLink);

        gbc.gridy = 12;
        gbc.insets = new Insets(0, 40, 15, 40);
        contentPanel.add(switchPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Enter key listeners
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performRegister();
                }
            }
        };
        nameField.addKeyListener(enterKey);
        emailField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
        deptField.addKeyListener(enterKey);
        registerButton.addActionListener(e -> performRegister());
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String dept = deptField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dept.isEmpty()) {
            statusLabel.setText("Please fill in all registration fields.");
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setText("Creating account in MySQL...");

        SwingWorker<Student, Void> worker = new SwingWorker<>() {
            @Override
            protected Student doInBackground() throws Exception {
                return authService.register(name, email, password, dept);
            }

            @Override
            protected void done() {
                registerButton.setEnabled(true);
                registerButton.setText("Create Account");
                try {
                    Student registered = get();
                    statusLabel.setForeground(UITheme.SUCCESS);
                    statusLabel.setText("Registration successful! Logging you in...");
                    parentDialog.onAuthenticationSuccess(registered);
                } catch (Exception ex) {
                    statusLabel.setForeground(UITheme.DANGER);
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setText(cause.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        deptField.setText("");
        statusLabel.setText(" ");
    }
}
