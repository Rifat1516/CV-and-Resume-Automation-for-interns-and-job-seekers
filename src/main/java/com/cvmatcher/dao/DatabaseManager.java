package com.cvmatcher.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton: one shared point of access to the SQLite connection.
 * Justified here because the whole application must agree on a single
 * database file/connection rather than each DAO opening its own.
 */
public final class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:cvmatcher.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }
            initSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() {
        try (InputStream is = getClass().getResourceAsStream("/db/schema.sql")) {
            if (is == null) {
                throw new IOException("schema.sql not found on classpath");
            }
            String sql = new String(is.readAllBytes());
            try (Statement st = connection.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        st.execute(trimmed);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }
}
