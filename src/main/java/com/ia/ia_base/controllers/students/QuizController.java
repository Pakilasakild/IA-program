package com.ia.ia_base.controllers.students;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.QuizDAO;
import com.ia.ia_base.database.dao.QuizQuestionDAO;
import com.ia.ia_base.database.dao.QuizTagDAO;
import com.ia.ia_base.database.dao.TagDAO;
import com.ia.ia_base.models.Quiz;
import com.ia.ia_base.models.Tag;
import com.ia.ia_base.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class QuizController extends BaseController {

    @FXML private Button startQuizBTN;
    @FXML private ListView<Quiz> quizListView;
    @FXML private ComboBox<Tag> tagSelect;

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuizTagDAO quizTagDAO = new QuizTagDAO();
    private final TagDAO tagDAO = new TagDAO();
    private final QuizQuestionDAO quizQuestionDAO = new QuizQuestionDAO();

    private final ObservableList<Quiz> allQuizzes = FXCollections.observableArrayList();
    private final ObservableList<Quiz> filteredQuizzes = FXCollections.observableArrayList();
    private final ObservableList<Tag> allTags = FXCollections.observableArrayList();

    private final Map<Integer, Integer> questionCounts = new HashMap<>();

    private final Tag ALL_TAG = new Tag("All");
    {
        ALL_TAG.setId(-1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupQuizListView();

        // Single selection only
        quizListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setupTagComboBox();
        loadInitialData();

        startQuizBTN.setOnAction(e -> startSession());
    }

    private void setupQuizListView() {
        quizListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String name = item.getName() == null ? "" : item.getName();

                int count = questionCounts.getOrDefault(item.getId(), 0);
                String countText = " (" + count + " question" + (count == 1 ? "" : "s") + ")";

                List<String> tags = item.getTags();
                String tagText = (tags == null || tags.isEmpty())
                        ? ""
                        : " [" + String.join(", ", tags) + "]";

                setText(name + countText + tagText);
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
            allTags.clear();
            allTags.add(ALL_TAG);
            allTags.addAll(tagDAO.findAll());
            tagSelect.setItems(allTags);
            tagSelect.getSelectionModel().select(ALL_TAG);

            allQuizzes.clear();
            questionCounts.clear();

            List<Quiz> quizzes = quizDAO.findAll();

            for (Quiz q : quizzes) {
                List<Tag> quizTags = quizTagDAO.findTagsForQuiz(q.getId());
                ArrayList<String> tagNames = new ArrayList<>();
                for (Tag t : quizTags) {
                    tagNames.add(t.getTagName());
                }
                q.setTags(tagNames);

                int count = quizQuestionDAO.findQuestionsForQuiz(q.getId()).size();
                questionCounts.put(q.getId(), count);
            }

            allQuizzes.addAll(quizzes);

            filteredQuizzes.setAll(allQuizzes);
            quizListView.setItems(filteredQuizzes);

            quizListView.getSelectionModel().clearSelection();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void applyTagFilter(Tag selectedTag) {
        if (selectedTag == null || selectedTag.getId() == -1) {
            filteredQuizzes.setAll(allQuizzes);
            quizListView.getSelectionModel().clearSelection();
            return;
        }

        String tagName = selectedTag.getTagName();
        filteredQuizzes.setAll(
                allQuizzes.filtered(q ->
                        q.getTags() != null && q.getTags().contains(tagName)
                )
        );

        Quiz selected = quizListView.getSelectionModel().getSelectedItem();
        if (selected != null && !filteredQuizzes.contains(selected)) {
            quizListView.getSelectionModel().clearSelection();
        }
    }

    private void startSession() {
        Quiz quiz = quizListView.getSelectionModel().getSelectedItem();

        if (quiz == null) {
            AlertManager.showError("No quiz selected", "Please select a quiz.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/IA/Student/quizSession.fxml")
            );
            Parent root = loader.load();

            QuizSessionController controller = loader.getController();
            if (controller != null) {
                controller.setQuiz(quiz);
            }

            Stage sessionStage = new Stage();
            sessionStage.setTitle("Quiz Session");
            sessionStage.setScene(new Scene(root));
            sessionStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertManager.showError("Unable to open quiz session", "Could not open the quiz session window.");
        }
    }
}
