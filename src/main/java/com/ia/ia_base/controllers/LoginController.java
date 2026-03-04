package com.ia.ia_base.controllers;

import com.ia.ia_base.database.dao.UserDAO;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
import com.ia.ia_base.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Objects;

public class LoginController extends BaseController {

    @FXML public Button logInBTN;
    @FXML public RadioButton studentRadio;
    @FXML public RadioButton teacherRadio;
    @FXML public Button forgotPasswordBTN;
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
        forgotPasswordBTN.setOnAction(e -> handleForgotPassword());
    }

    private void handleForgotPassword() {
        changeScene("IA/forgotPassword.fxml");
        if (stage != null) {
            stage.setTitle("Forgot Password");
            stage.setResizable(false);
            stage.setWidth(420);
            stage.setHeight(300);
        }
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

            // If must change password -> force modal popup BEFORE entering the app
            if (user.isMustChangePassword()) {
                AlertManager.showWarning("Password Change Required", "You must change your password to continue.");
                boolean changed = openMustChangePasswordPopup(user);
                if (!changed) {
                    AlertManager.showError("Password Change Required", "You must change your password to continue.");
                    return;
                }
            }

            // Now login proceeds normally
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

    /**
     * Opens mustChangePassword.fxml as a blocking modal.
     * Returns true only if password was successfully changed and persisted to DB.
     */
    private boolean openMustChangePasswordPopup(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/IA/mustChangePassword.fxml")
            );
            Parent root = loader.load();

            MustChangePasswordController controller = loader.getController();
            if (controller == null) {
                AlertManager.showError("Error", "mustChangePassword.fxml has no controller wired.");
                return false;
            }

            // Create modal stage
            Stage popup = new Stage();
            popup.setTitle("Change Password Required");
            popup.initModality(Modality.WINDOW_MODAL);
            if (stage != null) {
                popup.initOwner(stage);
            }
            popup.setResizable(false);
            popup.setScene(new Scene(root));

            // Give controller the stage and user
            controller.setStage(popup);
            controller.setUser(user);

            // Block until user finishes
            popup.showAndWait();

            return controller.isPasswordChanged();

        } catch (Exception e) {
            e.printStackTrace();
            AlertManager.showError("Error", "Could not open must change password window.");
            return false;
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
