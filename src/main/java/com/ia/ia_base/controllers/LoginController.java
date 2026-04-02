package com.ia.ia_base.controllers;

import com.ia.ia_base.database.dao.UserDAO;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
import com.ia.ia_base.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Objects;

public class LoginController extends BaseController {

    @FXML public Button logInBTN;
    @FXML public RadioButton studentRadio;
    @FXML public RadioButton teacherRadio;
    @FXML public Button createNewAccountBTN;
    @FXML private ToggleGroup accountTypeGroup;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        userDAO = new UserDAO();
        setupToggleGroup();
        setupButtonActions();
    }

    private void setupToggleGroup() {
        accountTypeGroup = new ToggleGroup();
        studentRadio.setToggleGroup(accountTypeGroup);
        teacherRadio.setToggleGroup(accountTypeGroup);
    }

    private void setupButtonActions() {
        logInBTN.setOnAction(e -> handleLogin());
        createNewAccountBTN.setOnAction(e -> handleRegisterLink());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText();

        RadioButton selected = (RadioButton) accountTypeGroup.getSelectedToggle();
        String role = selected == null ? "none"
                : (Objects.equals(selected.getText(), "I am a Student") ? "student" : "teacher");

        if (email.isEmpty() || password.isEmpty() || Objects.equals(role, "none")) {
            AlertManager.showError("Error", "Please enter email, password and choose your role.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);

            if (user == null) {
                AlertManager.showError("Login Failed", "Invalid role, email or password");
                return;
            }

            if (user.isBlocked()) {
                AlertManager.showError("Account Blocked", "Your account has been blocked. Please contact an administrator.");
                return;
            }

            if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                AlertManager.showError("Login Failed", "Invalid role, email or password");
                return;
            }

            if (!user.getRole().getName().equalsIgnoreCase(role)) {
                AlertManager.showError("Login Failed", "Invalid role, email or password");
                return;
            }

            SessionManager.getInstance().setCurrentUser(user);

            if (role.equals("student")) {
                changeScene("IA/Student/StudentViewMenu.fxml");
                if (stage != null) {
                    stage.setTitle("FactFlux");
                    stage.setResizable(true);
                    stage.setWidth(810);
                    stage.setHeight(540);
                }
            } else {
                changeScene("IA/Teachers/TeacherViewMenu.fxml");
                if (stage != null) {
                    stage.setTitle("FactFlux, Teacher Environment");
                    stage.setResizable(true);
                    stage.setWidth(1000);
                    stage.setHeight(540);
                }
            }

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink() {
        changeScene("IA/createAccount.fxml");
        if (stage != null) {
            stage.setTitle("FactFlux Create Account");
        }
    }
}
