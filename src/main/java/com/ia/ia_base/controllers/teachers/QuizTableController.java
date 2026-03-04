package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuizDAO;
import com.ia.ia_base.database.dao.QuizTagDAO;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.QuizReloadBus;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizTableController extends BaseController {

    @FXML private ComboBox<Tag> tagSelect;

    @FXML private TreeTableView<Quiz> quizTreeTableView;
    @FXML private TreeTableColumn<Quiz, String> quizNameColumn;
    @FXML private TreeTableColumn<Quiz, String> quizTagsColumn;
    @FXML private TreeTableColumn<Quiz, Void> editColumn;

    private final QuizDAO quizDAO = new QuizDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final QuizTagDAO quizTagDAO = new QuizTagDAO();

    private final ObservableList<Tag> tagItems = FXCollections.observableArrayList();

    private static final Tag ALL_TAGS = new Tag("All tags");
    static { ALL_TAGS.setId(-1); }

    private final Runnable reloadHandler = () -> reloadTablePreserveFilter();

    @Override
    public void initialize(URL location, java.util.ResourceBundle resources) {
        setupTree();
        setupColumns();
        setupTagCombo();

        reloadTablePreserveFilter();

        QuizReloadBus.register(reloadHandler);

        quizTreeTableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getWindow().setOnHidden(e -> QuizReloadBus.unregister(reloadHandler));
            }
        });
    }

    private void setupTree() {
        quizTreeTableView.setShowRoot(false);
        TreeItem<Quiz> root = new TreeItem<>();
        root.setExpanded(true);
        quizTreeTableView.setRoot(root);
    }

    private void setupColumns() {
        quizNameColumn.setCellValueFactory(param -> {
            Quiz q = param.getValue() == null ? null : param.getValue().getValue();
            return new ReadOnlyStringWrapper(q == null ? "" : q.getName());
        });

        quizTagsColumn.setCellValueFactory(param -> {
            Quiz q = param.getValue() == null ? null : param.getValue().getValue();
            if (q == null || q.getTags() == null || q.getTags().isEmpty()) {
                return new ReadOnlyStringWrapper("");
            }
            return new ReadOnlyStringWrapper(String.join(", ", q.getTags()));
        });

        editColumn.setCellFactory(col -> new TreeTableCell<>() {
            private final Hyperlink link = new Hyperlink("Edit");

            {
                link.setOnAction(e -> {
                    Quiz quiz = getTreeTableRow().getItem();
                    if (quiz != null) openEditQuiz(quiz);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : link);
            }
        });
    }

    private void setupTagCombo() {
        tagSelect.setItems(tagItems);

        tagSelect.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getId() == -1 ? "All tags" : item.getTagName());
            }
        });

        tagSelect.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("All tags");
                else setText(item.getId() == -1 ? "All tags" : item.getTagName());
            }
        });

        tagSelect.valueProperty().addListener((obs, oldVal, newVal) -> loadQuizzes(newVal));
    }

    private void reloadTablePreserveFilter() {
        Tag selected = tagSelect.getValue();

        loadTags();

        if (selected == null) {
            tagSelect.getSelectionModel().select(ALL_TAGS);
        } else {
            boolean exists = tagItems.stream().anyMatch(t -> t.getId() == selected.getId());
            tagSelect.getSelectionModel().select(exists ? selected : ALL_TAGS);
        }

        loadQuizzes(tagSelect.getValue());
    }

    private void loadTags() {
        try {
            tagItems.clear();
            tagItems.add(ALL_TAGS);
            tagItems.addAll(tagDAO.findAll());
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load tags: " + e.getMessage());
        }
    }

    private void loadQuizzes(Tag selectedTag) {
        try {
            List<Quiz> quizzes;

            if (selectedTag == null || selectedTag.getId() == -1) {
                quizzes = quizDAO.findAll();
            } else {
                quizzes = quizDAO.findAllByTagId(selectedTag.getId());
            }

            for (Quiz q : quizzes) {
                List<Tag> tags = quizTagDAO.findTagsForQuiz(q.getId());
                ArrayList<String> names = new ArrayList<>();
                for (Tag t : tags) names.add(t.getTagName());
                q.setTags(names);
            }

            TreeItem<Quiz> root = quizTreeTableView.getRoot();
            root.getChildren().clear();
            for (Quiz q : quizzes) root.getChildren().add(new TreeItem<>(q));

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load quizzes: " + e.getMessage());
        }
    }

    private void openEditQuiz(Quiz quiz) {
        try {
            URL fxml = Objects.requireNonNull(
                    getClass().getResource("/com/ia/ia_base/IA/Teachers/editQuiz.fxml"),
                    "editQuiz.fxml not found on classpath"
            );

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();

            EditQuizController controller = loader.getController();

            controller.setQuiz(quiz);

            Stage stage = new Stage();
            stage.setTitle("Edit Quiz");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            AlertManager.showError("UI Error", "Failed to open edit quiz:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
