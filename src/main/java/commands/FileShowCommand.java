package commands;

import repository.FileMetaRepository;
import service.FilleManager;
import domain.FileMeta;

public class FileShowCommand implements Command {
    FilleManager manager;
    FileMetaRepository fileMetaRepository;
    public FileShowCommand(FilleManager filleManager){
        this.manager = new FilleManager(fileMetaRepository);
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_show <file_id>");
        }

        long fileId = Long.parseLong(args[1]);
        FileMeta file = manager.getFileById(fileId);
        try {
            System.out.println("File #" + file.getId());
            System.out.println("name: " + file.getFileName());
            System.out.println("mime: " + file.getMimeType());
            System.out.println("size: " + file.getSizeBytes());
            System.out.println("status: " + file.getStatus());
        }catch(Exception e){
            System.out.println("Такого файла ещё/уже нет");
        }
    }
    public String description(){
        return "показать файл по id";
    }
    public String name() {
        return "file_show <file_id>";
    }
}