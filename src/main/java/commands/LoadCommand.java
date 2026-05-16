package commands;

import persistence.FileStorage;
import service.AttachmentManager;
import service.FilleManager;

public class LoadCommand implements Command {
    private final FilleManager fileManager;
    private final AttachmentManager attachmentManager;
    private final FileStorage storage;

    public LoadCommand(FilleManager fileManager, AttachmentManager attachmentManager, FileStorage storage) {
        this.fileManager = fileManager;
        this.attachmentManager = attachmentManager;
        this.storage = storage;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте load <path>");
        }

        storage.load(args[1], fileManager, attachmentManager);
        System.out.println("OK loaded");
    }

    @Override
    public String name() {
        return "load";
    }

    @Override
    public String description() {
        return "Загружает данные из JSON-файла";
    }
}