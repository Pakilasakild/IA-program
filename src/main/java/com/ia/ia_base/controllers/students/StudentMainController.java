package com.ia.ia_base.controllers.students;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.net.URL;

public class StudentMainController extends BaseController {

    @FXML
    public MenuItem changePasswordMenu;
    @FXML
    public MenuItem logoutMenu;
    @FXML
    public MenuItem quitMenu;
    @FXML
    public MenuItem viewFlashcardsMenu;
    @FXML
    public MenuItem viewQuizzesMenu;
    @FXML
    public BorderPane mainPage;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupMenuActions();
    }

    public void setupMenuActions(){
        quitMenu.setOnAction(e -> {
            quitMenu();
        });

        logoutMenu.setOnAction(e -> {
            logoutMenu();
        });
        changePasswordMenu.setOnAction(e -> {
            changePasswordMenu();
        });
        viewFlashcardsMenu.setOnAction(e -> {
            try {
                URL fxml = getClass().getResource("/com/ia/ia_base/IA/Student/flashcards.fxml");
                assert fxml != null;
                AnchorPane pane = FXMLLoader.load(fxml);
                mainPage.setCenter(pane);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        viewQuizzesMenu.setOnAction(e -> {
            try {
                URL fxml = getClass().getResource("/com/ia/ia_base/IA/Student/quizzes.fxml");
                assert fxml != null;
                AnchorPane pane = FXMLLoader.load(fxml);
                mainPage.setCenter(pane);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
