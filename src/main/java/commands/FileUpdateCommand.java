package commands;
import repository.FileMetaRepository;
import service.FilleManager;
import service.SessionService;

import java.util.Scanner;

public class FileUpdateCommand implements Command{
    FilleManager manager;
    Scanner scanner;
    SessionService sessionService;
    FileMetaRepository fileMetaRepository;
    public FileUpdateCommand(FilleManager filleManager, Scanner scanner, SessionService sessionService){
        this.manager = new FilleManager(fileMetaRepository);
        this.scanner = new Scanner(System.in);
        this.sessionService = sessionService;
    }
    @Override
    public void execute(String[] args) {
        if (!sessionService.isAuthorized()) {
            throw new IllegalStateException("Ошибка: сначала выполните login");
        }
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_update <file_id>");
        }

        long fileId;
        try {
            fileId = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка: file_id должен быть числом");
        }

        System.out.print("Впишите новое описание: ");
        String newDescription = scanner.nextLine().trim();

        manager.updateFileDescription(fileId, newDescription, SessionService.getCurrentUserId());
        System.out.println("OK");
    }
    public String description(){
        return "обновить описание файла";
    }
    public String name(){return "file_update <file_id>";}
}