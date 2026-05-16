package persistence;

import domain.AttachmentLink;
import domain.FileMeta;

import java.util.HashSet;
import java.util.Set;

public class FileValidator {
    public static void validate(DataSnapShot snapShot){
        if(snapShot == null){
            throw new IllegalArgumentException("Файл пустой");
        }
        if (snapShot.getFiles() == null){
            throw new IllegalArgumentException("Вы не добавили ни одного файла в коллекцию/все файлы удалены");
        }
        if (snapShot.getLinks() == null) {
            throw new IllegalArgumentException("Ошибка загрузки: отсутствует список links");
        }
        Set<Long> fileIds = new HashSet<>();

        for(FileMeta file: snapShot.getFiles()){
            if (file == null){
                throw new IllegalArgumentException("Ошибка: Найден пустой файл");
            }
            if (fileIds.contains(file.getId())) {
                throw new IllegalArgumentException("Ошибка загрузки: повторяется file id=" + file.getId());
            }
            fileIds.add(file.getId());
            if (file.getFileName() == null || file.getFileName().isBlank()) {
                throw new IllegalArgumentException("Ошибка загрузки: fileName пустой у file id=" + file.getId());
            }
            if (file.getSizeBytes() < 0) {
                throw new IllegalArgumentException("Ошибка загрузки: sizeBytes меньше 0 у file id=" + file.getId());
            }
            if (file.getDescription() != null && file.getDescription().length() > 256) {
                throw new IllegalArgumentException("Ошибка загрузки: description слишком длинный у file id=" + file.getId());
            }

            if (file.getStatus() == null) {
                throw new IllegalArgumentException("Ошибка загрузки: status отсутствует у file id=" + file.getId());
            }

        }
        Set<Long> linkIds = new HashSet<>();

        for (AttachmentLink link : snapShot.getLinks()) {
            if (link == null) {
                throw new IllegalArgumentException("Ошибка загрузки: найден пустой link");
            }
            if (linkIds.contains(link.getId())) {
                throw new IllegalArgumentException("Ошибка загрузки: повторяется link id=" + link.getId());
            }

            linkIds.add(link.getId());
            if (!fileIds.contains(link.getFileId())) {
                throw new IllegalArgumentException(
                        "Ошибка загрузки: link.fileId=" + link.getFileId()
                                + " ссылается на несуществующий файл"
                );
            }
            if (link.getTargetType() == null) {
                throw new IllegalArgumentException("Ошибка загрузки: targetType отсутствует у link id=" + link.getId());
            }
            if (link.getTargetId() <= 0) {
                throw new IllegalArgumentException("Ошибка загрузки: targetId должен быть больше 0 у link id=" + link.getId());
            }

        }

    }
}
