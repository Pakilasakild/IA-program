package com.ia.ia_base;

import com.ia.ia_base.config.AppConfig;
import com.ia.ia_base.controllers.LoginController;
import com.ia.ia_base.controllers.students.StudentMainController;
import com.ia.ia_base.controllers.teachers.TeacherMainController;
import com.ia.ia_base.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Main application class.
 * This class starts the JavaFX application.
 */
public class MainApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Try to connect to database only if it's enabled
        if (AppConfig.isUseDatabase()) {
            try {
                DatabaseConnection.getInstance().getConnection();
            } catch (SQLException e) {
                System.err.println("WARNING: Failed to connect to database!");
                System.err.println("Check settings in DatabaseConnection class.");
                System.err.println("Or disable DB usage with AppConfig.setUseDatabase(false)");
            }
        } else {
            System.out.println("Database not used. Application will run without DB.");
        }


        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("IA/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 470, 400);


        LoginController controller = fxmlLoader.getController();
        if (controller != null) {
            controller.setStage(stage);
        }

        stage.setTitle("FactFlux Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Close database connection before closing application
        DatabaseConnection.getInstance().closeConnection();
    }
}

