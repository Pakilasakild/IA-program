package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RoleDAO extends BaseDAO<Role> {
    public List<Role> findAll() throws SQLException {
        String sql = "SELECT * from roles";
        return executeQuery(sql);
    }

    public Role findById(int id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id = ?";
        List<Role> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.getFirst();
    }

    public Role findByName(String name) throws SQLException {
        String sql = "SELECT * FROM roles WHERE name = ?";
        List<Role> results = executeQuery(sql, name);
        return results.isEmpty() ? null : results.getFirst();
    }

    public int create(Role entity) throws SQLException {
        String sql = "INSERT INTO roles (name) VALUES (?)";
        return executeUpdate(sql, entity.getName());
    }

    public int update(Role entity) throws SQLException {
        String sql = "UPDATE roles SET name = ? WHERE id = ?";
        return executeUpdate(sql, entity.getName(), entity.getId());
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM roles WHERE id = ?";
        return executeUpdate(sql, id);
    }

    @Override
    protected Role mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Role(rs.getInt("id"), rs.getString("name"));
    }
}
