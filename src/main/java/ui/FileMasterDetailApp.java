package ui;

import domain.FileMeta;
import domain.FileStatus;
import service.AttachmentManager;
import javafx.application.Platform;
import persistence.FileStorage;
import service.AttachmentManager;
import service.FilleManager;
import domain.AttachmentLink;
import domain.AttachmentTargetType;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.Generator;

import java.io.File;

public class FileMasterDetailApp extends Application {

    private final FilleManager fileManager = new FilleManager();
    private final AttachmentManager attachmentManager = new AttachmentManager();
    private final FileStorage storage = new FileStorage();
    private ProgressIndicator progressIndicator;
    private TableView<AttachmentLink> linkTable;

    private TableView<FileMeta> fileTable;
    private TextArea detailsArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        fileTable = createFileTable();
        detailsArea = createDetailsArea();
        linkTable = createLinkTable();

        fileTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldFile, newFile) -> {
                    if (newFile != null) {
                        linkTable.getSelectionModel().clearSelection();
                        showFileDetails(newFile);
                    }
                });

        linkTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldLink, newLink) -> {
                    if (newLink != null) {
                        fileTable.getSelectionModel().clearSelection();
                        showLinkDetails(newLink);
                    }
                });
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(30, 30);
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        HBox root = new HBox(12, leftPanel, rightPanel);
        root.setPadding(new Insets(12));

        refreshFiles();

        Scene scene = new Scene(root, 950, 550);

        stage.setTitle("Files / Attachments — Master Detail");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        Label filesTitle = new Label("Files");
        Label linksTitle = new Label("Attachment links");

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshAll());

        Button addButton = new Button("Add file");
        addButton.setOnAction(event -> addFile());

        Button updateButton = new Button("Update description");
        updateButton.setOnAction(event -> updateSelectedDescription());

        Button deleteButton = new Button("Delete file");
        deleteButton.setOnAction(event -> deleteSelectedFile());

        Button linkButton = new Button("Link");
        linkButton.setOnAction(event -> linkSelectedFile());

        Button unlinkButton = new Button("Unlink");
        unlinkButton.setOnAction(event -> unlinkSelectedLink());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveData());

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> loadData());

        HBox fileButtons = new HBox(8, refreshButton, addButton, updateButton, deleteButton);
        HBox linkButtons = new HBox(8, linkButton, unlinkButton);
        HBox storageButtons = new HBox(8, saveButton, loadButton);

        VBox panel = new VBox(
                10,
                filesTitle,
                fileTable,
                fileButtons,
                linksTitle,
                linkTable,
                linkButtons,
                storageButtons
        );

        panel.setPrefWidth(650);

        return panel;
    }
    private void linkSelectedFile() {
        FileMeta selectedFile = getSelectedFile();

        if (selectedFile == null) {
            showError("Ошибка: выберите файл, который хотите прикрепить");
            return;
        }

        Dialog<LinkInput> dialog = new Dialog<>();
        dialog.setTitle("Link file");
        dialog.setHeaderText("Прикрепить файл #" + selectedFile.getId());

        ButtonType linkButtonType = new ButtonType("Link", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(linkButtonType, ButtonType.CANCEL);

        ComboBox<AttachmentTargetType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(AttachmentTargetType.SAMPLE, AttachmentTargetType.MEASUREMENT, AttachmentTargetType.REPORT);
        typeBox.setValue(AttachmentTargetType.SAMPLE);

        TextField targetIdField = new TextField();
        targetIdField.setPromptText("Например: 12");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Target type:"), 0, 0);
        grid.add(typeBox, 1, 0);

        grid.add(new Label("Target ID:"), 0, 1);
        grid.add(targetIdField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == linkButtonType) {
                return new LinkInput(typeBox.getValue(), targetIdField.getText());
            }

            return null;
        });

        dialog.showAndWait().ifPresent(input -> {
            try {
                long targetId = Long.parseLong(input.targetId());

                attachmentManager.linkFile(
                        selectedFile.getId(),
                        input.targetType(),
                        targetId
                );

                showInfo("Связь добавлена. Нажмите Refresh, чтобы обновить таблицу ссылок.");

            } catch (NumberFormatException e) {
                showError("Ошибка: ID объекта должен быть числом");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        });
    }
    private void unlinkSelectedLink() {
        AttachmentLink selectedLink = linkTable.getSelectionModel().getSelectedItem();

        if (selectedLink == null) {
            showError("Ошибка: выберите связь, которую хотите удалить");
            return;
        }

        try {
            attachmentManager.unlinkFile(
                    selectedLink.getFileId(),
                    selectedLink.getTargetType(),
                    selectedLink.getTargetId()
            );

            showInfo("Связь удалена. Нажмите Refresh, чтобы обновить таблицу ссылок.");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }
    private void showLinkDetails(AttachmentLink link) {
        if (link == null) {
            detailsArea.clear();
            return;
        }

        String text =

                "AttachmentLink #" + link.getId() + "\n\n" +

                        "fileId: " + link.getFileId() + "\n" +

                        "targetType: " + link.getTargetType() + "\n" +

                        "targetId: " + link.getTargetId() + "\n" +

                        "owner: " + link.getOwnerUsername() + "\n" +

                        "createdAt: " + link.getCreatedAt();

        detailsArea.setText(text);

    }
    private record LinkInput(
            AttachmentTargetType targetType,
            String targetId
    ) {
    }

    private VBox createRightPanel() {
        Label title = new Label("Details");

        VBox panel = new VBox(10, title, detailsArea);
        panel.setPrefWidth(320);

        return panel;
    }

    private TableView<FileMeta> createFileTable() {
        TableView<FileMeta> table = new TableView<>();

        TableColumn<FileMeta, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);

        TableColumn<FileMeta, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        nameColumn.setPrefWidth(180);

        TableColumn<FileMeta, String> mimeColumn = new TableColumn<>("MIME");
        mimeColumn.setCellValueFactory(new PropertyValueFactory<>("mimeType"));
        mimeColumn.setPrefWidth(130);

        TableColumn<FileMeta, Long> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("sizeBytes"));
        sizeColumn.setPrefWidth(100);

        TableColumn<FileMeta, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(mimeColumn);
        table.getColumns().add(sizeColumn);
        table.getColumns().add(statusColumn);

        return table;
    }

    private TextArea createDetailsArea() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefHeight(470);

        return area;
    }

    private void refreshFiles() {
        fileTable.setItems(
                FXCollections.observableArrayList(fileManager.getAllFiles_noerrors())
        );
    }
    private void refreshLinks() {
        linkTable.setItems(
                FXCollections.observableArrayList(attachmentManager.getAllLinksForSave())
        );
    }
    private void refreshAll() {
        fileTable.setItems(
                FXCollections.observableArrayList(fileManager.getAllFiles_noerrors())
        );
        linkTable.setItems(
                FXCollections.observableArrayList(attachmentManager.getAllLinksForSave())
        );
    }

    private void showFileDetails(FileMeta file) {
        if (file == null) {
            detailsArea.clear();
            return;
        }
        if (file.getStatus()== FileStatus.DELETED){
            detailsArea.clear();
            fileTable.setItems(FXCollections.observableArrayList(fileManager.getAllFiles_noerrors()));
            return;
        }

        String text =
                "File #" + file.getId() + "\n\n" +
                        "name: " + file.getFileName() + "\n" +
                        "mime: " + file.getMimeType() + "\n" +
                        "size: " + file.getSizeBytes() + "\n" +
                        "description: " + file.getDescription() + "\n" +
                        "status: " + file.getStatus() + "\n" +
                        "owner: " + file.getOwnerUsername() + "\n" +
                        "createdAt: " + file.getCreatedAt() + "\n" +
                        "updatedAt: " + file.getUpdatedAt();

        detailsArea.setText(text);
    }
    private TableView<AttachmentLink> createLinkTable() {
        TableView<AttachmentLink> table = new TableView<>();

        TableColumn<AttachmentLink, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);

        TableColumn<AttachmentLink, Long> fileIdColumn = new TableColumn<>("File ID");
        fileIdColumn.setCellValueFactory(new PropertyValueFactory<>("fileId"));
        fileIdColumn.setPrefWidth(90);

        TableColumn<AttachmentLink, String> targetTypeColumn = new TableColumn<>("Target type");
        targetTypeColumn.setCellValueFactory(new PropertyValueFactory<>("targetType"));
        targetTypeColumn.setPrefWidth(120);

        TableColumn<AttachmentLink, Long> targetIdColumn = new TableColumn<>("Target ID");
        targetIdColumn.setCellValueFactory(new PropertyValueFactory<>("targetId"));
        targetIdColumn.setPrefWidth(90);

        table.getColumns().add(idColumn);
        table.getColumns().add(fileIdColumn);
        table.getColumns().add(targetTypeColumn);
        table.getColumns().add(targetIdColumn);

        return table;
    }
    private void addFile() {
        Dialog<FileInput> dialog = new Dialog<>();
        dialog.setTitle("Add file");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField fileNameField = new TextField();
        fileNameField.setPromptText("photo1.png");

        TextField mimeField = new TextField();
        mimeField.setPromptText("image/png");

        TextField sizeField = new TextField();
        sizeField.setPromptText("210034");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        grid.add(new Label("File name:"), 0, 0);
        grid.add(fileNameField, 1, 0);

        grid.add(new Label("MIME:"), 0, 1);
        grid.add(mimeField, 1, 1);

        grid.add(new Label("Size bytes:"), 0, 2);
        grid.add(sizeField, 1, 2);

        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == addButtonType) {
                return new FileInput(
                        fileNameField.getText(),
                        mimeField.getText(),
                        sizeField.getText(),
                        descriptionArea.getText()
                );
            }

            return null;
        });

        dialog.showAndWait().ifPresent(input -> {
            try {
                long size = Long.parseLong(input.sizeBytes());

                fileManager.addFile(
                        input.fileName(),
                        input.mimeType(),
                        size,
                        input.description()
                );

                showInfo("Файл добавлен");
            } catch (NumberFormatException e) {
                showError("Ошибка: размер файла должен быть числом");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        });
    }

    private void updateSelectedDescription() {
        FileMeta selected = getSelectedFile();

        if (selected == null) {
            showError("Ошибка: выберите файл");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getDescription());
        dialog.setTitle("Update description");
        dialog.setHeaderText("Изменение описания файла #" + selected.getId());
        dialog.setContentText("Новое описание:");

        dialog.showAndWait().ifPresent(newDescription -> {
            try {
                fileManager.updateFileDescription(selected.getId(), newDescription);


                FileMeta updated = fileManager.getFileById(selected.getId());
                showFileDetails(updated);

                showInfo("Описание обновлено");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        });
    }

    private void deleteSelectedFile() {
        FileMeta selected = getSelectedFile();

        if (selected == null) {
            showError("Ошибка: выберите файл");
            return;
        }

        try {
            fileManager.deleteFile(selected.getId());

            detailsArea.clear();
            showInfo("Файл удалён");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void saveData() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save JSON");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );

        File file = chooser.showSaveDialog(fileTable.getScene().getWindow());

        if (file == null) {
            return;
        }

        progressIndicator.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                storage.save(file.getAbsolutePath(), fileManager, attachmentManager);

                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    showInfo("Данные сохранены");
                });

            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    showError(e.getMessage());
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void loadData() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load JSON");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );

        File file = chooser.showOpenDialog(fileTable.getScene().getWindow());
        Generator.file_id++;

        if (file == null) {
            return;
        }

        progressIndicator.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                storage.load(file.getAbsolutePath(), fileManager, attachmentManager);

                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    showInfo("Данные загружены. Нажмите Refresh, чтобы обновить таблицу.");
                });

            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    showError(e.getMessage());
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private FileMeta getSelectedFile() {
        return fileTable.getSelectionModel().getSelectedItem();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record FileInput(
            String fileName,
            String mimeType,
            String sizeBytes,
            String description
    ) {
    }
}