package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Question;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestionDAO extends BaseDAO<Question> {

    public List<Question> findAll() throws SQLException {
        return executeQuery("SELECT * FROM questions");
    }

    public Question findById(int id) throws SQLException {
        List<Question> res = executeQuery("SELECT * FROM questions WHERE id = ?", id);
        return res.isEmpty() ? null : res.getFirst();
    }

    public int create(Question q) throws SQLException {
        String sql = """
                    INSERT INTO questions (question, firstAnswer, secondAnswer, thirdAnswer, fourthAnswer, correctAnswer)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
        return executeUpdate(sql, q.getQuestion(), q.getFirstAnswer(), q.getSecondAnswer(), q.getThirdAnswer(), q.getFourthAnswer(), q.getCorrectAnswer());
    }

    public int update(Question q) throws SQLException {
        String sql = """
                    UPDATE questions
                    SET question = ?, firstAnswer = ?, secondAnswer = ?, thirdAnswer = ?, fourthAnswer = ?, correctAnswer = ?
                    WHERE id = ?
                """;
        return executeUpdate(sql, q.getQuestion(), q.getFirstAnswer(), q.getSecondAnswer(), q.getThirdAnswer(), q.getFourthAnswer(), q.getCorrectAnswer(), q.getId());
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM questions WHERE id = ?", id);
    }

    @Override
    protected Question mapResultSetToEntity(ResultSet rs) throws SQLException {
        Question q = new Question(rs.getString("question"), rs.getString("firstAnswer"), rs.getString("secondAnswer"), rs.getString("thirdAnswer"), rs.getString("fourthAnswer"), rs.getInt("correctAnswer"));
        q.setId(rs.getInt("id"));
        return q;
    }
}
