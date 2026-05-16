package commands;
import domain.AttachmentTargetType;
import service.AttachmentManager;
import service.SessionService;

public class FileUnlinkCommand implements Command {
    AttachmentManager manager;
    SessionService sessionService;
    public FileUnlinkCommand(AttachmentManager manager, SessionService sessionService){
        this.manager = new AttachmentManager();
        this.sessionService = sessionService;
    }
    @Override
    public void execute(String[] args) {
        if (!sessionService.isAuthorized()) {
            throw new IllegalStateException("Ошибка: сначала выполните login");
        }
        try {
            if (args.length != 4) {
                throw new IllegalArgumentException("Ошибка: используйте file_unlink <file_id> <type> <id>");
            } else {
                long fileId = Long.parseLong(args[1]);
                AttachmentTargetType targetType = AttachmentTargetType.valueOf(args[2].toUpperCase());
                long targetId = Long.parseLong(args[3]);

                manager.unlinkFile(fileId, targetType, targetId);
                System.out.println("OK unlinked");
            }
        }catch (IllegalArgumentException e){
            System.out.println("Ошибка: файл не добавлен");
            System.out.println(e.getMessage());
        }
    }
    public String description(){
        return "удалить связь файла с объектом";
    }
    public String name() {
        return "file_unlink <file_id> <type> <id>";
    }
}
