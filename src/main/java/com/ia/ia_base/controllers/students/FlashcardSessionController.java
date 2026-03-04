package com.ia.ia_base.controllers.students;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.models.Flashcard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FlashcardSessionController extends BaseController {

    @FXML public Text flashcardText;
    @FXML public Button nextFlashcardBTN;
    @FXML public Button lastFlashcardBTN;
    @FXML public Button showFlashAnswerBTN;
    @FXML public Button endSessionBTN;

    private final List<Flashcard> flashcards = new ArrayList<>();
    private int currentIndex = 0;
    private boolean showingAnswer = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        endSessionBTN.setOnAction(e -> {
            Stage stage = (Stage) endSessionBTN.getScene().getWindow();
            stage.close();
        });

        nextFlashcardBTN.setOnAction(e -> goNext());
        lastFlashcardBTN.setOnAction(e -> goBack());
        showFlashAnswerBTN.setOnAction(e -> toggleContent());

        setEmptyState("No flashcards loaded.");
    }

    public void setFlashcards(List<Flashcard> chosen) {
        flashcards.clear();

        if (chosen == null || chosen.isEmpty()) {
            setEmptyState("No flashcards selected.");
            return;
        }

        flashcards.addAll(chosen);
        currentIndex = 0;
        showingAnswer = false;

        render();
    }

    private void goBack() {
        if (flashcards.isEmpty()) return;

        if (currentIndex > 0) {
            currentIndex--;
            showingAnswer = false;
            render();
        }
    }

    private void goNext() {
        if (flashcards.isEmpty()) return;

        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
            showingAnswer = false;
            render();
        }
    }

    private void toggleContent() {
        if (flashcards.isEmpty()) return;

        showingAnswer = !showingAnswer;
        render();
    }

    private void render() {
        if (flashcards.isEmpty()) {
            setEmptyState("No flashcards loaded.");
            return;
        }

        Flashcard fc = flashcards.get(currentIndex);

        String header = (currentIndex + 1) + "/" + flashcards.size();
        String body;

        if (showingAnswer) {
            body = safe(fc.getAnswer());
            showFlashAnswerBTN.setText("Show question");
        } else {
            body = safe(fc.getQuestion());
            showFlashAnswerBTN.setText("Show answer");
        }

        flashcardText.setText(header + "\n\n" + body);

        lastFlashcardBTN.setDisable(currentIndex == 0);
        nextFlashcardBTN.setDisable(currentIndex == flashcards.size() - 1);

        showFlashAnswerBTN.setDisable(false);
    }

    private void setEmptyState(String message) {
        flashcardText.setText(message);

        nextFlashcardBTN.setDisable(true);
        lastFlashcardBTN.setDisable(true);
        showFlashAnswerBTN.setDisable(true);

        showFlashAnswerBTN.setText("Show answer");
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
