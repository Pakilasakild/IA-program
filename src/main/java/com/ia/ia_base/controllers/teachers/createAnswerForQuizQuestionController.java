package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuestionDAO;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.util.AlertManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class createAnswerForQuizQuestionController extends BaseController {

    @FXML public Text questionText;

    @FXML public TextField quizAnsOne;
    @FXML public TextField quizAnsTwo;
    @FXML public TextField quizAnsThree;
    @FXML public TextField quizAnsFour;

    @FXML public Button addQuestionAnswersBTN;
    @FXML public Button cancelBTN;

    @FXML private RadioButton firstCorrectBox, secondCorrectBox, thirdCorrectBox, fourthCorrectBox;
    @FXML private ToggleGroup correctGroup;

    private Question question;
    private QuestionDAO questionDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionDAO = new QuestionDAO();

        setupCorrectAnswerConstraints();
        setupActions();
    }

    private void setupActions() {
        cancelBTN.setOnAction(e -> onCancelBTN());

        addQuestionAnswersBTN.setOnAction(e -> {
            try {
                onCommit();
            } catch (SQLException ex) {
                AlertManager.showError("Database Error", ex.getMessage());
            }
        });
    }

    public void setQuestion(Question question) {
        this.question = question;
        questionText.setText(question.getQuestion());
    }

    private void onCancelBTN() {
        ((Stage) cancelBTN.getScene().getWindow()).close();
    }

    /**
     * - If a text field is blank, its radio is disabled (can't be correct)
     * - If a chosen correct answer becomes blank later, selection is cleared
     */
    private void setupCorrectAnswerConstraints() {
        bindDisableToBlank(firstCorrectBox, quizAnsOne);
        bindDisableToBlank(secondCorrectBox, quizAnsTwo);
        bindDisableToBlank(thirdCorrectBox, quizAnsThree);
        bindDisableToBlank(fourthCorrectBox, quizAnsFour);

        clearSelectionIfBecomesBlank(firstCorrectBox, quizAnsOne);
        clearSelectionIfBecomesBlank(secondCorrectBox, quizAnsTwo);
        clearSelectionIfBecomesBlank(thirdCorrectBox, quizAnsThree);
        clearSelectionIfBecomesBlank(fourthCorrectBox, quizAnsFour);
    }

    private void bindDisableToBlank(RadioButton rb, TextField tf) {
        rb.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> tf.getText() == null || tf.getText().isBlank(),
                        tf.textProperty()
                )
        );
    }

    private void clearSelectionIfBecomesBlank(RadioButton rb, TextField tf) {
        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean nowBlank = (newVal == null || newVal.isBlank());
            if (nowBlank && correctGroup.getSelectedToggle() == rb) {
                correctGroup.selectToggle(null);
            }
        });
    }

    private void onCommit() throws SQLException {
        if (question == null) {
            AlertManager.showError("Error", "No question provided.");
            return;
        }

        // Require at least 2 filled answers (change to 4 if you want all required)
        if (countFilled() < 2) {
            AlertManager.showError("Too few answers", "Enter at least 2 possible answers.");
            return;
        }

        int correctIndex = getCorrectAnswerIndex();
        if (correctIndex == 0) {
            AlertManager.showError("No correct answer", "Select which answer is correct.");
            return;
        }

        // Correct answer field must not be blank
        String correctText = getAnswerText(correctIndex);
        if (correctText == null || correctText.isBlank()) {
            AlertManager.showError("Invalid correct answer", "The correct answer cannot be empty.");
            return;
        }

        // ✅ FAILSAFE: compact answers (remove gaps) + remap correctIndex
        NormalizedAnswers norm = normalizeAnswersAndRemapCorrect(correctIndex);
        if (norm.correctIndex == 0) {
            AlertManager.showError("Invalid correct answer",
                    "The selected correct answer became invalid after normalization.");
            return;
        }

        Question question1 = new Question(
                question.getQuestion(),
                norm.answers[0],
                norm.answers[1],
                norm.answers[2],
                norm.answers[3],
                norm.correctIndex
        );

        questionDAO.create(question1);

        AlertManager.showInfo("Success!", "Question successfully created");
        ((Stage) addQuestionAnswersBTN.getScene().getWindow()).close();
    }

    private int countFilled() {
        int filled = 0;
        if (!quizAnsOne.getText().isBlank()) filled++;
        if (!quizAnsTwo.getText().isBlank()) filled++;
        if (!quizAnsThree.getText().isBlank()) filled++;
        if (!quizAnsFour.getText().isBlank()) filled++;
        return filled;
    }

    private String getAnswerText(int index) {
        return switch (index) {
            case 1 -> quizAnsOne.getText();
            case 2 -> quizAnsTwo.getText();
            case 3 -> quizAnsThree.getText();
            case 4 -> quizAnsFour.getText();
            default -> "";
        };
    }

    public int getCorrectAnswerIndex() {
        Toggle selected = correctGroup.getSelectedToggle();
        if (selected == firstCorrectBox) return 1;
        if (selected == secondCorrectBox) return 2;
        if (selected == thirdCorrectBox) return 3;
        if (selected == fourthCorrectBox) return 4;
        return 0;
    }

    /**
     * Compacts answers to remove gaps and remaps correctIndex.
     * Example: filled 1,2,4 -> answer4 moves to slot3 and correctIndex 4 -> 3.
     */
    private NormalizedAnswers normalizeAnswersAndRemapCorrect(int originalCorrectIndex) {
        String[] raw = new String[] {
                quizAnsOne.getText(),
                quizAnsTwo.getText(),
                quizAnsThree.getText(),
                quizAnsFour.getText()
        };

        // oldIndex (1..4) -> newIndex (1..n), 0 if removed/blank
        int[] map = new int[5];

        List<String> compact = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String v = raw[i - 1];
            if (v != null && !v.isBlank()) {
                compact.add(v);
                map[i] = compact.size(); // 1-based
            } else {
                map[i] = 0;
            }
        }

        String[] out = new String[4];
        for (int i = 0; i < 4; i++) {
            out[i] = i < compact.size() ? compact.get(i) : "";
        }

        int newCorrect = map[originalCorrectIndex];
        return new NormalizedAnswers(out, newCorrect);
    }

    private static class NormalizedAnswers {
        final String[] answers;   // always length 4, blanks only at the end
        final int correctIndex;   // 1..4 (or 0 if invalid)

        NormalizedAnswers(String[] answers, int correctIndex) {
            this.answers = answers;
            this.correctIndex = correctIndex;
        }
    }
}
