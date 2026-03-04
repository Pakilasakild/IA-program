package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Question;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuizQuestionDAO extends BaseDAO<Question> {

    /** Replace all questions in a quiz (also preserves order via position). */
    public void setQuestionsForQuiz(int quizId, List<Integer> questionIds) throws SQLException {
        executeUpdate("DELETE FROM quiz_questions WHERE quiz_id = ?", quizId);

        int pos = 1;
        for (int qid : questionIds) {
            executeUpdate(
                    "INSERT INTO quiz_questions (quiz_id, question_id, position) VALUES (?, ?, ?)",
                    quizId, qid, pos++
            );
        }
    }

    /** Get all Question objects for a quiz in position order. */
    public List<Question> findQuestionsForQuiz(int quizId) throws SQLException {
        String sql = """
            SELECT q.id, q.question, q.firstAnswer, q.secondAnswer, q.thirdAnswer, q.fourthAnswer, q.correctAnswer
            FROM questions q
            JOIN quiz_questions qq ON qq.question_id = q.id
            WHERE qq.quiz_id = ?
            ORDER BY qq.position
        """;
        return executeQuery(sql, quizId);
    }

    @Override
    protected Question mapResultSetToEntity(ResultSet rs) throws SQLException {
        Question q = new Question(
                rs.getString("question"),
                rs.getString("firstAnswer"),
                rs.getString("secondAnswer"),
                rs.getString("thirdAnswer"),
                rs.getString("fourthAnswer"),
                rs.getInt("correctAnswer")
        );
        q.setId(rs.getInt("id"));
        return q;
    }
}
