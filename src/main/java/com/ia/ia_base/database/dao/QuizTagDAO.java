package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuizTagDAO extends BaseDAO<Tag> {

    /** Replace all tags in a quiz. */
    public void setTagsForQuiz(int quizId, List<Integer> tagIds) throws SQLException {
        executeUpdate("DELETE FROM quiz_tags WHERE quiz_id = ?", quizId);

        for (int tagId : tagIds) {
            executeUpdate("INSERT INTO quiz_tags (quiz_id, tag_id) VALUES (?, ?)", quizId, tagId);
        }
    }

    /** Get Tag objects for a quiz. */
    public List<Tag> findTagsForQuiz(int quizId) throws SQLException {
        // alias tag_name -> tagName so Tag mapping can use one name if you prefer
        String sql = """
            SELECT t.id, t.tag_name AS tag_name
            FROM tags t
            JOIN quiz_tags qt ON qt.tag_id = t.id
            WHERE qt.quiz_id = ?
            ORDER BY t.tag_name
        """;
        return executeQuery(sql, quizId);
    }

    @Override
    protected Tag mapResultSetToEntity(ResultSet rs) throws SQLException {
        Tag t = new Tag(rs.getString("tag_name"));
        t.setId(rs.getInt("id"));
        return t;
    }
}
