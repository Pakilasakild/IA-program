package com.ia.ia_base.controllers;

import com.ia.ia_base.database.dao.UserDAO;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.security.SecureRandom;
import java.sql.SQLException;

public class ForgotPasswordController extends BaseController {

    @FXML
    public Button backToLoginBTN;
    @FXML
    private TextField emailField;
    @FXML
    private Button genPassBTN;

    private UserDAO userDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        userDAO = new UserDAO();
        genPassBTN.setOnAction(e -> {
            handleSetTemporaryPassword();
        });
        backToLoginBTN.setOnAction(e -> {
            changeScene("IA/login.fxml");
            if (stage != null) {
                stage.setTitle("FactFlux Login");
                stage.setResizable(false);
                stage.setWidth(470);
                stage.setHeight(440);
            }
        });
    }

    private void handleSetTemporaryPassword() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        if (email.isEmpty()) {
            AlertManager.showError("Error", "Please enter your email.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);

            if (user == null) {
                AlertManager.showError("Not found", "No account exists with that email.");
                return;
            }

            if (user.isBlocked()) {
                AlertManager.showError("Account Blocked", "Your account has been blocked. Please contact an administrator.");
                return;
            }

            String tempPassword = "TemporaryPassword";
            String newHash = PasswordHasher.hashPassword(tempPassword);

            user.setPasswordHash(newHash);
            user.setMustChangePassword(true);

            int updatedRows = userDAO.update(user);
            if (updatedRows <= 0) {
                AlertManager.showError("Reset Failed", "Could not update the user password in the database.");
                return;
            }

            AlertManager.showWarning("Temporary Password", "Your temporary password is:\n\n" + tempPassword + "\n\nLog in and you will be required to change it.");

            changeScene("IA/login.fxml");
            if (stage != null) {
                stage.setTitle("FactFlux Login");
                stage.setResizable(false);
                stage.setWidth(470);
                stage.setHeight(450);
            }

        } catch (SQLException ex) {
            AlertManager.showError("Database Error", "Failed to connect to database: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            AlertManager.showError("Error", ex.getMessage());
        }
    }
}
