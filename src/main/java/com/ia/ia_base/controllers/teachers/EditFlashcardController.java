package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.FlashcardDAO;
import com.ia.ia_base.database.dao.FlashcardTagDAO;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Flashcard;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class EditFlashcardController extends BaseController {

    private final TagDAO tagDAO = new TagDAO();
    private final FlashcardTagDAO flashcardTagDAO = new FlashcardTagDAO();
    private final ObservableList<Tag> allTags = FXCollections.observableArrayList();
    private final Map<Integer, BooleanProperty> checkedByTagId = new HashMap<>();
    @FXML
    public Button commitChangesBTN;
    @FXML
    public TextField questionField;
    @FXML
    public TextField answerField;
    @FXML
    public Button cancelBTN;
    @FXML
    public ListView<Tag> tagsListView;
    private Flashcard flashcard;
    private Runnable onSaved;

    @Override
    public void initialize(URL location, java.util.ResourceBundle resources) {
        setupTagsList();
    }

    private void setupTagsList() {
        try {
            allTags.setAll(tagDAO.findAll());
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load tags: " + e.getMessage());
        }

        tagsListView.setItems(allTags);

        // Render each tag as a checkbox row
        tagsListView.setCellFactory(CheckBoxListCell.forListView(tag -> checkedProperty(tag)));
    }

    private BooleanProperty checkedProperty(Tag tag) {
        // Create a stable BooleanProperty per tag id
        return checkedByTagId.computeIfAbsent(tag.getId(), k -> new SimpleBooleanProperty(false));
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;

        questionField.setText(flashcard.getQuestion());
        answerField.setText(flashcard.getAnswer());

        // Once a flashcard is set, mark which tags are currently linked
        loadCheckedTagsForFlashcard();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void loadCheckedTagsForFlashcard() {
        if (flashcard == null) return;

        // reset all to false first
        for (Tag t : allTags) {
            checkedProperty(t).set(false);
        }

        try {
            List<Tag> linkedTags = flashcardTagDAO.findTagsForFlashcard(flashcard.getId());
            Set<Integer> linkedIds = linkedTags.stream().map(Tag::getId).collect(Collectors.toSet());

            for (Tag t : allTags) {
                checkedProperty(t).set(linkedIds.contains(t.getId()));
            }
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load flashcard tags: " + e.getMessage());
        }
    }

    @FXML
    private void onCommitBTN() {
        if (flashcard == null) {
            AlertManager.showError("Error", "No flashcard selected.");
            return;
        }

        try {
            // 1) Update question/answer
            if (questionField.getText().isEmpty() || answerField.getText().isEmpty())
            {
                AlertManager.showError("One or more fields empty", "Please fill in all the fields.");
            }
            else{
                flashcard.setQuestion(questionField.getText());
                flashcard.setAnswer(answerField.getText());

                FlashcardDAO flashcardDAO = new FlashcardDAO();
                flashcardDAO.update(flashcard);

                // 2) Collect checked tags and update junction table
                List<Integer> selectedTagIds = allTags.stream()
                        .filter(t -> checkedProperty(t).get())
                        .map(Tag::getId)
                        .toList();

                flashcardTagDAO.setTagsForFlashcard(flashcard.getId(), selectedTagIds);

                // 3) Update in-memory tag names list (so table column can show it after reload)
                ArrayList<String> selectedTagNames = new ArrayList<>(
                        allTags.stream()
                                .filter(t -> checkedProperty(t).get())
                                .map(Tag::getTagName)
                                .toList()
                );
                flashcard.setTags(selectedTagNames);

                if (onSaved != null) onSaved.run();

                ((Stage) commitChangesBTN.getScene().getWindow()).close();

            }
        } catch (SQLException e) {
            AlertManager.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    private void onCancelBTN() {
        ((Stage) cancelBTN.getScene().getWindow()).close();
    }
}
