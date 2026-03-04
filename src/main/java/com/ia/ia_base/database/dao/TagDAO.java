package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TagDAO extends BaseDAO<Tag> {

    public List<Tag> findAll() throws SQLException {
        return executeQuery("SELECT id, tag_name FROM tags ORDER BY tag_name");
    }

    public Tag findById(int id) throws SQLException {
        List<Tag> res = executeQuery("SELECT id, tag_name FROM tags WHERE id = ?", id);
        return res.isEmpty() ? null : res.getFirst();
    }

    public Tag findByTag(String name) throws SQLException {
        List<Tag> res = executeQuery("SELECT id, tag_name FROM tags WHERE tag_name = ?", name);
        return res.isEmpty() ? null : res.getFirst();
    }

    public int create(Tag entity) throws SQLException {
        return executeUpdate("INSERT INTO tags (tag_name) VALUES (?)", entity.getTagName());
    }

    public int update(Tag entity) throws SQLException {
        return executeUpdate("UPDATE tags SET tag_name = ? WHERE id = ?", entity.getTagName(), entity.getId());
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM tags WHERE id = ?", id);
    }

    @Override
    protected Tag mapResultSetToEntity(ResultSet rs) throws SQLException {
        Tag t = new Tag(rs.getString("tag_name"));
        t.setId(rs.getInt("id"));
        return t;
    }
}
