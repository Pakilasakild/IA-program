package com.ia.ia_base.controllers.students;

import com.ia.ia_base.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class QuizFeedbackController extends BaseController {

    @FXML private Text pointsFeedbackField;
    @FXML private Button closeWindowBTN;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeWindowBTN.setOnAction(e -> {
            Stage stage = (Stage) closeWindowBTN.getScene().getWindow();
            stage.close();
        });
    }

    public void setResult(int score, int total) {
        pointsFeedbackField.setText("You scored " + score + " out of " + total + ".");
    }
}
