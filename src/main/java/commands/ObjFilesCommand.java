package commands;
import domain.AttachmentTargetType;
import repository.FileMetaRepository;
import service.FilleManager;
import domain.FileMeta;

import java.util.List;

public class ObjFilesCommand implements Command{
    FilleManager manager;
    FileMetaRepository fileMetaRepository;
    public ObjFilesCommand(FilleManager filleManager){
        this.manager = new FilleManager(fileMetaRepository);
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Ошибка: используйте obj_files <type> <id>");
        }

        AttachmentTargetType targetType = AttachmentTargetType.valueOf(args[1].toUpperCase());
        long targetId = Long.parseLong(args[2]);
        List<FileMeta> files = manager.getFilesByObject(targetType, targetId);
        if (files.isEmpty()){
            throw new ArrayStoreException("Массив файлов пустой");
        }

        for (FileMeta file : files) {
            System.out.println("File " + file.getId() + " " + file.getFileName());
        }

    }
    public String description(){
        return "показать файлы объекта";
    }
    public String name() {
        return "obj_files <type> <id>";
    }
}
