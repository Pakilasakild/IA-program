package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
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
                try {
                    tagDAO.create(tag);
                    AlertManager.showInfo("Success", "Tag successfully created");
                    closeWindow();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
        });
    }
}
