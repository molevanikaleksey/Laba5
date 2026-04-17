package service;
/*
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
public class CollectionManager {

    private static final TreeMap<Long, FileMeta> files = new TreeMap<>();
    private final TreeMap<Long, AttachmentLink> attachmentLinks = new TreeMap<>();

    public long getFileNextId() {
        return System.currentTimeMillis() + files.size();
    }
    public static long getNextId() {
        return System.currentTimeMillis() + attachmentLinks.size();
    }
    /*
    public static long getNextAttachId(){
        return System.currentTimeMillis() + attachmentLinks.size();
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

    public List<FileMeta> getAllFiles() {
        return new ArrayList<>(files.values());
    }
/*
    public TreeMap<Long, AttachmentLink> searchType(AttachmentTargetType targetType){
        if (targetType == AttachmentTargetType.REPORT) {
            return report;
        } else if (targetType == AttachmentTargetType.SAMPLE) {
            return sample;
        } else {
            return measurement;
        }
    }



    public void addAttachmentLink(Long fileId, AttachmentTargetType targetType, Long targetid) {
        AttachmentLink attLink = new AttachmentLink();
        for (Long i : attachmentLinks.keySet()) {
            if (attLink.getId() == i) {
                throw new IllegalArgumentException("Объект с таким id уже существует");
            }
        }
        attLink.setId(getNextId(attachmentLinks));
        attLink.setCreatedAt(Instant.now());
        attLink.setFileId(fileId);
        attLink.setTargetType(targetType);
        attLink.setTargetId(targetid);

        attLink.setCreatedAt(Instant.now());
        attachmentLinks.put(attLink.getId(), attLink);
    }

    public List<AttachmentLink> getLinksByFileId(long fileId) {
        getFileById(fileId);

        List<AttachmentLink> result = new ArrayList<>();
        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getFileId() == fileId) {
                result.add(link);
            }
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


    public void setStatusDel(Long fileId){
        FileMeta fileMeta = files.get(fileId);
        fileMeta.setStatus(FileStatus.DELETED);
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

    public void unlinkFile(long fileId, AttachmentTargetType targetType, long targetId) {
        getFileById(fileId);

        long foundLinkId = -1;

        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getFileId() == fileId
                    && link.getTargetType() == targetType
                    && link.getTargetId() == targetId) {
                foundLinkId = link.getId();
                break;
            }
        }

        if (foundLinkId == -1) {
            throw new IllegalArgumentException("Ошибка: связь не найдена");
        }

        attachmentLinks.remove(foundLinkId);
    }
}

 */

