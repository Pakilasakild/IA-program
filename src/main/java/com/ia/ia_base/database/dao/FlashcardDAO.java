package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Flashcard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlashcardDAO extends BaseDAO<Flashcard> {

    private final FlashcardTagDAO flashcardTagDAO = new FlashcardTagDAO();

    public List<Flashcard> findAll() throws SQLException {
        return executeQuery("SELECT * FROM flashcards");
    }

    public List<Flashcard> findAllWithTags() throws SQLException {
        List<Flashcard> cards = findAll();
        for (Flashcard fc : cards) {
            ArrayList<String> tagNames = new ArrayList<>(flashcardTagDAO.findTagNamesForFlashcard(fc.getId()));
            fc.setTags(tagNames);
        }
        return cards;
    }

    public Flashcard findById(int id) throws SQLException {
        List<Flashcard> res = executeQuery("SELECT * FROM flashcards WHERE id = ?", id);
        Flashcard fc = res.isEmpty() ? null : res.getFirst();
        if (fc != null) fc.setTags(new ArrayList<>(flashcardTagDAO.findTagNamesForFlashcard(fc.getId())));
        return fc;
    }

    public Flashcard findByQuestion(String question) throws SQLException {
        List<Flashcard> res = executeQuery("SELECT * FROM flashcards WHERE question = ?", question);
        Flashcard fc = res.isEmpty() ? null : res.getFirst();
        if (fc != null) fc.setTags(new ArrayList<>(flashcardTagDAO.findTagNamesForFlashcard(fc.getId())));
        return fc;
    }

    public int create(Flashcard entity) throws SQLException {
        return executeUpdate("INSERT INTO flashcards (question, answer, active) VALUES (?, ?, ?)",
                entity.getQuestion(), entity.getAnswer(), entity.isActive());
    }

    public int update(Flashcard entity) throws SQLException {
        return executeUpdate("UPDATE flashcards SET question = ?, answer = ?, active = ? WHERE id = ?",
                entity.getQuestion(), entity.getAnswer(), entity.isActive(), entity.getId());
    }

    public int updateActive(int id, boolean active) throws SQLException {
        return executeUpdate("UPDATE flashcards SET active = ? WHERE id = ?", active, id);
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM flashcards WHERE id = ?", id);
    }

    @Override
    protected Flashcard mapResultSetToEntity(ResultSet rs) throws SQLException {
        Flashcard fc = new Flashcard(rs.getString("question"), rs.getString("answer"));
        fc.setId(rs.getInt("id"));
        fc.setActive(rs.getBoolean("active"));
        return fc;
    }
}
