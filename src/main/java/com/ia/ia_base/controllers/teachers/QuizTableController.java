package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuizDAO;
import com.ia.ia_base.database.dao.QuizTagDAO;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.QuizReloadBus;
import com.ia.ia_base.util.TagReloadBus;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

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
    private final List<Quiz> allQuizzes = new ArrayList<>();

    private static final Tag ALL_TAGS = new Tag("All");
    static {
        ALL_TAGS.setId(-1);
    }

    private final Runnable reloadHandler = this::reloadTablePreserveFilter;
    private final Runnable tagReloadHandler = this::reloadTablePreserveFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTree();
        setupColumns();
        setupTagCombo();

        reloadTablePreserveFilter();

        QuizReloadBus.register(reloadHandler);
        TagReloadBus.register(tagReloadHandler);

        quizTreeTableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.setOnHidden(e -> {
                            QuizReloadBus.unregister(reloadHandler);
                            TagReloadBus.unregister(tagReloadHandler);
                        });
                    }
                });
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

        editColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(null));
        editColumn.setSortable(false);

        editColumn.setCellFactory(col -> new TreeTableCell<>() {
            private final Hyperlink link = new Hyperlink("Edit");

            {
                link.setOnAction(e -> {
                    TreeTableRow<Quiz> row = getTreeTableRow();
                    if (row == null) {
                        return;
                    }

                    Quiz quiz = row.getItem();
                    if (quiz != null) {
                        openEditQuiz(quiz);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                TreeTableRow<Quiz> row = getTreeTableRow();
                Quiz quiz = row == null ? null : row.getItem();

                setText(null);
                setGraphic(empty || quiz == null ? null : link);
            }
        });
    }

    private void setupTagCombo() {
        tagSelect.setItems(tagItems);

        tagSelect.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item.getId() == -1) {
                    setText("All");
                } else {
                    setText(item.getTagName());
                }
            }
        });

        tagSelect.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All");
                } else if (item.getId() == -1) {
                    setText("All");
                } else {
                    setText(item.getTagName());
                }
            }
        });

        tagSelect.valueProperty().addListener((obs, oldVal, newVal) -> applyTagFilter(newVal));
    }

    private void reloadTablePreserveFilter() {
        Tag previouslySelected = tagSelect.getValue();

        try {
            loadAllQuizzesWithTags();
            rebuildTagOptions();

            if (previouslySelected == null) {
                tagSelect.getSelectionModel().select(ALL_TAGS);
            } else {
                boolean exists = tagItems.stream().anyMatch(t -> t.getId() == previouslySelected.getId());
                tagSelect.getSelectionModel().select(exists ? previouslySelected : ALL_TAGS);
            }

            applyTagFilter(tagSelect.getValue());

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to reload quizzes: " + e.getMessage());
        }
    }

    private void loadAllQuizzesWithTags() throws SQLException {
        allQuizzes.clear();

        List<Quiz> quizzes = quizDAO.findAll();

        for (Quiz q : quizzes) {
            List<Tag> tags = quizTagDAO.findTagsForQuiz(q.getId());

            ArrayList<String> tagNames = new ArrayList<>();
            for (Tag t : tags) {
                tagNames.add(t.getTagName());
            }

            q.setTags(tagNames);
            allQuizzes.add(q);
        }
    }

    private void rebuildTagOptions() throws SQLException {
        Tag previouslySelected = tagSelect.getValue();

        Set<String> usedTagNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Quiz quiz : allQuizzes) {
            List<String> tags = quiz.getTags();
            if (tags == null) {
                continue;
            }

            for (String tagName : tags) {
                if (tagName != null && !tagName.isBlank()) {
                    usedTagNames.add(tagName.trim());
                }
            }
        }

        tagItems.clear();
        tagItems.add(ALL_TAGS);

        for (Tag tag : tagDAO.findAll()) {
            String name = tag.getTagName();
            if (name != null && usedTagNames.contains(name.trim())) {
                tagItems.add(tag);
            }
        }

        if (previouslySelected == null) {
            tagSelect.getSelectionModel().select(ALL_TAGS);
        } else {
            boolean exists = tagItems.stream().anyMatch(t -> t.getId() == previouslySelected.getId());
            tagSelect.getSelectionModel().select(exists ? previouslySelected : ALL_TAGS);
        }
    }

    private void applyTagFilter(Tag selectedTag) {
        TreeItem<Quiz> root = quizTreeTableView.getRoot();
        if (root == null) {
            return;
        }

        root.getChildren().clear();

        for (Quiz quiz : allQuizzes) {
            if (selectedTag == null || selectedTag.getId() == -1) {
                root.getChildren().add(new TreeItem<>(quiz));
                continue;
            }

            List<String> tags = quiz.getTags();
            if (tags == null) {
                continue;
            }

            for (String tagName : tags) {
                if (tagName != null && tagName.equalsIgnoreCase(selectedTag.getTagName())) {
                    root.getChildren().add(new TreeItem<>(quiz));
                    break;
                }
            }
        }

        quizTreeTableView.refresh();
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