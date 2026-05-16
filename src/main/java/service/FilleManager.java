package service;

import domain.AttachmentLink;
import domain.AttachmentTargetType;
import domain.FileMeta;
import domain.FileStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import repository.FileMetaRepository;
import static service.AttachmentManager.attachmentLinks;

public class FilleManager {
    public static final TreeMap<Long, FileMeta> files = new TreeMap<>();
    SessionService sessionService;
    private final FileMetaRepository fileMetaRepository;

    public FilleManager(FileMetaRepository fileMetaRepository) {
        this.fileMetaRepository = fileMetaRepository;
    }


    public TreeMap<Long, FileMeta> getTreeMap(){
        return files;
    }
    public void loadFromDatabase() {
        files.clear();

        List<FileMeta> loadedFiles = fileMetaRepository.findAll();

        System.out.println("LOADED FROM DB = " + loadedFiles.size());

        for (FileMeta file : loadedFiles) {
            System.out.println("LOADED FILE ID = " + file.getId()
                    + ", NAME = " + file.getFileName());

            files.put(file.getId(), file);
        }

        System.out.println("FILES IN MAP = " + files.size());
    }
    public void addFileMeta(String describtion, String path, Long id) {
        FileMeta fileMeta = new FileMeta();
        List<FileMeta> loadedFiles = fileMetaRepository.findAll();

        System.out.println("LOADED FROM DB = " + loadedFiles.size());

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
        FileMeta savedFile = fileMetaRepository.save(fileMeta);
        files.put(savedFile.getId(), savedFile);
    }
    public void addFile(String name, String mime, Long size, String description, long ownerId) {
        FileMeta fileMeta = new FileMeta();

        fileMeta.setFileName(name);
        fileMeta.setDescription(description);
        fileMeta.setCreatedAt(Instant.now());
        fileMeta.setUpdatedAt(Instant.now());
        fileMeta.setStatus(FileStatus.ACTIVE);
        fileMeta.setMimeType(mime);
        fileMeta.setSizeBytes(size);
        fileMeta.setOwnerId(ownerId);

        System.out.println("REPOSITORY CLASS = " + fileMetaRepository.getClass().getName());
        System.out.println("BEFORE SAVE TO DB");

        FileMeta savedFile = fileMetaRepository.save(fileMeta);

        System.out.println("SAVED FILE ID = " + savedFile.getId());

        files.put(savedFile.getId(), savedFile);
    }
    public FileMeta getFileById(long id) {
        FileMeta fileMeta = files.get(id);
        if (fileMeta == null) {
            throw new IllegalArgumentException("Ошибка: file с id=" + id + " не найден");
        }
        return fileMeta;
    }

    public void setFilefields(long id, String describtion, String fileName, String mimeType, Long size){
        FileMeta fileMeta = getFileById(id);
        fileMeta.setUpdatedAt(Instant.now());
        fileMeta.setDescription(describtion);
        fileMeta.setFileName(fileName);
        long ownerId = SessionService.getCurrentUserId();
        fileMeta.setOwnerId(ownerId);
        fileMeta.setMimeType(mimeType);
        fileMeta.setSizeBytes(size);
    }


    public void updateFileDescription(long fileId, String description, long currentUserId) {
        FileMeta fileMeta = getFileById(fileId);
        checkOwner(fileMeta, sessionService.getCurrentUserId());
        fileMeta.setDescription(description);
        fileMeta.setUpdatedAt(Instant.now());
    }

    public void deleteFile(long id, long currentUserId) {
        FileMeta fileMeta = getFileById(id);
        checkOwner(fileMeta, sessionService.getCurrentUserId());

        if (fileMeta.getStatus() == FileStatus.DELETED) {
            throw new IllegalArgumentException("Ошибка: файл уже удалён");
        }
        fileMeta.setStatus(FileStatus.DELETED);
        fileMeta.setUpdatedAt(Instant.now());
    }
    public List<FileMeta> getAllFiles_noerrors() {
        return files.values()
                .stream()
                .filter(file -> file.getStatus() == FileStatus.ACTIVE)
                .toList();
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
    public void replaceAllFiles(List<FileMeta> files) {
        getTreeMap().clear();
        long maxId = 0;
        for (FileMeta file : files) {
            getTreeMap().put(file.getId(), file);
            if (file.getId() > maxId) {
                maxId = file.getId();
            }
        }
    }
    private void checkOwner(FileMeta fileMeta, Long currentUserId) {
        if (!fileMeta.getOwnerId().equals(currentUserId)) {
            throw new IllegalArgumentException(
                    "Ошибка: у вас нет прав на изменение этого объекта"
            );
        }
    }

}
