package commands;
import service.FilleManager;

import java.util.Scanner;

public class FileUpdateCommand implements Command{
    FilleManager manager;
    Scanner scanner;
    public FileUpdateCommand(FilleManager filleManager, Scanner scanner){
        this.manager = new FilleManager();
        this.scanner = new Scanner(System.in);
    }
    @Override
    public void execute(String[] args) {
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

        manager.updateFileDescription(fileId, newDescription);
        System.out.println("OK");
    }
    public String description(){
        return "обновить описание файла";
    }
    public String name(){return "file_update <file_id>";}
}