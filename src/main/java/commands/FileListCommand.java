package commands;
import repository.FileMetaRepository;
import service.FilleManager;
import domain.FileMeta;

import java.util.List;

public class FileListCommand implements Command{
    FilleManager filleManager;
    FileMetaRepository fileMetaRepository;
    public FileListCommand(FilleManager filleManager){
        this.filleManager = new FilleManager(fileMetaRepository);
    }
    @Override
    public void execute(String[] args) {
        List<FileMeta> files;

        if (filleManager.files.isEmpty()){
            throw new IllegalArgumentException("Вы не добавили файлов в коллекцию, или все файлы удалены");
        }
        else {
            if (args.length == 1) {
                files = filleManager.getAllFiles();
            } else if (args.length == 3 && args[1].equals("--last")) {
                int n = Integer.parseInt(args[2]);
                files = filleManager.getLastFiles(n);
            } else {
                throw new IllegalArgumentException("Ошибка: используйте file_list [--last N]");
            }
        }

        System.out.printf("%-5s %-20s %-15s %-10s %-30s%n",
                "ID", "Name", "Type", "Size", "Description");

        for (FileMeta file : files) {
            System.out.printf("%-5d %-20s %-15s %-10d %-30s%n",
                    file.getId(),
                    file.getFileName(),
                    file.getMimeType(),
                    file.getSizeBytes(),
                    file.getDescription());
        }
    }
    public String description(){
        return "показать список файлов(--last n покажет последние n файлов)";
    }
    public String name() {
        return "file_list";
    }
}