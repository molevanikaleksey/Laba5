package commands;
import domain.FileStatus;
import service.AttachmentManager;
import service.FilleManager;

public class FileLinksCommand implements Command{
    public AttachmentManager manager;
    public FilleManager filleManager;
    public FileLinksCommand(AttachmentManager attachmentManager, FilleManager filleManager){
        this.manager = new AttachmentManager();
        this.filleManager = new FilleManager();
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_links <file_id>");
        }

        long fileId = Long.parseLong(args[1]);

        var links = manager.getLinksByFileId(fileId);
        if (filleManager.getFileById(fileId).getStatus() == FileStatus.DELETED){
            throw new IllegalArgumentException("Такого файла больше не существует");
        }
        for (var link : links) {
            System.out.println(link.getTargetType() + " " + link.getTargetId());
        }

    }
    public String description(){
        return "показать связи файла";
    }
    public String name() {
        return "file_links <file_id>";
    }
}