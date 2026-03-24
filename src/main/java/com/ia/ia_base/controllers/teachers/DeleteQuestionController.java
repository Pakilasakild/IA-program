package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuestionDAO;
import com.ia.ia_base.database.dao.QuizDAO;
import com.ia.ia_base.database.dao.QuizQuestionDAO;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteQuestionController extends BaseController {

    private final ObservableList<Question> questions = FXCollections.observableArrayList();

    @FXML
    public Button deleteQuestionBTN;
    @FXML
    public Button cancelBTN;
    @FXML
    public ComboBox<Question> chooseQuestionCombo;

    private QuestionDAO questionDAO;
    private QuizDAO quizDAO;
    private QuizQuestionDAO quizQuestionDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionDAO = new QuestionDAO();
        quizDAO = new QuizDAO();
        quizQuestionDAO = new QuizQuestionDAO();

        loadQuestions();
        chooseQuestionCombo.setItems(questions);

        chooseQuestionCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });

        chooseQuestionCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getQuestion());
            }
        });

        setupActions();
    }

    private void setupActions() {
        cancelBTN.setOnAction(e -> closeWindow());

        deleteQuestionBTN.setOnAction(e -> {
            Question selected = chooseQuestionCombo.getValue();

            if (selected == null) {
                AlertManager.showError("No question selected", "Choose a question to delete.");
                return;
            }

            try {
                if (isQuestionUsedInAnyQuiz(selected.getId())) {
                    AlertManager.showError(
                            "Cannot delete question",
                            "This question is assigned to one or more quizzes and cannot be deleted."
                    );
                    return;
                }

                questionDAO.delete(selected.getId());

                questions.remove(selected);
                chooseQuestionCombo.getSelectionModel().clearSelection();

                AlertManager.showInfo("Success", "Question deleted.");
                closeWindow();

            } catch (SQLException ex) {
                AlertManager.showError("Database Error", "Could not delete question.\n\n" + ex.getMessage());
            }
        });
    }

    private boolean isQuestionUsedInAnyQuiz(int questionId) throws SQLException {
        List<Quiz> quizzes = quizDAO.findAll();

        for (Quiz quiz : quizzes) {
            List<Question> quizQuestions = quizQuestionDAO.findQuestionsForQuiz(quiz.getId());

            for (Question question : quizQuestions) {
                if (question.getId() == questionId) {
                    return true;
                }
            }
        }

        return false;
    }

    private void loadQuestions() {
        try {
            List<Question> all = questionDAO.findAll();
            questions.setAll(all);
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load questions: " + e.getMessage());
        }
    }
}