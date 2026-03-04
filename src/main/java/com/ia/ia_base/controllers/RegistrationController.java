package com.ia.ia_base.controllers;

import com.ia.ia_base.database.dao.RoleDAO;
import com.ia.ia_base.database.dao.UserDAO;
import com.ia.ia_base.models.Role;
import com.ia.ia_base.models.StudentUser;
import com.ia.ia_base.models.TeacherUser;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
/*
TODO add hasher
import com.ia.ia_base.util.PasswordHasher;
*/
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegistrationController extends BaseController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @FXML
    private ToggleGroup accountTypeGroup = new ToggleGroup();
    @FXML
    public Button signUpBTN;
    @FXML
    public RadioButton studentRadio;
    @FXML
    public RadioButton teacherRadio;
    @FXML
    public Button backToLoginBTN;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;

    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupToggleGroup();
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();
        setupButtonActions();
    }

    private void setupToggleGroup() {
        studentRadio.setToggleGroup(accountTypeGroup);
        teacherRadio.setToggleGroup(accountTypeGroup);
    }

    private void setupButtonActions() {
        signUpBTN.setOnAction(e -> handleRegister());

        backToLoginBTN.setOnAction(e -> {
            changeScene("IA/login.fxml");
            if (stage != null) {
                stage.setTitle("FactFlux Login");
            }
        });
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = repeatPasswordField.getText();

        RadioButton selected = (RadioButton) accountTypeGroup.getSelectedToggle();
        String role = (selected == null)
                ? "none"
                : (Objects.equals(selected.getText(), "I am a Student") ? "student" : "teacher");

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.equals("none")) {
            AlertManager.showError("Error", "Please fill in all fields");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            AlertManager.showError("Invalid Email", "Please enter a valid email address");
            return;
        }

        if (password.length() < 4) {
            AlertManager.showError("Weak Password", "Password must be at least 4 characters long");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertManager.showError("Password Mismatch", "Passwords do not match");
            return;
        }

        try {
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                AlertManager.showError("Registration Failed", "Email already registered");
                return;
            }

            Role accountRole = role.equals("teacher")
                    ? roleDAO.findByName("teacher")
                    : roleDAO.findByName("student");

            if (accountRole == null) {
                AlertManager.showError("Error", "System error: " + role + " role not found");
                return;
            }
            User newUser;
            switch (role) {
                case "student":
                    newUser = new StudentUser();
                    break;

                case "teacher":
                    newUser = new TeacherUser();
                    break;

                default:
                    AlertManager.showError("Error", "Unknown account type");
                    return;
            }
            newUser.setEmail(email);
            String hash = PasswordHasher.hashPassword(password);
            newUser.setPasswordHash(hash);
            newUser.setRole(accountRole);
            newUser.setBlocked(false);
            newUser.setMustChangePassword(false);
            userDAO.create(newUser);
            AlertManager.showInfo("Success", "Registration successful! You can now login.");
            handleLoginLink();
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to register: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginLink() {
        changeScene("IA/login.fxml");
        if (stage != null) {
            stage.setTitle("FactFlux Login");
        }
    }
}
