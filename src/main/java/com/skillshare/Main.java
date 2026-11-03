package com.skillshare;

import com.skillshare.config.DBConnection;
import com.skillshare.service.AuthService;
import com.skillshare.service.ExchangeService;
import com.skillshare.service.MatchingService;
import com.skillshare.service.ReviewService;
import com.skillshare.service.SessionService;
import com.skillshare.service.SkillService;
import com.skillshare.ui.MainFrame;
import com.skillshare.ui.common.UITheme;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main application entry point for SkillShare Campus Skill Exchange.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Setup Swing Look & Feel
        UITheme.setupLookAndFeel();

        // 2. Initialize and verify database connection
        try {
            DBConnection.initializeDatabase();
            System.out.println("[SkillShare] Database verified and tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[SkillShare] Database connection warning: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        "Could not connect to MySQL database:\n" + e.getMessage() +
                                "\n\nPlease ensure MySQL is running and check your credentials.",
                        "Database Connection Notice",
                        JOptionPane.WARNING_MESSAGE
                );
            });
        }

        // 3. Initialize Services
        AuthService authService = new AuthService();
        SkillService skillService = new SkillService();
        MatchingService matchingService = new MatchingService();
        ExchangeService exchangeService = new ExchangeService();
        SessionService sessionService = new SessionService();
        ReviewService reviewService = new ReviewService();

        // 4. Launch UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(
                    authService,
                    skillService,
                    matchingService,
                    exchangeService,
                    sessionService,
                    reviewService
            );

            // Present Login/Register dialog
            mainFrame.openAuthDialog();
        });
    }
}

