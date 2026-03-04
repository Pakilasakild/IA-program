package com.ia.ia_base.controllers.students;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.FlashcardDAO;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Flashcard;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FlashcardController extends BaseController {

    private final FlashcardDAO flashcardDAO = new FlashcardDAO();
    private final TagDAO tagDAO = new TagDAO();

    private final ObservableList<Flashcard> allFlashcards = FXCollections.observableArrayList();
    private final ObservableList<Flashcard> filteredFlashcards = FXCollections.observableArrayList();
    private final ObservableList<Tag> allTags = FXCollections.observableArrayList();

    private final Tag ALL_TAG = new Tag("All");
    {
        ALL_TAG.setId(-1);
    }

    @FXML private Button startFlashcardSessionBTN;
    @FXML private ListView<Flashcard> flashcardListView;
    @FXML private ComboBox<Tag> tagSelect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFlashcardListView();
        flashcardListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupTagComboBox();
        loadInitialData();

        startFlashcardSessionBTN.setOnAction(e -> startSession());
    }

    private void setupFlashcardListView() {
        flashcardListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Flashcard item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String q = item.getQuestion() == null ? "" : item.getQuestion();
                List<String> tags = item.getTags();

                String tagText = (tags == null || tags.isEmpty())
                        ? ""
                        : " [" + String.join(", ", tags) + "]";

                setText(q + tagText);
            }
        });
    }

    private void setupTagComboBox() {
        tagSelect.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTagName());
            }
        });

        tagSelect.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTagName());
            }
        });

        tagSelect.valueProperty().addListener((obs, oldVal, newVal) -> applyTagFilter(newVal));
    }

    private void loadInitialData() {
        try {
            // Load tags
            allTags.clear();
            allTags.add(ALL_TAG);
            allTags.addAll(tagDAO.findAll());
            tagSelect.setItems(allTags);
            tagSelect.getSelectionModel().select(ALL_TAG);

            // Load flashcards (ONLY ACTIVE)
            allFlashcards.clear();

            List<Flashcard> fetched = flashcardDAO.findAllWithTags();
            List<Flashcard> activeOnly = new ArrayList<>();
            for (Flashcard fc : fetched) {
                if (fc != null && fc.isActive()) {
                    activeOnly.add(fc);
                }
            }
            allFlashcards.addAll(activeOnly);

            filteredFlashcards.setAll(allFlashcards);
            flashcardListView.setItems(filteredFlashcards);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void applyTagFilter(Tag selectedTag) {
        if (selectedTag == null || selectedTag.getId() == -1) {
            filteredFlashcards.setAll(allFlashcards);
            return;
        }

        String tagName = selectedTag.getTagName();
        filteredFlashcards.setAll(
                allFlashcards.filtered(fc ->
                        fc.getTags() != null && fc.getTags().contains(tagName)
                )
        );
    }

    private void startSession() {
        List<Flashcard> chosen = List.copyOf(flashcardListView.getSelectionModel().getSelectedItems());

        if (chosen.isEmpty()) {
            AlertManager.showError("No flashcards selected", "Please select flashcards.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/IA/Student/flashcardSession.fxml")
            );
            Parent root = loader.load();

            FlashcardSessionController controller = loader.getController();
            if (controller != null) {
                controller.setFlashcards(chosen);
            }

            Stage sessionStage = new Stage();
            sessionStage.setTitle("Flashcard Session");
            sessionStage.setScene(new Scene(root));
            sessionStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertManager.showError("Unable to open flashcard session", "Could not open the flashcard session window.");
        }
    }
}
