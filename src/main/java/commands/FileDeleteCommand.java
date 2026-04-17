package commands;

import service.FilleManager;
import service.AttachmentManager;
public class FileDeleteCommand implements Command {
    AttachmentManager attachmentManager;
    FilleManager filleManager;

    public FileDeleteCommand(AttachmentManager attachmentManager, FilleManager filleManager){
        this.attachmentManager = new AttachmentManager();
        this.filleManager = new FilleManager();
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_delete <file_id>");
        }

        long fileId = Long.parseLong(args[1]);
        filleManager.deleteFile(fileId);
        attachmentManager.setStatusDel(fileId);
        System.out.println("OK status=DELETED");
    }
    public String description(){
        return "пометить файл как DELETED";
    }
    public String name() {
        return "file_delete <file_id>";
    }
}