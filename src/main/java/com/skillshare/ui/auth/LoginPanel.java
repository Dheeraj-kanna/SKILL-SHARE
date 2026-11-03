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
 * Login screen panel with email, password fields, validation and loading states.
 */
public class LoginPanel extends JPanel {
    private final AuthService authService;
    private final AuthDialog parentDialog;

    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private JLabel statusLabel;
    private ModernButton loginButton;

    public LoginPanel(AuthService authService, AuthDialog parentDialog) {
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
        gbc.insets = new Insets(6, 40, 6, 40);
        gbc.gridx = 0;

        // Title & subtitle
        JLabel titleLabel = new JLabel("Welcome back!");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitleLabel = new JLabel("Log in to continue sharing skills");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 0;
        gbc.insets = new Insets(20, 40, 4, 40);
        contentPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 20, 40);
        contentPanel.add(subtitleLabel, gbc);

        // Email field
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(UITheme.FONT_BODY_BOLD);
        emailLbl.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 40, 2, 40);
        contentPanel.add(emailLbl, gbc);

        emailField = new ModernTextField("student@college.edu");
        emailField.setPreferredSize(new Dimension(300, 38));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 40, 10, 40);
        contentPanel.add(emailField, gbc);

        // Password field
        JLabel pwdLbl = new JLabel("Password");
        pwdLbl.setFont(UITheme.FONT_BODY_BOLD);
        pwdLbl.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 4;
        gbc.insets = new Insets(4, 40, 2, 40);
        contentPanel.add(pwdLbl, gbc);

        passwordField = new ModernPasswordField("Enter your password");
        passwordField.setPreferredSize(new Dimension(300, 38));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 40, 10, 40);
        contentPanel.add(passwordField, gbc);

        // Status / error message label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 40, 10, 40);
        contentPanel.add(statusLabel, gbc);

        // Login Button
        loginButton = new ModernButton("Sign In", ModernButton.Style.PRIMARY);
        loginButton.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 40, 15, 40);
        contentPanel.add(loginButton, gbc);

        // Register link
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchPanel.setBackground(Color.WHITE);
        JLabel noAccLbl = new JLabel("Don't have an account?");
        noAccLbl.setFont(UITheme.FONT_SMALL);
        noAccLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel registerLink = new JLabel("Register here");
        registerLink.setFont(UITheme.FONT_SMALL_BOLD);
        registerLink.setForeground(UITheme.PRIMARY);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentDialog.showRegister();
            }
        });

        switchPanel.add(noAccLbl);
        switchPanel.add(registerLink);

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 40, 20, 40);
        contentPanel.add(switchPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Enter key listeners
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        emailField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setText("Authenticating credentials...");

        SwingWorker<Student, Void> worker = new SwingWorker<>() {
            @Override
            protected Student doInBackground() throws Exception {
                return authService.login(email, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                try {
                    Student loggedIn = get();
                    statusLabel.setForeground(UITheme.SUCCESS);
                    statusLabel.setText("Success! Redirecting...");
                    parentDialog.onAuthenticationSuccess(loggedIn);
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
        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
