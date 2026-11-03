package com.skillshare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application and database configurations.
 * Prioritizes Environment Variables > Local config.properties > Classpath defaults.
 */
public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/campus_skill_exchange?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Dheeraj@2712";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfig();
    }

    public static synchronized void loadConfig() {
        Properties props = new Properties();

        // 1. Try classpath resource
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }

        // 2. Try local config.properties if present
        File localFile = new File(CONFIG_FILE);
        if (localFile.exists()) {
            try (FileInputStream fis = new FileInputStream(localFile)) {
                props.load(fis);
            } catch (IOException ignored) {
            }
        }

        // 3. Fallback resolution: Env var > properties > defaults
        String envUrl = System.getenv("CAMPUS_DB_URL");
        String envUser = System.getenv("CAMPUS_DB_USER");
        String envPassword = System.getenv("CAMPUS_DB_PASSWORD");

        dbUrl = (envUrl != null && !envUrl.isBlank()) ? envUrl :
                props.getProperty("db.url", DEFAULT_URL);

        dbUser = (envUser != null && !envUser.isBlank()) ? envUser :
                props.getProperty("db.user", DEFAULT_USER);

        dbPassword = (envPassword != null) ? envPassword :
                props.getProperty("db.password", DEFAULT_PASSWORD);
    }

    public static synchronized String getDbUrl() {
        return dbUrl;
    }

    public static synchronized void setDbUrl(String url) {
        dbUrl = url;
    }

    public static synchronized String getDbUser() {
        return dbUser;
    }

    public static synchronized void setDbUser(String user) {
        dbUser = user;
    }

    public static synchronized String getDbPassword() {
        return dbPassword;
    }

    public static synchronized void setDbPassword(String password) {
        dbPassword = password;
    }

    public static synchronized void saveConfig(String url, String user, String password) throws IOException {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;

        Properties props = new Properties();
        props.setProperty("db.url", url);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password != null ? password : "");

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "SkillShare Database Configuration");
        }
    }
}
