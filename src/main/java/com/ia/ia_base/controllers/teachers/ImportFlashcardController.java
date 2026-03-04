package com.ia.ia_base.controllers.teachers;

import com.ia.ia_base.controllers.BaseController;
import com.ia.ia_base.database.dao.FlashcardDAO;
import com.ia.ia_base.models.Flashcard;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.FlashcardReloadBus;
import com.ia.ia_base.util.InformationReloadBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImportFlashcardController extends BaseController {

    @FXML public Button selectFileBTN;
    @FXML public Button uploadBTN;
    @FXML public Button cancelImportBTN;

    @FXML public TableView<Flashcard> fileContentsTableView;
    @FXML public TableColumn<Flashcard, String> column1; // question
    @FXML public TableColumn<Flashcard, String> column2; // answer

    private final ObservableList<Flashcard> previewList = FXCollections.observableArrayList();
    private File selectedFile;
    private FlashcardDAO flashcardDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        flashcardDAO = new FlashcardDAO();
        column1.setText("Question");
        column2.setText("Answer");
        column1.setCellValueFactory(new PropertyValueFactory<>("question"));
        column2.setCellValueFactory(new PropertyValueFactory<>("answer"));

        fileContentsTableView.setItems(previewList);

        uploadBTN.setDisable(true);

        selectFileBTN.setOnAction(e -> onSelectFile());
        uploadBTN.setOnAction(e -> onUpload());
        cancelImportBTN.setOnAction(e -> closeWindow());
    }

    private void onSelectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Flashcards CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv")
        );

        File file = chooser.showOpenDialog(selectFileBTN.getScene().getWindow());
        if (file == null) return;

        selectedFile = file;

        try {
            List<Flashcard> loaded = readCsvToFlashcards(file);

            previewList.setAll(loaded);
            uploadBTN.setDisable(previewList.isEmpty());

            if (previewList.isEmpty()) {
                AlertManager.showError("No data", "The CSV file had no valid rows.");
            } else {
                AlertManager.showInfo("Loaded", "Loaded " + previewList.size() + " flashcards from CSV.");
            }

        } catch (IOException ex) {
            AlertManager.showError("File Error", "Failed to read file: " + ex.getMessage());
        }
    }

    private void onUpload() {
        if (selectedFile == null || previewList.isEmpty()) {
            AlertManager.showError("Nothing to upload", "Please select a CSV file with flashcards first.");
            return;
        }

        int inserted = 0;
        int skipped = 0;

        try {
            for (Flashcard fc : previewList) {
                fc.setActive(false);

                flashcardDAO.create(fc);
                inserted++;
            }

            AlertManager.showInfo("Import complete",
                    "Inserted: " + inserted + (skipped > 0 ? ("\nSkipped: " + skipped) : ""));

            FlashcardReloadBus.requestReload();
            InformationReloadBus.requestReload();

            closeWindow();

        } catch (SQLException ex) {
            AlertManager.showError("Database Error", "Import failed: " + ex.getMessage());
        }
    }

    /**
     *
     * CSV format expected:
     * question,answer
     * Q1,A1
     * Q2,A2
     *
     */
    private List<Flashcard> readCsvToFlashcards(File file) throws IOException {
        List<Flashcard> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                // Skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                List<String> cols = parseCsvLine(line);
                if (cols.size() < 2) continue;

                String q = cols.get(0).trim();
                String a = cols.get(1).trim();

                if (q.isEmpty() || a.isEmpty()) continue;

                out.add(new Flashcard(q, a));
            }
        }

        return out;
    }

    // Handles quotes and commas safely
    private List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cols.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        cols.add(sb.toString());
        return cols;
    }
}
