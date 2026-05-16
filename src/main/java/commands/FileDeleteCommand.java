package commands;

import repository.FileMetaRepository;
import service.FilleManager;
import service.AttachmentManager;
import service.SessionService;

public class FileDeleteCommand implements Command {
    AttachmentManager attachmentManager;
    FilleManager filleManager;
    SessionService sessionService;
    FileMetaRepository fileMetaRepository;
    public FileDeleteCommand(AttachmentManager attachmentManager, FilleManager filleManager, SessionService sessionService){
        this.attachmentManager = new AttachmentManager();
        this.filleManager = new FilleManager(fileMetaRepository);
        this.sessionService = sessionService;
    }
    @Override
    public void execute(String[] args) {
        if (!sessionService.isAuthorized()) {
            throw new IllegalStateException("Ошибка: сначала выполните login");
        }
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_delete <file_id>");
        }

        long fileId = Long.parseLong(args[1]);
        filleManager.deleteFile(fileId, sessionService.getCurrentUserId());
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