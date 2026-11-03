package com.skillshare.ui;

import com.skillshare.model.Student;
import com.skillshare.service.AuthService;
import com.skillshare.service.ExchangeService;
import com.skillshare.service.MatchingService;
import com.skillshare.service.ReviewService;
import com.skillshare.service.SessionService;
import com.skillshare.service.SkillService;
import com.skillshare.ui.auth.AuthDialog;
import com.skillshare.ui.common.ModernButton;
import com.skillshare.ui.common.UITheme;
import com.skillshare.ui.dashboard.DashboardPanel;
import com.skillshare.ui.matches.FindMatchesPanel;
import com.skillshare.ui.profile.ProfilePanel;
import com.skillshare.ui.requests.ExchangeRequestsPanel;
import com.skillshare.ui.reviews.ReviewsPanel;
import com.skillshare.ui.sessions.SessionsPanel;
import com.skillshare.ui.skills.MySkillsPanel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Main application window for SkillShare featuring a persistent left-side navigation sidebar
 * and a CardLayout center area hosting all 7 main screens.
 */
public class MainFrame extends JFrame {
    public static final String VIEW_DASHBOARD = "DASHBOARD";
    public static final String VIEW_SKILLS = "SKILLS";
    public static final String VIEW_MATCHES = "MATCHES";
    public static final String VIEW_REQUESTS = "REQUESTS";
    public static final String VIEW_SESSIONS = "SESSIONS";
    public static final String VIEW_REVIEWS = "REVIEWS";
    public static final String VIEW_PROFILE = "PROFILE";

    private final AuthService authService;
    private final SkillService skillService;
    private final MatchingService matchingService;
    private final ExchangeService exchangeService;
    private final SessionService sessionService;
    private final ReviewService reviewService;

    private Student currentStudent;

    private CardLayout cardLayout;
    private JPanel mainContentArea;

    private DashboardPanel dashboardPanel;
    private MySkillsPanel mySkillsPanel;
    private FindMatchesPanel findMatchesPanel;
    private ExchangeRequestsPanel exchangeRequestsPanel;
    private SessionsPanel sessionsPanel;
    private ReviewsPanel reviewsPanel;
    private ProfilePanel profilePanel;

    private JLabel studentNameLabel;
    private JLabel studentDeptLabel;

    private final Map<String, ModernButton> navButtons = new HashMap<>();
    private String activeView = VIEW_DASHBOARD;

    public MainFrame(AuthService authService, SkillService skillService, MatchingService matchingService,
                     ExchangeService exchangeService, SessionService sessionService, ReviewService reviewService) {
        super("SkillShare – Campus Skill Exchange System");
        this.authService = authService;
        this.skillService = skillService;
        this.matchingService = matchingService;
        this.exchangeService = exchangeService;
        this.sessionService = sessionService;
        this.reviewService = reviewService;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left Navigation Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Center View Area with CardLayout
        cardLayout = new CardLayout();
        mainContentArea = new JPanel(cardLayout);
        mainContentArea.setBackground(UITheme.BG_MAIN);

        dashboardPanel = new DashboardPanel(this, skillService, matchingService, exchangeService, sessionService, reviewService);
        mySkillsPanel = new MySkillsPanel(this, skillService);
        findMatchesPanel = new FindMatchesPanel(this, matchingService, exchangeService);
        exchangeRequestsPanel = new ExchangeRequestsPanel(this, exchangeService);
        sessionsPanel = new SessionsPanel(this, sessionService, exchangeService);
        reviewsPanel = new ReviewsPanel(this, reviewService);
        profilePanel = new ProfilePanel(this, authService, skillService);

        mainContentArea.add(dashboardPanel, VIEW_DASHBOARD);
        mainContentArea.add(mySkillsPanel, VIEW_SKILLS);
        mainContentArea.add(findMatchesPanel, VIEW_MATCHES);
        mainContentArea.add(exchangeRequestsPanel, VIEW_REQUESTS);
        mainContentArea.add(sessionsPanel, VIEW_SESSIONS);
        mainContentArea.add(reviewsPanel, VIEW_REVIEWS);
        mainContentArea.add(profilePanel, VIEW_PROFILE);

        add(mainContentArea, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 760));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        // Brand Logo & Title
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        brandPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));

        JLabel logoIcon = new JLabel("🎓");
        logoIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));

        JLabel brandTitle = new JLabel("SkillShare");
        brandTitle.setFont(UITheme.FONT_LOGO);
        brandTitle.setForeground(Color.WHITE);

        brandPanel.add(logoIcon);
        brandPanel.add(brandTitle);

        sidebar.add(brandPanel);
        sidebar.add(Box.createVerticalStrut(24));

        // Navigation Menu Buttons
        addNavButton(sidebar, "📊 Dashboard", VIEW_DASHBOARD);
        addNavButton(sidebar, "💡 My Skills", VIEW_SKILLS);
        addNavButton(sidebar, "🔍 Find Matches", VIEW_MATCHES);
        addNavButton(sidebar, "📬 Exchange Requests", VIEW_REQUESTS);
        addNavButton(sidebar, "📅 Sessions", VIEW_SESSIONS);
        addNavButton(sidebar, "⭐ Reviews", VIEW_REVIEWS);
        addNavButton(sidebar, "👤 My Profile", VIEW_PROFILE);

        sidebar.add(Box.createVerticalGlue());

        // User info box at bottom of sidebar
        JPanel userBox = new JPanel();
        userBox.setLayout(new BoxLayout(userBox, BoxLayout.Y_AXIS));
        userBox.setOpaque(false);
        userBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(12, 4, 12, 4)
        ));
        userBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));

        studentNameLabel = new JLabel("Not logged in");
        studentNameLabel.setFont(UITheme.FONT_BODY_BOLD);
        studentNameLabel.setForeground(Color.WHITE);

        studentDeptLabel = new JLabel("Please sign in");
        studentDeptLabel.setFont(UITheme.FONT_SMALL);
        studentDeptLabel.setForeground(UITheme.SIDEBAR_TEXT_MUTED);

        userBox.add(studentNameLabel);
        userBox.add(Box.createVerticalStrut(2));
        userBox.add(studentDeptLabel);

        sidebar.add(userBox);
        sidebar.add(Box.createVerticalStrut(6));

        // Logout Button
        ModernButton logoutBtn = new ModernButton("🚪 Logout", ModernButton.Style.SIDEBAR);
        logoutBtn.addActionListener(e -> handleLogout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String label, String viewName) {
        ModernButton btn = new ModernButton(label, ModernButton.Style.SIDEBAR);
        btn.addActionListener(e -> showView(viewName));
        navButtons.put(viewName, btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(4));
    }

    public void showView(String viewName) {
        this.activeView = viewName;
        cardLayout.show(mainContentArea, viewName);

        // Highlight active nav button
        navButtons.forEach((name, btn) -> {
            if (name.equals(viewName)) {
                btn.setBackground(UITheme.SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(0, 0, 0, 0));
                btn.setForeground(UITheme.SIDEBAR_TEXT);
            }
        });

        // Trigger refresh on the newly shown panel
        switch (viewName) {
            case VIEW_DASHBOARD:
                dashboardPanel.refreshData();
                break;
            case VIEW_SKILLS:
                mySkillsPanel.refreshData();
                break;
            case VIEW_MATCHES:
                findMatchesPanel.refreshData();
                break;
            case VIEW_REQUESTS:
                exchangeRequestsPanel.refreshData();
                break;
            case VIEW_SESSIONS:
                sessionsPanel.refreshData();
                break;
            case VIEW_REVIEWS:
                reviewsPanel.refreshData();
                break;
            case VIEW_PROFILE:
                profilePanel.refreshData();
                break;
        }
    }

    public void onLoginSuccessful(Student student) {
        this.currentStudent = student;
        updateSidebarStudentInfo(student);
        setVisible(true);
        showView(VIEW_DASHBOARD);
    }

    public void updateSidebarStudentInfo(Student student) {
        if (student != null) {
            studentNameLabel.setText(student.getName());
            studentDeptLabel.setText(student.getDepartment());
        } else {
            studentNameLabel.setText("Not logged in");
            studentDeptLabel.setText("Please sign in");
        }
    }

    public void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of SkillShare?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            this.currentStudent = null;
            setVisible(false);
            openAuthDialog();
        }
    }

    public void openAuthDialog() {
        AuthDialog dialog = new AuthDialog(this, authService, this);
        dialog.setVisible(true);
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }
}
