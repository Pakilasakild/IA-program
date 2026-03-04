package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.models.Flashcard;
import com.ia.ia_base.models.Question;
import com.ia.ia_base.util.AlertManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateQuizQuestionController extends BaseController {

    @FXML
    public TextField enterQuestionTextField;
    @FXML
    public Button createQuestionBTN;
    @FXML
    public Button cancelBTN;



    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupMenuActions();
    }

    public void setupMenuActions(){
        cancelBTN.setOnAction(e -> {
            closeWindow();
        });
        createQuestionBTN.setOnAction(e -> {
            if (enterQuestionTextField.getText().isEmpty()) {
                AlertManager.showError("Error creating question", "Fill enter the question.");
                return;
            }
            Question question = new Question(enterQuestionTextField.getText());
            openAnswerWindow(question);
            closeWindow();
        });
    }

    private void openAnswerWindow(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/ia/ia_base/IA/Teachers/createAnswerForQuizQuestion.fxml"
            ));
            Parent root = loader.load();
            createAnswerForQuizQuestionController editCtrl = loader.getController();
            editCtrl.setQuestion(question);

            Stage stage = new Stage();
            stage.setTitle("Edit flashcard");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (Exception e) {
            AlertManager.showError("UI Error", "Failed to open Edit window: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
