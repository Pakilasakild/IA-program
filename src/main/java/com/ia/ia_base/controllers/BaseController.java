package com.ia.ia_base.controllers;

import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import com.ia.ia_base.util.WindowManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base Controller class.
 * All other controllers can extend this class.
 */
public abstract class BaseController implements Initializable {
    protected Stage stage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Base initialization - can be overridden in child classes
    }
    
    /**
     * Sets Stage object (used from Application class)
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Opens new window
     */
    protected void openNewWindow(String fxmlPath, String title) {
        WindowManager.openWindow(fxmlPath, title);
    }
    
    /**
     * Opens modal window
     */
    protected void openModalWindow(String fxmlPath, String title) {
        WindowManager.openWindow(fxmlPath, title, true);
    }
    
    /**
     * Changes current window content (scene)
     * Useful when you want to change window instead of opening new one
     */
    protected void changeScene(String fxmlPath) {
        if (stage == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/ia/ia_base/" + fxmlPath)
            );
            Parent root = loader.load();
            
            // Set Stage object in new controller
            BaseController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }
            
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            System.err.println("Error changing window: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    /**
     * Closes current window
     */
    protected void closeWindow() {
        if (stage != null) {
            WindowManager.closeWindow(stage);
        }
    }

    public void quitMenu() {
        Platform.exit();
    }

    public void logoutMenu() {
        if (AlertManager.confirmLogout()) {
            changeScene("IA/login.fxml");

            if (stage != null) {
                stage.setTitle("FactFlux Login");
                stage.setWidth(470);
                stage.setHeight(450);
            }
            SessionManager.getInstance().logout();
        }
    }

    public void changePasswordMenu() {
        openNewWindow("/com/ia/ia_base/IA/changePassword.fxml", "Change password");
    }
}

