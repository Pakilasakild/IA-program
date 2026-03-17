package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.FlashcardDAO;
import com.ia.ia_base.models.Flashcard;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.FlashcardReloadBus;
import com.ia.ia_base.util.InformationReloadBus;
import com.ia.ia_base.util.TagReloadBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class FlashcardTableController extends BaseController {

    private static final String ALL_TAGS = "All";

    private final ObservableList<Flashcard> flashcardsObs = FXCollections.observableArrayList();
    private final ObservableList<String> tagOptions = FXCollections.observableArrayList();

    private List<Flashcard> allFlashcards = new ArrayList<>();

    @FXML public TableView<Flashcard> flashTableTeach;
    @FXML public TableColumn<Flashcard, String> questionsFlashColumn;
    @FXML public TableColumn<Flashcard, ArrayList<String>> tagsFlashColumn;
    @FXML public TableColumn<Flashcard, Boolean> activeFlashColumn;
    @FXML public TableColumn<Flashcard, Void> editFlashColumn;
    @FXML public TableColumn<Flashcard, Void> delFlashColumn;

    @FXML public ComboBox<String> tagSelect;

    private FlashcardDAO flashcardDAO;

    private final Runnable flashcardReloadHandler = this::reloadTable;
    private final Runnable tagReloadHandler = this::reloadTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flashcardDAO = new FlashcardDAO();

        setupColumns();
        setupTagFilter();

        flashTableTeach.setEditable(true);
        flashTableTeach.setItems(flashcardsObs);

        reloadTable();

        FlashcardReloadBus.register(flashcardReloadHandler);
        TagReloadBus.register(tagReloadHandler);

        flashTableTeach.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.setOnHidden(e -> {
                            FlashcardReloadBus.unregister(flashcardReloadHandler);
                            TagReloadBus.unregister(tagReloadHandler);
                        });
                    }
                });
            }
        });
    }

    private void setupTagFilter() {
        tagSelect.setItems(tagOptions);

        tagSelect.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ALL_TAGS : item);
            }
        });

        tagSelect.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });

        tagSelect.valueProperty().addListener((obs, oldVal, newVal) -> applyTagFilter());

        if (tagSelect.getValue() == null) {
            tagSelect.getSelectionModel().select(ALL_TAGS);
        }
    }

    public void reloadTable() {
        try {
            List<Flashcard> flashcards = flashcardDAO.findAllWithTags();

            for (Flashcard fc : flashcards) {
                attachActiveListener(fc);
            }

            allFlashcards = flashcards;
            rebuildTagOptions(allFlashcards);
            applyTagFilter();

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to reload flashcards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rebuildTagOptions(List<Flashcard> flashcards) {
        String previouslySelected = tagSelect.getValue();
        if (previouslySelected == null || previouslySelected.isBlank()) {
            previouslySelected = ALL_TAGS;
        }

        SortedSet<String> tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Flashcard fc : flashcards) {
            List<String> t = fc.getTags();
            if (t == null) continue;
            for (String name : t) {
                if (name != null && !name.isBlank()) tags.add(name.trim());
            }
        }

        tagOptions.clear();
        tagOptions.add(ALL_TAGS);
        tagOptions.addAll(tags);

        if (tagOptions.contains(previouslySelected)) {
            tagSelect.getSelectionModel().select(previouslySelected);
        } else {
            tagSelect.getSelectionModel().select(ALL_TAGS);
        }
    }

    private void applyTagFilter() {
        String selected = tagSelect.getValue();
        if (selected == null || selected.isBlank()) selected = ALL_TAGS;

        if (ALL_TAGS.equalsIgnoreCase(selected)) {
            flashcardsObs.setAll(allFlashcards);
            return;
        }

        List<Flashcard> filtered = new ArrayList<>();
        for (Flashcard fc : allFlashcards) {
            List<String> tags = fc.getTags();
            if (tags == null) continue;

            for (String t : tags) {
                if (t != null && t.equalsIgnoreCase(selected)) {
                    filtered.add(fc);
                    break;
                }
            }
        }
        flashcardsObs.setAll(filtered);
    }

    private void setupColumns() {
        questionsFlashColumn.setCellValueFactory(new PropertyValueFactory<>("question"));

        tagsFlashColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        tagsFlashColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ArrayList<String> item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.join(", ", item));
            }
        });

        activeFlashColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        activeFlashColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeFlashColumn));
        activeFlashColumn.setEditable(true);

        editFlashColumn.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink editLink = new Hyperlink("Edit");

            {
                editLink.setOnAction(e -> {
                    Flashcard flashcard = getTableView().getItems().get(getIndex());
                    openEditWindow(flashcard);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editLink);
            }
        });

        delFlashColumn.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink deleteLink = new Hyperlink("Delete");

            {
                deleteLink.setOnAction(e -> {
                    Flashcard flashcard = getTableView().getItems().get(getIndex());
                    onDelete(flashcard);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteLink);
            }
        });
    }

    private void attachActiveListener(Flashcard fc) {
        final boolean[] guard = {false};
        fc.activeProperty().addListener((obs, oldVal, newVal) -> {
            if (guard[0]) return;

            try {
                flashcardDAO.update(fc);
            } catch (Exception ex) {
                guard[0] = true;
                fc.setActive(oldVal);
                guard[0] = false;

                AlertManager.showError("Database Error",
                        "Failed to update active status for id=" + fc.getId() + ": " + ex.getMessage());
            }
        });
    }

    private void openEditWindow(Flashcard flashcard) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/ia/ia_base/IA/Teachers/editFlashcards.fxml"
            ));
            Parent root = loader.load();

            EditFlashcardController editCtrl = loader.getController();
            editCtrl.setFlashcard(flashcard);

            editCtrl.setOnSaved(() -> {
                reloadTable();
                flashTableTeach.refresh();
            });

            Stage stage = new Stage();
            stage.setTitle("Edit flashcard");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(flashTableTeach.getScene().getWindow());
            stage.show();

        } catch (Exception e) {
            AlertManager.showError("UI Error", "Failed to open Edit window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onDelete(Flashcard flashcard) {
        try {
            if (AlertManager.showConfirmation("Delete flashcard", "Are you sure you want to delete this flashcard?", "")) {
                flashcardDAO.delete(flashcard.getId());
                reloadTable();
                InformationReloadBus.requestReload();
            }
        } catch (SQLException e) {
            AlertManager.showError("Database Error",
                    "Failed to delete flashcard id=" + flashcard.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}