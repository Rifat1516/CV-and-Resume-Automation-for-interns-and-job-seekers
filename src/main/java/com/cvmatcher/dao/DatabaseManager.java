package com.cvmatcher.dao;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes the SQLite connection and enforces foreign key constraints.
     */
    private DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
            
            try (final Statement statement = this.connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON;");
            }
            
            initSchema();
            
        } catch (final SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    /**
     * Retrieves the single, shared instance of the DatabaseManager.
     * Synchronized to ensure thread safety during initial creation.
     *
     * @return The DatabaseManager instance.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Retrieves the active database connection.
     *
     * @return The SQLite Connection object.
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Reads the schema.sql file from the classpath and executes its statements
     * to ensure the database tables are properly initialized.
     */
    private void initSchema() {
        try (final InputStream inputStream = getClass().getResourceAsStream("/db/schema.sql")) {
            
            if (inputStream == null) {
                throw new IOException("schema.sql not found on classpath");
            }
            
            final String sqlContent = new String(inputStream.readAllBytes());
            
            try (final Statement statement = this.connection.createStatement()) {
                final String[] sqlStatements = sqlContent.split(";");
                
                for (final String singleStatement : sqlStatements) {
                    final String trimmedStatement = singleStatement.trim();
                    
                    if (!trimmedStatement.isEmpty()) {
                        statement.execute(trimmedStatement);
                    }
                }
            }
            
        } catch (final IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }
}
