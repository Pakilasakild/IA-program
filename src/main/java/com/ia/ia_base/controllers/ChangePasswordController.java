package com.ia.ia_base.controllers;

import com.ia.ia_base.database.dao.UserDAO;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
import com.ia.ia_base.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ChangePasswordController extends BaseController{

    @FXML
    public Button changePasswordBTN;
    @FXML
    public PasswordField oldPasswordField;
    @FXML
    public PasswordField newPasswordField;
    @FXML
    public PasswordField repeatNewPasswordField;
    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        setupButtonAction();
    }

    public void setupButtonAction(){
        changePasswordBTN.setOnAction(e -> {
            String oldPass = oldPasswordField.getText();
            String newPass = newPasswordField.getText();
            String repNewPass = repeatNewPasswordField.getText();
            if (oldPass.isEmpty() || newPass.isEmpty() || repNewPass.isEmpty()) {
                AlertManager.showError("Error", "Fill all the fields with the correct information");
                return;
            }
            if (newPass.length() < 4 || repNewPass.length() < 4) {
                AlertManager.showError("Error", "Password is too short");
                return;
            }
            if (!newPass.equals(repNewPass)) {
                AlertManager.showError("Error", "Passwords do not match");
                return;
            }
            if (PasswordHasher.verifyPassword(oldPass, SessionManager.getInstance().getCurrentUser().getPasswordHash())) {
                SessionManager.getInstance().getCurrentUser().setPasswordHash(PasswordHasher.hashPassword(newPass));
                try {
                    AlertManager.showConfirmation("Successful!", "Password changed", "Your password has been successfully changed.");
                    userDAO.update(SessionManager.getInstance().getCurrentUser());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
