package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.*;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.QuizReloadBus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class EditQuizController extends BaseController {

    @FXML public Button commitChangesBTN;
    @FXML public Button cancelBTN;

    @FXML public TextField quizName;
    @FXML public ListView<Question> questionListView;
    @FXML public ListView<Tag> tagsListView;

    private Quiz quiz;
    private Runnable onSaved;

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final QuizQuestionDAO quizQuestionDAO = new QuizQuestionDAO();
    private final QuizTagDAO quizTagDAO = new QuizTagDAO();

    private final ObservableList<Question> allQuestions = FXCollections.observableArrayList();
    private final ObservableList<Tag> allTags = FXCollections.observableArrayList();

    private final Map<Integer, BooleanProperty> tagChecked = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // questions: multi-select
        questionListView.setItems(allQuestions);
        questionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        questionListView.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });

        // tags: checkbox list
        tagsListView.setItems(allTags);
        tagsListView.setCellFactory(CheckBoxListCell.forListView(tag ->
                tagChecked.computeIfAbsent(tag.getId(), k -> new SimpleBooleanProperty(false))
        ));

        cancelBTN.setOnAction(e -> onCancelBTN());
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        quizName.setText(quiz.getName());
        loadAllData();
        preloadSelections();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void loadAllData() {
        try {
            allQuestions.setAll(questionDAO.findAll());
            allTags.setAll(tagDAO.findAll());
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load questions/tags: " + e.getMessage());
        }
    }

    private void preloadSelections() {
        if (quiz == null) return;

        try {
            // preselect questions
            List<Question> linkedQs = quizQuestionDAO.findQuestionsForQuiz(quiz.getId());
            Set<Integer> linkedQIds = linkedQs.stream().map(Question::getId).collect(Collectors.toSet());

            MultipleSelectionModel<Question> sm = questionListView.getSelectionModel();
            sm.clearSelection();
            for (int i = 0; i < allQuestions.size(); i++) {
                if (linkedQIds.contains(allQuestions.get(i).getId())) {
                    sm.select(i);
                }
            }

            // precheck tags
            List<Tag> linkedTags = quizTagDAO.findTagsForQuiz(quiz.getId());
            Set<Integer> linkedTagIds = linkedTags.stream().map(Tag::getId).collect(Collectors.toSet());

            for (Tag t : allTags) {
                tagChecked.computeIfAbsent(t.getId(), k -> new SimpleBooleanProperty(false))
                        .set(linkedTagIds.contains(t.getId()));
            }

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load quiz links: " + e.getMessage());
        }
    }

    @FXML
    public void onCommitBTN() {
        if (quiz == null) {
            AlertManager.showError("Error", "No quiz selected.");
            return;
        }

        String name = quizName.getText() == null ? "" : quizName.getText().trim();

        // failsafes
        if (name.isEmpty()) {
            AlertManager.showError("Invalid name", "Quiz name cannot be empty.");
            return;
        }

        List<Question> selectedQuestions = questionListView.getSelectionModel().getSelectedItems();
        if (selectedQuestions == null || selectedQuestions.isEmpty()) {
            AlertManager.showError("No questions selected", "Select at least 1 question.");
            return;
        }

        try {
            // unique name check (allow same quiz keeping same name)
            Quiz existing = quizDAO.findByName(name);
            if (existing != null && existing.getId() != quiz.getId()) {
                AlertManager.showError("Duplicate name", "A different quiz already uses this name.");
                return;
            }

            // update quiz
            quiz.setName(name);
            quizDAO.update(quiz);

            // link questions in stable order (screen order)
            Set<Integer> selectedIds = selectedQuestions.stream().map(Question::getId).collect(Collectors.toSet());
            List<Integer> orderedQuestionIds = allQuestions.stream()
                    .filter(q -> selectedIds.contains(q.getId()))
                    .map(Question::getId)
                    .toList();

            quizQuestionDAO.setQuestionsForQuiz(quiz.getId(), orderedQuestionIds);

            // link tags (0 tags allowed)
            List<Integer> tagIds = allTags.stream()
                    .filter(t -> tagChecked.getOrDefault(t.getId(), new SimpleBooleanProperty(false)).get())
                    .map(Tag::getId)
                    .toList();

            quizTagDAO.setTagsForQuiz(quiz.getId(), tagIds);

            // update in-memory tags list for table display
            ArrayList<String> tagNames = new ArrayList<>(
                    allTags.stream()
                            .filter(t -> tagChecked.getOrDefault(t.getId(), new SimpleBooleanProperty(false)).get())
                            .map(Tag::getTagName)
                            .toList()
            );
            quiz.setTags(tagNames);

            AlertManager.showInfo("Success", "Quiz updated.");

            // refresh quiz list + welcome counts
            QuizReloadBus.requestReload();
            if (onSaved != null) onSaved.run();

            ((Stage) commitChangesBTN.getScene().getWindow()).close();

        } catch (SQLException e) {
            AlertManager.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    public void onCancelBTN() {
        ((Stage) commitChangesBTN.getScene().getWindow()).close();
    }
}
