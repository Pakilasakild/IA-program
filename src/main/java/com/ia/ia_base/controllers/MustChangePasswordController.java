package com.ia.ia_base.controllers;

import com.ia.ia_base.database.dao.UserDAO;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MustChangePasswordController extends BaseController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField repeatNewPasswordField;
    @FXML private Button changePasswordBTN;

    private final UserDAO userDAO = new UserDAO();

    private User user;
    private boolean passwordChanged = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changePasswordBTN.setOnAction(e -> handleChangePassword());
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    private void handleChangePassword() {
        if (user == null) {
            AlertManager.showError("Error", "No user loaded for password change.");
            return;
        }

        String newPass = newPasswordField.getText() == null ? "" : newPasswordField.getText();
        String repeat = repeatNewPasswordField.getText() == null ? "" : repeatNewPasswordField.getText();

        if (newPass.isEmpty() || repeat.isEmpty()) {
            AlertManager.showError("Error", "Please fill both password fields.");
            return;
        }

        if (!newPass.equals(repeat)) {
            AlertManager.showError("Error", "Passwords do not match.");
            return;
        }

        try {
            String newHash = PasswordHasher.hashPassword(newPass);

            user.setPasswordHash(newHash);
            user.setMustChangePassword(false);

            int updated = userDAO.update(user);
            if (updated <= 0) {
                AlertManager.showError("Error", "Could not update password in the database.");
                return;
            }

            passwordChanged = true;

            // Close popup
            Stage s = (Stage) changePasswordBTN.getScene().getWindow();
            s.close();

        } catch (IllegalArgumentException ex) {
            AlertManager.showError("Error", ex.getMessage());
        } catch (SQLException ex) {
            AlertManager.showError("Database Error", "Failed to update password: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
