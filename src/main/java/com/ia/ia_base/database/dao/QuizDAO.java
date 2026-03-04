package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Quiz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuizDAO extends BaseDAO<Quiz> {

    public List<Quiz> findAll() throws SQLException {
        return executeQuery("SELECT * FROM quizzes");
    }

    public Quiz findById(int id) throws SQLException {
        List<Quiz> res = executeQuery("SELECT * FROM quizzes WHERE id = ?", id);
        return res.isEmpty() ? null : res.getFirst();
    }

    public Quiz findByName(String quizName) throws SQLException {
        List<Quiz> res = executeQuery("SELECT * FROM quizzes WHERE quizName = ?", quizName);
        return res.isEmpty() ? null : res.getFirst();
    }

    public List<Quiz> findAllByTagId(int tagId) throws SQLException {
        String sql =
                "SELECT q.id, q.quizName " +
                        "FROM quizzes q " +
                        "JOIN quiz_tags qt ON qt.quiz_id = q.id " +
                        "WHERE qt.tag_id = ? " +
                        "ORDER BY q.quizName";
        return executeQuery(sql, tagId);
    }

    public int create(Quiz quiz) throws SQLException {
        return executeUpdate("INSERT INTO quizzes (quizName) VALUES (?)", quiz.getName());
    }

    public int update(Quiz quiz) throws SQLException {
        return executeUpdate("UPDATE quizzes SET quizName = ? WHERE id = ?", quiz.getName(), quiz.getId());
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM quizzes WHERE id = ?", id);
    }

    @Override
    protected Quiz mapResultSetToEntity(ResultSet rs) throws SQLException {
        Quiz q = new Quiz(rs.getString("quizName"));
        q.setId(rs.getInt("id"));
        return q;
    }
}
