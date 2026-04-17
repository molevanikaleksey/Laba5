package service;

import domain.AttachmentLink;
import domain.AttachmentTargetType;
import domain.FileMeta;
import domain.FileStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static service.AttachmentManager.attachmentLinks;

public class FilleManager {
    public static final TreeMap<Long, FileMeta> files = new TreeMap<>();
    public TreeMap<Long, FileMeta> getTreeMap(){
        return files;
    }
    public void addFileMeta(String describtion, String path, Long id) {
        FileMeta fileMeta = new FileMeta();
        for (Long i : files.keySet()) {
            if (id == i) {
                throw new IllegalArgumentException("Объект с таким id уже существует");
            }
        }
        fileMeta.setId(id);
        fileMeta.setDescription(describtion);
        fileMeta.setCreatedAt(Instant.now());
        fileMeta.setPath(path);
        fileMeta.setStatus(FileStatus.ACTIVE);
        files.put(id, fileMeta);
    }

    public FileMeta getFileById(long id) {
        FileMeta fileMeta = files.get(id);
        if (fileMeta == null) {
            throw new IllegalArgumentException("Ошибка: file с id=" + id + " не найден");
        }
        return fileMeta;
    }

    public void setFilefields(long id, String describtion, String fileName, String owner, String mimeType, Long size){
        FileMeta fileMeta = getFileById(id);
        fileMeta.setUpdatedAt(Instant.now());
        fileMeta.setDescription(describtion);
        fileMeta.setFileName(fileName);
        fileMeta.setOwnerUsername(owner);
        fileMeta.setMimeType(mimeType);
        fileMeta.setSizeBytes(size);
    }


    public void updateFileDescription(long fileId, String description) {
        FileMeta fileMeta = getFileById(fileId);

        fileMeta.setDescription(description);
        fileMeta.setUpdatedAt(Instant.now());
    }

    public void deleteFile(long id) {
        FileMeta fileMeta = getFileById(id);

        if (fileMeta.getStatus() == FileStatus.DELETED) {
            throw new IllegalArgumentException("Ошибка: файл уже удалён");
        }
        fileMeta.setStatus(FileStatus.DELETED);
        fileMeta.setUpdatedAt(Instant.now());
    }
    public List<FileMeta> getAllFiles_noerrors(){
        return new ArrayList<>(files.values());
    }
    public List<FileMeta> getAllFiles() {
        List<FileMeta> result = new ArrayList<>();
        for (FileMeta file : files.values()) {
            if (file.getStatus() == FileStatus.ACTIVE) {
                result.add(file);
            }
        }
        if (result.isEmpty()){
            throw new ArrayIndexOutOfBoundsException("Массив файлов пустой. Вы не добавили ни одного файла, или все файлы удалены");
        }
        return result;
    }

    public List<FileMeta> getFilesByObject(AttachmentTargetType targetType, long targetId) {
        List<FileMeta> result = new ArrayList<>();
        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getTargetType() == targetType && link.getTargetId() == targetId) {
                FileMeta fileMeta = files.get(link.getFileId());
                if (fileMeta != null) {
                    result.add(fileMeta);
                }
            }
        }
        return result;
    }
    public List<FileMeta> getLastFiles(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Ошибка: число должно быть больше 0");
        }

        List<FileMeta> all = new ArrayList<>(files.values());
        List<FileMeta> result = new ArrayList<>();

        int start = Math.max(0, all.size() - n);
        for (int i = start; i < all.size(); i++) {
            result.add(all.get(i));
        }

        return result;
    }
}
