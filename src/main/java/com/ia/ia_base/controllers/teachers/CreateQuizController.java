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

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CreateQuizController extends BaseController {

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final QuizQuestionDAO quizQuestionDAO = new QuizQuestionDAO();
    private final QuizTagDAO quizTagDAO = new QuizTagDAO();
    private final ObservableList<Question> questionsObs = FXCollections.observableArrayList();
    private final ObservableList<Tag> tagsObs = FXCollections.observableArrayList();
    private final Map<Integer, BooleanProperty> tagChecked = new HashMap<>();
    @FXML
    public Button createQuizBTN;
    @FXML
    public Button cancelBTN;
    @FXML
    public TextField quizNameField;
    @FXML
    public ListView<Question> quizQuestionsListView;
    @FXML
    public ListView<Tag> quizTagsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupQuestionList();
        setupTagsList();
        setupMenuActions();
        loadData();
    }

    private void setupMenuActions() {
        cancelBTN.setOnAction(e -> closeWindow());
        createQuizBTN.setOnAction(e -> onCreateQuiz());
    }

    private void setupQuestionList() {
        quizQuestionsListView.setItems(questionsObs);
        quizQuestionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        quizQuestionsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });
    }

    private void setupTagsList() {
        quizTagsListView.setItems(tagsObs);

        // checkbox cells for tags
        quizTagsListView.setCellFactory(CheckBoxListCell.forListView(tag ->
                tagChecked.computeIfAbsent(tag.getId(), k -> new SimpleBooleanProperty(false))
        ));
    }

    private void loadData() {
        try {
            questionsObs.setAll(questionDAO.findAll());
            tagsObs.setAll(tagDAO.findAll());

            if (questionsObs.isEmpty()) {
                createQuizBTN.setDisable(true);
                AlertManager.showError("No questions", "Create at least one question before creating a quiz.");
            }
        } catch (SQLException e) {
            createQuizBTN.setDisable(true);
            AlertManager.showError("Database Error", "Failed to load questions/tags: " + e.getMessage());
        }
    }

    private void onCreateQuiz() {
        String name = (quizNameField.getText() == null) ? "" : quizNameField.getText().trim();
        if (name.isEmpty()) {
            AlertManager.showError("Invalid quiz name", "Please enter a quiz name.");
            return;
        }

        List<Question> selectedQuestions = quizQuestionsListView.getSelectionModel().getSelectedItems();
        if (selectedQuestions == null || selectedQuestions.isEmpty()) {
            AlertManager.showError("No questions selected", "Select at least 1 question for the quiz.");
            return;
        }

        try {
            Quiz existing = quizDAO.findByName(name);
            if (existing != null) {
                AlertManager.showError("Duplicate quiz name", "A quiz with this name already exists.");
                return;
            }

            Quiz quiz = new Quiz(name);
            quizDAO.create(quiz);

            Quiz inserted = quizDAO.findByName(name);
            if (inserted == null) {
                AlertManager.showError("Database Error", "Quiz was created but could not be reloaded (id missing).");
                return;
            }
            int quizId = inserted.getId();

            Set<Integer> selectedQuestionIds = selectedQuestions.stream().map(Question::getId).collect(Collectors.toSet());
            List<Integer> orderedQuestionIds = questionsObs.stream()
                    .filter(q -> selectedQuestionIds.contains(q.getId()))
                    .map(Question::getId)
                    .toList();

            quizQuestionDAO.setQuestionsForQuiz(quizId, orderedQuestionIds);

            List<Integer> tagIds = tagsObs.stream()
                    .filter(t -> tagChecked.getOrDefault(t.getId(), new SimpleBooleanProperty(false)).get())
                    .map(Tag::getId)
                    .toList();

            quizTagDAO.setTagsForQuiz(quizId, tagIds);

            AlertManager.showInfo("Success", "Quiz created successfully.");

            QuizReloadBus.requestReload();

            closeWindow();

        } catch (SQLException e) {
            AlertManager.showError("Database Error", e.getMessage());
        }
    }
}