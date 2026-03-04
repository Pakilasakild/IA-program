package com.ia.ia_base.database.dao;

import com.ia.ia_base.config.AppConfig;
import com.ia.ia_base.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base DAO (Data Access Object) class.
 * Provides basic methods for working with database.
 */
public abstract class BaseDAO<T> {
    protected DatabaseConnection dbConnection;
    
    public BaseDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Checks if database is used
     */
    protected void checkDatabaseEnabled() throws SQLException {
        if (!AppConfig.isUseDatabase()) {
            throw new SQLException("Database not used. Enable it with AppConfig.setUseDatabase(true)");
        }
    }
    
    /**
     * Executes SELECT query and returns results
     */
    protected List<T> executeQuery(String sql, Object... params) throws SQLException {
        checkDatabaseEnabled();
        
        List<T> results = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs));
                }
            }
        }
        return results;
    }
    
    /**
     * Executes INSERT, UPDATE, DELETE queries
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        checkDatabaseEnabled();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Sets parameters for PreparedStatement object
     */
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
    
    /**
     * Converts ResultSet to Entity object
     * Must be implemented in each child class
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
}

