package ui;

import domain.FileMeta;
import domain.FileStatus;
import service.AttachmentManager;
import javafx.application.Platform;
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
import db.DatabaseConfig;
import db.DatabaseConnectionFactory;
import repository.*;
import security.PasswordHash;
import service.AuthService;
import service.SessionService;

import java.io.File;

public class FileMasterDetailApp extends Application {
    private FilleManager fileManager;
    private AttachmentManager attachmentManager;

    private AuthService authService;
    private SessionService sessionService;

    private ProgressIndicator progressIndicator;
    private TableView<AttachmentLink> linkTable;
    private TableView<FileMeta> fileTable;
    private TextArea detailsArea;

    private Button updateButton;
    private Button deleteButton;
    private Button linkButton;
    private Button unlinkButton;
    private Label accountLabel;
    private Button logoutButton;
    private Stage currentStage;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        initServices();
        showLoginWindow(stage);
    }
    private void initServices() {
        DatabaseConfig config = new DatabaseConfig("db.properties");
        DatabaseConnectionFactory connectionFactory =
                new DatabaseConnectionFactory(config);

        UserRepository userRepository =
                new PostgresUserRepository(connectionFactory);

        FileMetaRepository fileMetaRepository =
                new PostgresFileMetaRepository(connectionFactory);

        AttachmentLinkRepository attachmentLinkRepository =
                new PostgresAttachmentLinkRepository(connectionFactory);

        fileManager = new FilleManager(fileMetaRepository);

        attachmentManager = new AttachmentManager(
                attachmentLinkRepository,
                fileManager
        );

        fileManager.loadFromDatabase();
        attachmentManager.loadFromDatabase();

        PasswordHash passwordHash = new PasswordHash();
        sessionService = new SessionService();

        authService = new AuthService(
                userRepository,
                passwordHash,
                sessionService
        );
    }
    private void showLoginWindow(Stage stage) {
        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label messageLabel = new Label();

        loginButton.setOnAction(event -> {
            try {
                authService.login(
                        loginField.getText(),
                        passwordField.getText()
                );

                showMainWindow(stage);

            } catch (Exception e) {
                messageLabel.setText(e.getMessage());
            }
        });

        registerButton.setOnAction(event -> {
            try {
                authService.register(
                        loginField.getText(),
                        passwordField.getText()
                );

                messageLabel.setText("OK registered");

            } catch (Exception e) {
                messageLabel.setText(e.getMessage());
            }
        });

        VBox root = new VBox(
                10,
                new Label("Authorization"),
                loginField,
                passwordField,
                loginButton,
                registerButton,
                messageLabel
        );

        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 300, 250);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public void showMainWindow(Stage stage) {
        fileTable = createFileTable();
        detailsArea = createDetailsArea();
        linkTable = createLinkTable();
        this.currentStage = stage;
        fileTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldFile, newFile) -> {
                    if (newFile != null) {
                        linkTable.getSelectionModel().clearSelection();
                        showFileDetails(newFile);
                        updateButtonsAccess(newFile);
                    } else {
                        detailsArea.clear();
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                        linkButton.setDisable(true);
                    }
                });

        linkTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldLink, newLink) -> {
                    if (newLink != null) {
                        fileTable.getSelectionModel().clearSelection();
                        showLinkDetails(newLink);
                        updateButtonsAccessForLink(newLink);
                    } else {
                        unlinkButton.setDisable(true);
                    }
                });
/*
        linkTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldLink, newLink) -> {
                    if (newLink != null) {
                        fileTable.getSelectionModel().clearSelection();
                        showLinkDetails(newLink);
                    }
                });

 */
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(30, 30);
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        HBox root = new HBox(12, leftPanel, rightPanel);
        root.setPadding(new Insets(12));

        refreshAll();

        Scene scene = new Scene(root, 950, 550);

        stage.setTitle("Files / Attachments — Master Detail");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        accountLabel = new Label(
                "Account: " + sessionService.getCurrentUser().getLogin()
        );

        logoutButton = new Button("Logout");

        logoutButton.setOnAction(event -> {
            sessionService.logout();
            showLoginWindow(currentStage);
        });

        HBox accountBox = new HBox(10, accountLabel, logoutButton);
        Label filesTitle = new Label("Files");
        Label linksTitle = new Label("Attachment links");

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshAll());

        Button addButton = new Button("Add file");
        addButton.setOnAction(event -> addFile());

        //Button updateButton = new Button("Update description");
        updateButton = new Button("Update description");
        updateButton.setOnAction(event -> updateSelectedDescription());

        //Button deleteButton = new Button("Delete file");
        deleteButton = new Button("Delete file");
        deleteButton.setOnAction(event -> deleteSelectedFile());

        //Button linkButton = new Button("Link");
        linkButton = new Button("Link");
        linkButton.setOnAction(event -> linkSelectedFile());

        //Button unlinkButton = new Button("Unlink");
        unlinkButton = new Button("Unlink");
        unlinkButton.setOnAction(event -> unlinkSelectedLink());
        /*
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveData());

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> loadData());

         */


        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        linkButton.setDisable(true);
        unlinkButton.setDisable(true);


        HBox fileButtons = new HBox(8, refreshButton, addButton, updateButton, deleteButton);
        HBox linkButtons = new HBox(8, linkButton, unlinkButton);
        //HBox storageButtons = new HBox(8, saveButton, loadButton);

        VBox panel = new VBox(
                10,
                accountBox,
                filesTitle,
                fileTable,
                fileButtons,
                linksTitle,
                linkTable,
                linkButtons
        );

        panel.setPrefWidth(650);

        return panel;
    }
    private void linkSelectedFile() {
        FileMeta selectedFile = getSelectedFile();

        if (selectedFile == null) {

            showError("Ошибка: выберите файл, который хотите прикрепить");

            fileTable.refresh();

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

                long ownerId = sessionService.getCurrentUser().getUserId();


                attachmentManager.linkFile(
                        selectedFile.getId(),
                        input.targetType(),
                        targetId,
                        ownerId
                );

                refreshAll();

                showInfo("Связь добавлена");

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
            long currentUserId = sessionService.getCurrentUser().getUserId();

            attachmentManager.unlinkFile(
                    selectedLink.getFileId(),
                    selectedLink.getTargetType(),
                    selectedLink.getTargetId(),
                    currentUserId
            );

            refreshAll();

            showInfo("Связь удалена");

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

                        "ownerId: " + link.getOwnerId() + "\n" +

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

        TableColumn<FileMeta, Long> ownerColumn = new TableColumn<>("Owner ID");
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        ownerColumn.setPrefWidth(90);

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(mimeColumn);
        table.getColumns().add(sizeColumn);
        table.getColumns().add(statusColumn);
        table.getColumns().add(ownerColumn);

        return table;
    }

    private TextArea createDetailsArea() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefHeight(470);

        return area;
    }

    private void refreshFilesJson() {
        fileTable.setItems(
                FXCollections.observableArrayList(fileManager.getAllFiles_noerrors())
        );
    }
    private void refreshLinksJson() {
        linkTable.setItems(
                FXCollections.observableArrayList(attachmentManager.getAllLinksForSave())
        );
    }
    private void refreshAll() {
        try {
            fileManager.loadFromDatabase();
            attachmentManager.loadFromDatabase();

            fileTable.setItems(
                    FXCollections.observableArrayList(fileManager.getAllFiles_noerrors())
            );

            linkTable.setItems(
                    FXCollections.observableArrayList(attachmentManager.getAllLinksForSave())
            );

            fileTable.refresh();
            linkTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
        }
    }
    private void refreshFiles() {
        fileManager.loadFromDatabase();

        fileTable.setItems(
                FXCollections.observableArrayList(fileManager.getAllFiles_noerrors())
        );
    }
    private void refreshLinks() {
        attachmentManager.loadFromDatabase();

        linkTable.setItems(
                FXCollections.observableArrayList(attachmentManager.getAllLinksForSave())
        );
    }
    private void refreshAllJson() {
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

        String text =
                "File #" + file.getId() + "\n\n" +
                        "name: " + file.getFileName() + "\n" +
                        "mime: " + file.getMimeType() + "\n" +
                        "size: " + file.getSizeBytes() + "\n" +
                        "description: " + file.getDescription() + "\n" +
                        "status: " + file.getStatus() + "\n" +
                        "ownerId: " + file.getOwnerId() + "\n" +
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

        TableColumn<AttachmentLink, Long> ownerColumn = new TableColumn<>("Owner ID");
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        ownerColumn.setPrefWidth(90);

        table.getColumns().add(idColumn);
        table.getColumns().add(fileIdColumn);
        table.getColumns().add(targetTypeColumn);
        table.getColumns().add(targetIdColumn);
        table.getColumns().add(ownerColumn);

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

                long ownerId = sessionService.getCurrentUser().getUserId();
                System.out.println(123);
                fileManager.addFile(
                        input.fileName(),
                        input.mimeType(),
                        size,
                        input.description(),
                        ownerId
                );

                refreshAll();

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
                long currentUserId = sessionService.getCurrentUser().getUserId();

                fileManager.updateFileDescription(
                        selected.getId(),
                        newDescription,
                        currentUserId
                );


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
            long currentUserId = sessionService.getCurrentUser().getUserId();

            fileManager.deleteFile(
                    selected.getId(),
                    currentUserId
            );

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
                //storage.save(file.getAbsolutePath(), fileManager, attachmentManager);

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
                //storage.load(file.getAbsolutePath(), fileManager, attachmentManager);

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
    private void updateButtonsAccess(FileMeta file) {
        long currentUserId = sessionService.getCurrentUser().getUserId();

        boolean isOwner = file.getOwnerId() == currentUserId;

        updateButton.setDisable(!isOwner);
        deleteButton.setDisable(!isOwner);
        linkButton.setDisable(!isOwner);
        unlinkButton.setDisable(true);
    }
    private void updateButtonsAccessForLink(AttachmentLink link) {
        long currentUserId = sessionService.getCurrentUser().getUserId();

        boolean isOwner = link.getOwnerId() == currentUserId;

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        linkButton.setDisable(true);
        unlinkButton.setDisable(!isOwner);
    }

    private record FileInput(
            String fileName,
            String mimeType,
            String sizeBytes,
            String description
    ) {
    }
}