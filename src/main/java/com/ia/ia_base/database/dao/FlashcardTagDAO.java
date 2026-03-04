package com.ia.ia_base.database.dao;

import com.ia.ia_base.models.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FlashcardTagDAO extends BaseDAO<Tag> {

    /** Link a tag to a flashcard */
    public int link(int flashcardId, int tagId) throws SQLException {
        return executeUpdate(
                "INSERT INTO flashcard_tags (flashcard_id, tag_id) VALUES (?, ?)",
                flashcardId, tagId
        );
    }

    /** Unlink one tag from one flashcard */
    public int unlink(int flashcardId, int tagId) throws SQLException {
        return executeUpdate(
                "DELETE FROM flashcard_tags WHERE flashcard_id = ? AND tag_id = ?",
                flashcardId, tagId
        );
    }

    /** Remove all tags for a flashcard */
    public int unlinkAllForFlashcard(int flashcardId) throws SQLException {
        return executeUpdate("DELETE FROM flashcard_tags WHERE flashcard_id = ?", flashcardId);
    }

    /** Remove all flashcard links for a tag */
    public int unlinkAllForTag(int tagId) throws SQLException {
        return executeUpdate("DELETE FROM flashcard_tags WHERE tag_id = ?", tagId);
    }

    /** Get Tag objects for a flashcard */
    public List<Tag> findTagsForFlashcard(int flashcardId) throws SQLException {
        return executeQuery(
                "SELECT t.id, t.tag_name AS tagName " +   // ✅ alias to match Java
                        "FROM tags t " +
                        "JOIN flashcard_tags ft ON ft.tag_id = t.id " +
                        "WHERE ft.flashcard_id = ? " +
                        "ORDER BY t.tag_name",
                flashcardId
        );
    }

    /** Get tag NAMES for a flashcard (for Flashcard.tags ArrayList<String>) */
    public List<String> findTagNamesForFlashcard(int flashcardId) throws SQLException {
        List<Tag> tags = findTagsForFlashcard(flashcardId);
        return tags.stream().map(Tag::getTagName).toList();
    }

    /**
     * Replace all tags assigned to a flashcard with the new set.
     */
    public void setTagsForFlashcard(int flashcardId, List<Integer> tagIds) throws SQLException {
        unlinkAllForFlashcard(flashcardId);
        for (int tagId : tagIds) {
            link(flashcardId, tagId);
        }
    }

    @Override
    protected Tag mapResultSetToEntity(ResultSet rs) throws SQLException {
        // ✅ because we aliased tag_name AS tagName
        Tag t = new Tag(rs.getString("tagName"));
        t.setId(rs.getInt("id"));
        return t;
    }
}
