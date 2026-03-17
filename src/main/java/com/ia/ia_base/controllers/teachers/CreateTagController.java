package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.TagReloadBus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateTagController extends BaseController {
    @FXML
    public TextField TagTextField;
    @FXML
    public Button createTagBTN;
    @FXML
    public Button cancelBTN;

    public TagDAO tagDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupActions();
    }

    private void setupActions() {
        cancelBTN.setOnAction(e -> {
            closeWindow();
        });

        createTagBTN.setOnAction(e -> {
            Tag tag = new Tag(TagTextField.getText());
            tagDAO = new TagDAO();

            if (tag.getTagName().isEmpty()) {
                AlertManager.showError("Error creating tag", "Tag name cannot be empty.");
            } else {
                try {
                    if (tagDAO.findByTag(tag.getTagName()) != null) {
                        AlertManager.showError("Error creating tag", "Tag already exists.");
                    } else {
                        tagDAO.create(tag);
                        TagReloadBus.notifyReload();
                        AlertManager.showInfo("Success", "Tag successfully created");
                        closeWindow();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}