package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.FlashcardReloadBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteTagController extends BaseController {

    @FXML public Button deleteTagBTN;
    @FXML public Button cancelBTN;
    @FXML public ComboBox<Tag> chooseTagCombo;

    private TagDAO tagDAO;
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tagDAO = new TagDAO();

        loadTags();
        chooseTagCombo.setItems(tags);

        setupActions();
    }

    private void setupActions() {
        cancelBTN.setOnAction(e -> closeWindow());

        deleteTagBTN.setOnAction(e -> {
            Tag selected = chooseTagCombo.getValue();

            if (selected == null) {
                AlertManager.showError("No tag selected", "Choose a tag to delete.");
                return;
            }

            try {
                tagDAO.delete(selected.getId());
                tags.remove(selected);
                chooseTagCombo.getSelectionModel().clearSelection();

                AlertManager.showInfo("Success", "Tag deleted.");
                FlashcardReloadBus.requestReload();

                closeWindow();

            } catch (SQLException ex) {
                AlertManager.showError("Database Error",
                        "Could not delete tag. It may be used by flashcards or quizzes.\n\n" + ex.getMessage());
            }
        });
    }

    private void loadTags() {
        try {
            List<Tag> all = tagDAO.findAll();
            tags.setAll(all);
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load tags: " + e.getMessage());
        }
    }
}
