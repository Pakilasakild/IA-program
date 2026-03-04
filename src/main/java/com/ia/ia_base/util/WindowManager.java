package com.ia.ia_base.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for window management.
 * Simplifies opening new windows.
 */
public class WindowManager {
    
    /**
     * Opens new window from FXML file
     * @param fxmlPath path to FXML file (e.g., "views/MainView.fxml")
     * @param title window title
     * @return Stage object
     */
    public static Stage openWindow(String fxmlPath, String title) {
        return openWindow(fxmlPath, title, false);
    }
    
    /**
     * Opens new window from FXML file
     * @param fxmlPath path to FXML file
     * @param title window title
     * @param modal whether window should be modal
     * @return Stage object
     */
    public static Stage openWindow(String fxmlPath, String title, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            
            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            
            // Set Stage object in controller if controller is BaseController
            Object controller = loader.getController();
            if (controller instanceof com.ia.ia_base.controllers.BaseController) {
                ((com.ia.ia_base.controllers.BaseController) controller).setStage(stage);
            }
            
            stage.show();
            return stage;
        } catch (IOException e) {
            System.err.println("Error opening window: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Opens window with specified dimensions
     */
    public static Stage openWindow(String fxmlPath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource("/com/ia/ia_base/" + fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
            return stage;
        } catch (IOException e) {
            System.err.println("Error opening window: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Closes window
     */
    public static void closeWindow(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}

