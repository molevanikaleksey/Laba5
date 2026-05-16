package service;

import domain.AttachmentLink;
import domain.AttachmentTargetType;
import domain.FileMeta;
import domain.FileStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static service.FilleManager.files;


public class AttachmentManager {

    public static final TreeMap<Long, AttachmentLink> attachmentLinks = new TreeMap<>();
    FilleManager filleManager = new FilleManager();
    FileMeta fileMeta = new FileMeta();
    public void addAttachmentLink(Long fileId, AttachmentTargetType targetType, Long targetid) {
        AttachmentLink attLink = new AttachmentLink();
        for (Long i : attachmentLinks.keySet()) {
            if (attLink.getId() == i) {
                throw new IllegalArgumentException("Объект с таким id уже существует");
            }
        }
        attLink.setId(Generator.getAttNextId());
        attLink.setCreatedAt(Instant.now());
        attLink.setFileId(fileId);
        attLink.setTargetType(targetType);
        attLink.setTargetId(targetid);

        attLink.setCreatedAt(Instant.now());
        attachmentLinks.put(attLink.getId(), attLink);
    }
    public FileMeta getFileById(long id) {
        FileMeta fileMeta = files.get(id);
        if (fileMeta == null) {
            throw new IllegalArgumentException("Ошибка: file с id=" + id + " не найден");
        }
        return fileMeta;
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

    public void setStatusDel(Long fileId){
        FileMeta fileMeta = files.get(fileId);
        fileMeta.setStatus(FileStatus.DELETED);
    }

    public void unlinkFile(
            Long fileId,
            AttachmentTargetType targetType,
            Long targetId,
            Long currentUserId
    ) {
        AttachmentLink found = null;

        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getFileId().equals(fileId)
                    && link.getTargetType() == targetType
                    && link.getTargetId().equals(targetId)) {
                found = link;
                break;
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("Ошибка: связь не найдена");
        }

        if (!found.getOwnerId().equals(currentUserId)) {
            throw new IllegalArgumentException("Ошибка: у вас нет прав на удаление этой связи");
        }

        attachmentLinks.remove(found.getId());
    }
    public ArrayList<AttachmentLink> getAllLinksForSave(){
        return new ArrayList<>(attachmentLinks.values());
    }
    public void replaceAllLinks(List<AttachmentLink> linksFromJson) {
        attachmentLinks.clear();
        for (AttachmentLink link : linksFromJson) {
            attachmentLinks.put(link.getId(), link);
        }
    }
    public String getLinkDetails(FileMeta link) {
        if (link == null) {
            throw new IllegalArgumentException("Ошибка: связь не выбрана");
        }
        AttachmentLink attachmentLink = new AttachmentLink();

        return "AttachmentLink #" + attachmentLink.getId() + "\n\n" +
                "fileId: " + attachmentLink.getFileId() + "\n" +
                "targetType: " + attachmentLink.getTargetType() + "\n" +
                "targetId: " + attachmentLink.getTargetId() + "\n" +
                "owner: " + attachmentLink.getOwnerUsername() + "\n" +
                "createdAt: " + attachmentLink.getCreatedAt();
    }
    public void linkFile(Long fileId, AttachmentTargetType targetType, Long targetId, Long ownerId) {

        if (fileMeta.getOwnerId() == null) {
            throw new IllegalArgumentException(
                    "Ошибка: у файла нет владельца. Создайте файл заново или загрузите корректный JSON."
            );
        }

        if (!fileMeta.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Ошибка: у вас нет прав на изменение этого файла");
        }

        AttachmentLink link = new AttachmentLink();

        Long id = Generator.getAttNextId();

        link.setId(id);
        link.setFileId(fileId);
        link.setTargetType(targetType);
        link.setTargetId(targetId);
        link.setOwnerId(ownerId);
        link.setCreatedAt(Instant.now());

        attachmentLinks.put(id, link);
    }

}
