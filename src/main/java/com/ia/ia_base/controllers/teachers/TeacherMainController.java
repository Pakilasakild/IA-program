package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.database.dao.QuestionDAO;
import com.ia.ia_base.database.dao.QuizDAO;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import com.ia.ia_base.controllers.BaseController;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class TeacherMainController extends BaseController {
    @FXML
    public BorderPane mainPage;
    @FXML
    public MenuItem changePasswordMenu;
    @FXML
    public MenuItem logoutMenu;
    @FXML
    public MenuItem quitMenu;
    @FXML
    public MenuItem viewFlashcardsMenu;
    @FXML
    public MenuItem createFlashcardMenu;
    @FXML
    public MenuItem viewQuizzesMenu;
    @FXML
    public MenuItem addQuizMenu;
    @FXML
    public MenuItem addQuizQuestionMenu;
    @FXML
    public MenuItem deleteQuizMenu;
    @FXML
    public MenuItem importFlashcardMenu;
    @FXML
    public MenuItem addTagMenu;
    @FXML
    public MenuItem deleteTagMenu;
    public QuestionDAO questionDAO = new QuestionDAO();

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupMenuActions();
    }


    private void setupMenuActions() {
        quitMenu.setOnAction(e -> {
            quitMenu();
        });

        logoutMenu.setOnAction(e -> {
            logoutMenu();
        });
        changePasswordMenu.setOnAction(e -> {
            changePasswordMenu();
        });
        createFlashcardMenu.setOnAction(e -> {
            openNewWindow("/com/ia/ia_base/IA/Teachers/createFlashcard.fxml", "Create flashcard");
        });
        importFlashcardMenu.setOnAction(e -> {
            openNewWindow("/com/ia/ia_base/IA/Teachers/teacherUploadFlashcard.fxml", "Import flashcards");
        });
        viewFlashcardsMenu.setOnAction(e -> {
            try {
                URL fxml = getClass().getResource("/com/ia/ia_base/IA/Teachers/flashcardTableTeach.fxml");
                assert fxml != null;
                AnchorPane pane = FXMLLoader.load(fxml);
                mainPage.setCenter(pane);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        viewQuizzesMenu.setOnAction(e -> {
            try {
                URL fxml = getClass().getResource("/com/ia/ia_base/IA/Teachers/viewQuizzesTeacher.fxml");
                assert fxml != null;
                AnchorPane pane = FXMLLoader.load(fxml);
                mainPage.setCenter(pane);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        addQuizMenu.setOnAction(e -> {
            try {
                List<Question> questions = questionDAO.findAll();
                if (questions.isEmpty())
                {
                    AlertManager.showError("No questions", "Create at least one question before creating a quiz.");
                }
                else {
                    openNewWindow("/com/ia/ia_base/IA/Teachers/createQuiz.fxml", "Create new quiz");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        addQuizQuestionMenu.setOnAction(e -> {
            openNewWindow("/com/ia/ia_base/IA/Teachers/createQuizQuestion.fxml", "Create new quiz question");
        });
        deleteQuizMenu.setOnAction(e -> {
            openNewWindow("/com/ia/ia_base/IA/Teachers/deleteQuiz.fxml", "Delete quiz");
        });
        addTagMenu.setOnAction(e -> {
            openNewWindow("/com/ia/ia_base/IA/Teachers/createTag.fxml", "Create new tag");
        });
        deleteTagMenu.setOnAction(e -> {
            openNewWindow("/com/ia/ia_base/IA/Teachers/deleteTag.fxml", "Delete tag");
        });
    }
}
