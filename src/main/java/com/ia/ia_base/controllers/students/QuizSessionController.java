package com.ia.ia_base.controllers.students;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuizQuestionDAO;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.util.AlertManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QuizSessionController extends BaseController {

    @FXML private AnchorPane quizSessionWindow;

    @FXML private Text questionText;
    @FXML private RadioButton answerRB1;
    @FXML private RadioButton answerRB2;
    @FXML private RadioButton answerRB3;
    @FXML private RadioButton answerRB4;

    @FXML private Button nextQuestionBTN;
    @FXML private Button lastQuestionBTN;

    private final ToggleGroup answersGroup = new ToggleGroup();
    private final QuizQuestionDAO quizQuestionDAO = new QuizQuestionDAO();

    private Quiz quiz;

    private final List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;

    // Stores chosen answer index per question: 0 = not answered, 1..4 = chosen option
    private int[] chosenAnswers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        answerRB1.setToggleGroup(answersGroup);
        answerRB2.setToggleGroup(answersGroup);
        answerRB3.setToggleGroup(answersGroup);
        answerRB4.setToggleGroup(answersGroup);

        nextQuestionBTN.setOnAction(e -> goNext());
        lastQuestionBTN.setOnAction(e -> goBack());
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;

        questions.clear();
        currentIndex = 0;

        try {
            questions.addAll(quizQuestionDAO.findQuestionsForQuiz(quiz.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
            AlertManager.showError("Error", "Failed to load quiz questions.");
            disableAll();
            return;
        }

        if (questions.isEmpty()) {
            AlertManager.showError("No questions", "This quiz has no questions.");
            disableAll();
            return;
        }

        chosenAnswers = new int[questions.size()];
        showQuestion(0);
    }

    private void disableAll() {
        questionText.setText("Unable to start quiz.");
        answerRB1.setDisable(true);
        answerRB2.setDisable(true);
        answerRB3.setDisable(true);
        answerRB4.setDisable(true);
        nextQuestionBTN.setDisable(true);
        lastQuestionBTN.setDisable(true);
    }

    private void showQuestion(int index) {
        currentIndex = index;

        Question q = questions.get(currentIndex);
        questionText.setText((currentIndex + 1) + "/" + questions.size() + " - " + safe(q.getQuestion()));

        // Set answer texts (hide empty answers so 2–4 answers work)
        setRadio(answerRB1, q.getFirstAnswer());
        setRadio(answerRB2, q.getSecondAnswer());
        setRadio(answerRB3, q.getThirdAnswer());
        setRadio(answerRB4, q.getFourthAnswer());

        // Restore previously selected answer (if any)
        restoreSelectionForCurrentQuestion();

        // Update navigation buttons
        lastQuestionBTN.setDisable(currentIndex == 0);
        nextQuestionBTN.setText(currentIndex == questions.size() - 1 ? "Finish" : "Next question");
    }

    private void goNext() {
        // Save current selection (if any)
        saveCurrentSelection();

        // Require an answer before going forward / finishing
        if (chosenAnswers[currentIndex] == 0) {
            AlertManager.showError("No answer selected", "Please select an answer.");
            return;
        }

        if (currentIndex == questions.size() - 1) {
            finishQuiz();
            return;
        }

        showQuestion(currentIndex + 1);
    }

    private void goBack() {
        // Save current selection (if any)
        saveCurrentSelection();

        if (currentIndex > 0) {
            showQuestion(currentIndex - 1);
        }
    }

    private void saveCurrentSelection() {
        Toggle selected = answersGroup.getSelectedToggle();
        if (selected == null) {
            // keep whatever was previously saved (could be 0)
            return;
        }

        int idx = toggleToIndex(selected);
        if (idx != -1) {
            chosenAnswers[currentIndex] = idx;
        }
    }

    private void restoreSelectionForCurrentQuestion() {
        answersGroup.selectToggle(null);

        int saved = chosenAnswers[currentIndex];
        if (saved == 0) return;

        RadioButton rb = indexToRadio(saved);
        if (rb == null) return;

        // If that answer is currently hidden (blank), don't restore it
        if (!rb.isVisible() || !rb.isManaged()) return;

        answersGroup.selectToggle(rb);
    }

    private void finishQuiz() {
        // Calculate score at the end (like looping through the list)
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            int chosen = chosenAnswers[i];
            int correct = questions.get(i).getCorrectAnswer();
            if (chosen != 0 && chosen == correct) {
                score++;
            }
        }

        openFeedbackWindow(score, questions.size());

        // Close session window
        Stage stage = (Stage) quizSessionWindow.getScene().getWindow();
        stage.close();
    }

    private void openFeedbackWindow(int score, int total) {
        try {
            URL fxmlUrl = getClass().getResource("/com/ia/ia_base/IA/Student/feedbackLayout.fxml");
            if (fxmlUrl == null) {
                AlertManager.showError(
                        "Unable to open feedback",
                        "feedbackLayout.fxml was not found at: /com/ia/ia_base/IA/Student/feedbackLayout.fxml\n" +
                                "Make sure it is inside src/main/resources and included in the build."
                );
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            QuizFeedbackController controller = loader.getController();
            if (controller != null) {
                controller.setResult(score, total);
            }

            Stage feedbackStage = new Stage();
            feedbackStage.setTitle("Quiz Feedback");
            feedbackStage.setScene(new Scene(root));
            feedbackStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertManager.showError("Unable to open feedback", "Could not open feedback window.");
        }
    }

    private void setRadio(RadioButton rb, String text) {
        String t = safe(text);

        if (t.isBlank()) {
            rb.setText("");
            rb.setVisible(false);
            rb.setManaged(false);

            // If user had previously chosen this hidden option, clear it
            // (prevents “ghost” answers when question has fewer choices)
            int idx = radioToIndex(rb);
            if (chosenAnswers != null && idx != -1 && chosenAnswers[currentIndex] == idx) {
                chosenAnswers[currentIndex] = 0;
            }
        } else {
            rb.setText(t);
            rb.setVisible(true);
            rb.setManaged(true);
        }
    }

    private int toggleToIndex(Toggle t) {
        if (t == answerRB1) return 1;
        if (t == answerRB2) return 2;
        if (t == answerRB3) return 3;
        if (t == answerRB4) return 4;
        return -1;
    }

    private int radioToIndex(RadioButton rb) {
        if (rb == answerRB1) return 1;
        if (rb == answerRB2) return 2;
        if (rb == answerRB3) return 3;
        if (rb == answerRB4) return 4;
        return -1;
    }

    private RadioButton indexToRadio(int idx) {
        return switch (idx) {
            case 1 -> answerRB1;
            case 2 -> answerRB2;
            case 3 -> answerRB3;
            case 4 -> answerRB4;
            default -> null;
        };
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
