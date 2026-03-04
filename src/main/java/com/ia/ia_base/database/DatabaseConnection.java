package com.ia.ia_base.database;

import com.ia.ia_base.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for managing database connection.
 * Uses Singleton pattern to ensure only one connection exists.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Database settings - change as needed
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "IAbase";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    
    private DatabaseConnection() {
        // Private constructor - Singleton pattern
    }
    
    /**
     * Gets the single DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Opens connection to database
     * @return Connection object
     * @throws SQLException if connection fails or if DB is not used
     */
    public Connection getConnection() throws SQLException {
        // Check if database should be used
        if (!AppConfig.isUseDatabase()) {
            throw new SQLException("Database not used. Enable it with AppConfig.setUseDatabase(true)");
        }
        
        if (connection == null || connection.isClosed()) {
            try {
                String fullUrl = DB_URL + DB_NAME + "?useSSL=false&serverTimezone=UTC";
                connection = DriverManager.getConnection(fullUrl, DB_USER, DB_PASSWORD);
                System.out.println("Connected to database: " + DB_NAME);
            } catch (SQLException e) {
                System.err.println("Error connecting to database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Closes connection to database
     */
    public void closeConnection() {
        // If DB is not used, do nothing
        if (!AppConfig.isUseDatabase()) {
            return;
        }
        
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Checks if connection is active
     */
    public boolean isConnected() {
        if (!AppConfig.isUseDatabase()) {
            return false;
        }
        
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Sets database configuration
     */
    public static void setDatabaseConfig(String url, String dbName, String user, String password) {
        // Can add setter methods if need to change settings at runtime
    }
}

