package com.ia.ia_base.controllers;

import com.ia.ia_base.config.AppConfig;
import com.ia.ia_base.database.DatabaseConnection;
import com.ia.ia_base.util.*;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomeController extends BaseController {

    private static final String FLASHCARD_TABLE = "flashcards";
    private static final String QUIZ_TABLE = "quizzes";

    @FXML public Text greetingText;
    @FXML public Text flashcardAmountText;
    @FXML public Text quizAmountText;

    private final Runnable refreshHandler = this::refreshCounts;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupGreeting();
        refreshCounts();

        QuizReloadBus.register(refreshHandler);
        FlashcardReloadBus.register(refreshHandler);
        InformationReloadBus.register(refreshHandler);

        greetingText.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getWindow().setOnHidden(e -> {
                    QuizReloadBus.unregister(refreshHandler);
                    FlashcardReloadBus.unregister(refreshHandler);
                    InformationReloadBus.unregister(refreshHandler);
                });
            }
        });
    }

    private void setupGreeting() {
        if (!SessionManager.getInstance().isLoggedIn() || SessionManager.getInstance().getCurrentUser() == null) {
            greetingText.setText("Welcome");
            return;
        }

        String email = SessionManager.getInstance().getCurrentUser().getEmail();
        String name = (email == null || !email.contains("@")) ? "User" : email.split("@", 2)[0];
        greetingText.setText("Welcome, " + name);
    }

    private void refreshCounts() {
        int flashCount = fetchFlashcardCountBasedOnRole();
        int quizCount = fetchCount(QUIZ_TABLE);

        flashcardAmountText.setText("There are " + flashCount + " flashcards");
        quizAmountText.setText("There are " + quizCount + " quizzes");
    }

    /**
     * Teacher -> count ALL flashcards
     * Student -> count only active flashcards
     */
    private int fetchFlashcardCountBasedOnRole() {
        if (!AppConfig.isUseDatabase()) {
            return 0;
        }

        boolean teacher = SessionManager.getInstance().isTeacher();

        String sqlAll = "SELECT COUNT(*) AS total FROM " + FLASHCARD_TABLE;
        String sqlActiveOnly = "SELECT COUNT(*) AS total FROM " + FLASHCARD_TABLE + " WHERE active = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            if (teacher) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlAll);
                     ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt("total") : 0;
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(sqlActiveOnly)) {
                    stmt.setBoolean(1, true);
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next() ? rs.getInt("total") : 0;
                    }
                }
            }

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load flashcards count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private int fetchCount(String tableName) {
        if (!AppConfig.isUseDatabase()) {
            return 0;
        }

        String sql = "SELECT COUNT(*) AS total FROM " + tableName;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt("total") : 0;

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load " + tableName + " count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
