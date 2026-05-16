package commands;
import repository.FileMetaRepository;
import service.FilleManager;
import domain.FileMeta;

public class FileExportMetaCommand implements Command{
    FilleManager manager;
    FileMetaRepository fileMetaRepository;
    public FileExportMetaCommand(FilleManager filleManager){
        this.manager = new FilleManager(fileMetaRepository);
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_export_meta <file_id>");
        }

        long fileId = Long.parseLong(args[1]);
        FileMeta file = manager.getFileById(fileId);

        System.out.println("File meta exported (text)");
        System.out.println("id: " + fileId);
        System.out.println("name: " + file.getFileName());
        System.out.println("mime: " + file.getMimeType());
        System.out.println("size: " + file.getSizeBytes());
        System.out.println("description: " + file.getDescription());
        System.out.println("status: " + file.getStatus());
    }
    public String description(){
        return "вывести метаданные файла";
    }
    public String name() {
        return "file_export_meta <file_id>";
    }
}