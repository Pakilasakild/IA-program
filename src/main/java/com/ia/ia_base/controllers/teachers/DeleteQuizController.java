package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuizDAO;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.InformationReloadBus;
import com.ia.ia_base.util.QuizReloadBus;
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

public class DeleteQuizController extends BaseController {

    @FXML public Button deleteQuizBTN;
    @FXML public Button cancelBTN;
    @FXML public ComboBox<Quiz> chooseQuizCombo;

    private QuizDAO quizDAO;
    private final ObservableList<Quiz> quizzes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quizDAO = new QuizDAO();

        loadQuizzes();
        chooseQuizCombo.setItems(quizzes);

        chooseQuizCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        chooseQuizCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        setupActions();
    }

    private void setupActions() {
        cancelBTN.setOnAction(e -> closeWindow());

        deleteQuizBTN.setOnAction(e -> {
            Quiz selected = chooseQuizCombo.getValue();

            if (selected == null) {
                AlertManager.showError("No quiz selected", "Choose a quiz to delete.");
                return;
            }

            try {
                quizDAO.delete(selected.getId());

                quizzes.remove(selected);
                chooseQuizCombo.getSelectionModel().clearSelection();

                AlertManager.showInfo("Success", "Quiz deleted.");
                QuizReloadBus.requestReload();
                InformationReloadBus.requestReload();

                closeWindow();

            } catch (SQLException ex) {
                AlertManager.showError(
                        "Database Error",
                        "Could not delete quiz. It may be linked to questions/tags.\n\n" + ex.getMessage()
                );
            }
        });
    }

    private void loadQuizzes() {
        try {
            List<Quiz> all = quizDAO.findAll();
            quizzes.setAll(all);
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }
}
