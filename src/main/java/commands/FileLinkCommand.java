package commands;
import domain.AttachmentTargetType;
import repository.AttachmentLinkRepository;
import repository.FileMetaRepository;
import service.AttachmentManager;
import service.FilleManager;
import service.SessionService;

import java.util.Scanner;

public class FileLinkCommand implements Command{
    AttachmentManager manager;
    Scanner scanner;
    FilleManager filleManager;
    SessionService sessionService;
    FileMetaRepository fileMetaRepository;
    AttachmentLinkRepository attachmentLinkRepository;

    //TreeMap<Long, FileMeta> files = filleManager.getTreeMap();
    public FileLinkCommand(AttachmentManager manager, Scanner scanner, FilleManager filleManager, SessionService sessionService){
        this.manager = new AttachmentManager(attachmentLinkRepository, filleManager);
        this.scanner = new Scanner(System.in);
        this.filleManager = new FilleManager(fileMetaRepository);
        this.sessionService = sessionService;

    }

    @Override
    public void execute(String[] args) {
        if (!sessionService.isAuthorized()) {
            throw new IllegalStateException("Ошибка: сначала выполните login");
        }
        if (args.length != 2) {
            throw new IllegalArgumentException("Ошибка: используйте file_link <file_id>");
        }

        long fileId;
        try {
            fileId = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка: file_id должен быть числом");
        }

        System.out.print("К чему прикрепить (SAMPLE|MEASUREMENT|REPORT): ");
        String typeInput = scanner.nextLine().trim().toUpperCase();

        AttachmentTargetType targetType;
        try {
            targetType = AttachmentTargetType.valueOf(typeInput);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Ошибка: тип должен быть SAMPLE, MEASUREMENT или REPORT"
            );
        }

        System.out.print("ID объекта: ");
        String targetIdInput = scanner.nextLine().trim();

        long targetId;
        try {
            targetId = Long.parseLong(targetIdInput);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка: id объекта должен быть числом");
        }

        if (!filleManager.getAllFiles_noerrors().contains(filleManager.getFileById(fileId))) {
            throw new IllegalArgumentException("Файла с таким id ещё/уже не существует");
        }



        manager.linkFile(fileId, targetType, targetId, sessionService.getCurrentUserId());
        System.out.println("OK linked");
    }
    public String description(){
        return "прикрепить файл к объекту";
    }
    public String name() {
        return "file_link <file_id>";
    }
}