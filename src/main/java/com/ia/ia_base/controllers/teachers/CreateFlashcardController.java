package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.FlashcardDAO;
import com.ia.ia_base.models.Flashcard;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.FlashcardReloadBus;
import com.ia.ia_base.util.InformationReloadBus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class CreateFlashcardController extends BaseController {

    @FXML
    public AnchorPane createFlashcard;
    @FXML
    public TextField questionTextFieldFlash;
    @FXML
    public TextField answerTextFieldFlash;
    @FXML
    public Button createFlashcardBTN;
    @FXML
    public Button cancelBTN;
    private FlashcardDAO flashcardDAO;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupMenuActions();
    }

    public void setupMenuActions() {
        cancelBTN.setOnAction(e -> {
            closeWindow();
        });
        createFlashcardBTN.setOnAction(e -> {
            flashcardDAO = new FlashcardDAO();

            if (questionTextFieldFlash.getText().isEmpty() || answerTextFieldFlash.getText().isEmpty()) {
                AlertManager.showError("Error creating flashcard", "Fill all the fields.");
                return;
            }

            Flashcard flashcard = new Flashcard(questionTextFieldFlash.getText(), answerTextFieldFlash.getText());
            try {
                flashcardDAO.create(flashcard);

                FlashcardReloadBus.requestReload();
                InformationReloadBus.requestReload();

                AlertManager.showInfo("Success", "Successfully created flashcard.");
                closeWindow();
            } catch (SQLException ex) {
                AlertManager.showError("Database Error", ex.getMessage());
            }
        });
    }
}
