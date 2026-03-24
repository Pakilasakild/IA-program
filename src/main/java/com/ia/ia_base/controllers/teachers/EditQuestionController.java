package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuestionDAO;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class EditQuestionController extends BaseController {

    private final ObservableList<Question> questions = FXCollections.observableArrayList();
    private final ToggleGroup correctAnswerGroup = new ToggleGroup();

    @FXML
    public Button commitBTN;
    @FXML
    public Button cancelBTN;
    @FXML
    public ComboBox<Question> chooseQuestionCombo;
    @FXML
    public TextField questionTextField;
    @FXML
    public RadioButton ansOneRB;
    @FXML
    public RadioButton ansTwoRB;
    @FXML
    public RadioButton ansThrRB;
    @FXML
    public RadioButton ansFouRB;

    private QuestionDAO questionDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionDAO = new QuestionDAO();

        ansOneRB.setToggleGroup(correctAnswerGroup);
        ansTwoRB.setToggleGroup(correctAnswerGroup);
        ansThrRB.setToggleGroup(correctAnswerGroup);
        ansFouRB.setToggleGroup(correctAnswerGroup);

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

        chooseQuestionCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                populateFields(selected);
            } else {
                clearFields();
            }
        });

        setupActions();
    }

    private void setupActions() {
        cancelBTN.setOnAction(e -> closeWindow());

        commitBTN.setOnAction(e -> {
            Question selected = chooseQuestionCombo.getValue();

            if (selected == null) {
                AlertManager.showError("No question selected", "Choose a question to edit.");
                return;
            }

            String newQuestionText = questionTextField.getText() == null ? "" : questionTextField.getText().trim();

            if (newQuestionText.isEmpty()) {
                AlertManager.showError("Invalid input", "Question cannot be empty.");
                return;
            }

            int selectedCorrectAnswer = getSelectedCorrectAnswer();
            if (selectedCorrectAnswer == 0) {
                AlertManager.showError("Invalid input", "Choose the correct answer.");
                return;
            }

            try {
                selected.setQuestion(newQuestionText);
                selected.setCorrectAnswer(selectedCorrectAnswer);

                questionDAO.update(selected);
                AlertManager.showInfo("Success", "Question updated.");
                closeWindow();

            } catch (SQLException ex) {
                AlertManager.showError("Database Error", "Could not update question.\n\n" + ex.getMessage());
            }
        });
    }

    private void populateFields(Question question) {
        questionTextField.setText(question.getQuestion());

        ansOneRB.setText(question.getFirstAnswer());
        ansTwoRB.setText(question.getSecondAnswer());
        ansThrRB.setText(question.getThirdAnswer());
        ansFouRB.setText(question.getFourthAnswer());

        switch (question.getCorrectAnswer()) {
            case 1 -> correctAnswerGroup.selectToggle(ansOneRB);
            case 2 -> correctAnswerGroup.selectToggle(ansTwoRB);
            case 3 -> correctAnswerGroup.selectToggle(ansThrRB);
            case 4 -> correctAnswerGroup.selectToggle(ansFouRB);
            default -> correctAnswerGroup.selectToggle(null);
        }
    }

    private int getSelectedCorrectAnswer() {
        if (correctAnswerGroup.getSelectedToggle() == ansOneRB) return 1;
        if (correctAnswerGroup.getSelectedToggle() == ansTwoRB) return 2;
        if (correctAnswerGroup.getSelectedToggle() == ansThrRB) return 3;
        if (correctAnswerGroup.getSelectedToggle() == ansFouRB) return 4;
        return 0;
    }

    private void clearFields() {
        questionTextField.clear();
        ansOneRB.setText("");
        ansTwoRB.setText("");
        ansThrRB.setText("");
        ansFouRB.setText("");
        correctAnswerGroup.selectToggle(null);
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