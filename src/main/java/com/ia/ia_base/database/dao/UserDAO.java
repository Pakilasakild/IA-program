package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Role;
import com.ia.ia_base.models.StudentUser;
import com.ia.ia_base.models.TeacherUser;
import com.ia.ia_base.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends BaseDAO<User> {

    public List<User> findAll() throws SQLException {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u " + "LEFT JOIN roles r ON u.role_id = r.id";
        return executeQuery(sql);
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u " + "LEFT JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        List<User> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.getFirst();
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u " + "LEFT JOIN roles r ON u.role_id = r.id WHERE u.email = ?";
        List<User> results = executeQuery(sql, email);
        return results.isEmpty() ? null : results.getFirst();
    }

    public int create(User entity) throws SQLException {
        String sql = "INSERT INTO users(email, password_hash, role_id, is_blocked, must_change_password) VALUES (?, ?, ?, ?, ?)";
        Integer roleId = entity.getRole() != null && entity.getRole().getId() > 0 ? entity.getRole().getId() : null;
        return executeUpdate(sql, entity.getEmail(), entity.getPasswordHash(), roleId, entity.isBlocked(), entity.isMustChangePassword());
    }

    public int update(User entity) throws SQLException {
        String sql = "UPDATE users SET email = ?, password_hash = ?, role_id = ?, is_blocked = ?, must_change_password = ? WHERE id = ?";
        Integer roleId = entity.getRole() != null && entity.getRole().getId() > 0 ? entity.getRole().getId() : null;
        return executeUpdate(sql, entity.getEmail(), entity.getPasswordHash(), roleId, entity.isBlocked(), entity.isMustChangePassword(), entity.getId());
    }
    public int delete(int id) throws SQLException{
        String sql = "DELETE FROM users WHERE id = ?";
        return executeUpdate(sql, id);
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        String roleName = rs.getString("role_name");
        User user;

        if ("teacher".equalsIgnoreCase(roleName)) {
            user = new TeacherUser();
        } else if ("student".equalsIgnoreCase(roleName)) {
            user = new StudentUser();
        } else {
            throw new IllegalStateException("Unknown role_name in DB: " + roleName);
        }

        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setMustChangePassword(rs.getBoolean("must_change_password"));

        int roleId = rs.getInt("role_id");
        if (roleId > 0) {
            Role role = new Role(roleId, roleName);
            user.setRole(role);
        }

        return user;
    }
}
