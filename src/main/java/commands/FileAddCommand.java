package commands;
import service.FilleManager;
import service.Generator;

import java.util.Scanner;

public class FileAddCommand implements Command{
    FilleManager manager;
    Scanner scanner;
    String args;

    public FileAddCommand(FilleManager filleManager, Scanner scanner){
        this.manager = new FilleManager();
        this.scanner = new Scanner(System.in);
    }
    @Override
    public void execute (String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Ошибка: команда file_add не принимает аргументы");
        }

        System.out.print("Путь к файлу: ");
        String path = scanner.nextLine().trim();

        System.out.print("Описание (можно пусто): ");
        String description = scanner.nextLine().trim();

        System.out.print("Имя файла: ");
        String name = scanner.nextLine().trim();

        System.out.print("Введите тип файла ");
        String MimeType = scanner.nextLine().trim();

        System.out.print("Введите размер файла ");
        Long size = Long.parseLong((scanner.nextLine().trim()));

        System.out.print("Введите имя пользователя ");
        String owner = scanner.nextLine().trim();

        Long id = Generator.getFileNextId();

        manager.addFileMeta(description, path, id);
        manager.setFilefields(id, description, name, owner, MimeType, size);
        System.out.println("OK file_id=" + id);
    }
    public String description(){
        return "Добавляет файл в память";
    }
    public String name() {
        return "file_add";
    }
}