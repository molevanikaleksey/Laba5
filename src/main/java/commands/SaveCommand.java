package commands;

import persistence.FileStorage;
import service.AttachmentManager;
import service.FilleManager;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;

public class SaveCommand implements Command {
    private final FilleManager fileManager;
    private final AttachmentManager attachmentManager;
    private final FileStorage storage;

    public SaveCommand(FilleManager fileManager, AttachmentManager attachmentManager, FileStorage storage) {
        this.fileManager = fileManager;
        this.attachmentManager = attachmentManager;
        this.storage = storage;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте save <path>");
        }

        storage.save(args[1], fileManager, attachmentManager);
        System.out.println("OK saved");
    }

    @Override
    public String name() {
        return "save";
    }

    @Override
    public String description() {
        return "Сохраняет данные в JSON-файл";
    }
}