package com.skillshare.ui.auth;

import com.skillshare.model.Student;
import com.skillshare.service.AuthService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.UITheme;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Authentication dialog hosting Login and Register screens with a branded decorative side panel.
 */
public class AuthDialog extends JDialog {
    private final AuthService authService;
    private final MainFrame mainFrame;

    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    public AuthDialog(JFrame owner, AuthService authService, MainFrame mainFrame) {
        super(owner, "SkillShare – Campus Skill Exchange", true);
        this.authService = authService;
        this.mainFrame = mainFrame;

        initUI();
    }

    private void initUI() {
        setSize(820, 540);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Branding Panel (Indigo gradient)
        JPanel brandingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.applyAntiAliasing(g2);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(30, 27, 75), // Deep navy indigo
                        getWidth(), getHeight(), new Color(79, 70, 229) // Vibrant purple/indigo
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-50, -50, 220, 220);
                g2.fillOval(getWidth() - 100, getHeight() - 120, 200, 200);

                g2.dispose();
            }
        };
        brandingPanel.setPreferredSize(new Dimension(340, 540));
        brandingPanel.setLayout(null);

        JLabel logoIcon = new JLabel("🎓");
        logoIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 46));
        logoIcon.setBounds(40, 80, 60, 60);
        brandingPanel.add(logoIcon);

        JLabel appTitle = new JLabel("SkillShare");
        appTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        appTitle.setForeground(Color.WHITE);
        appTitle.setBounds(40, 145, 260, 36);
        brandingPanel.add(appTitle);

        JLabel appSub = new JLabel("Campus Skill Exchange");
        appSub.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        appSub.setForeground(new Color(199, 210, 254));
        appSub.setBounds(40, 180, 260, 24);
        brandingPanel.add(appSub);

        JLabel tagline = new JLabel("<html>Share Skills.<br>Learn Together.<br>Grow Together.</html>");
        tagline.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        tagline.setForeground(new Color(224, 231, 255));
        tagline.setBounds(40, 240, 260, 90);
        brandingPanel.add(tagline);

        JLabel footerNote = new JLabel("<html>Empowering students through peer knowledge exchange.</html>");
        footerNote.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        footerNote.setForeground(new Color(165, 180, 252));
        footerNote.setBounds(40, 440, 260, 40);
        brandingPanel.add(footerNote);

        // Right Content Panel with CardLayout
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(Color.WHITE);

        loginPanel = new LoginPanel(authService, this);
        registerPanel = new RegisterPanel(authService, this);

        cardsPanel.add(loginPanel, "LOGIN");
        cardsPanel.add(registerPanel, "REGISTER");

        add(brandingPanel, BorderLayout.WEST);
        add(cardsPanel, BorderLayout.CENTER);

        showLogin();
    }

    public void showLogin() {
        loginPanel.clearFields();
        cardLayout.show(cardsPanel, "LOGIN");
    }

    public void showRegister() {
        registerPanel.clearFields();
        cardLayout.show(cardsPanel, "REGISTER");
    }

    public void onAuthenticationSuccess(Student student) {
        dispose();
        mainFrame.onLoginSuccessful(student);
    }
}
