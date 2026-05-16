package service;

import domain.AttachmentLink;
import domain.AttachmentTargetType;
import domain.FileMeta;
import repository.AttachmentLinkRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AttachmentManager {
    private final AttachmentLinkRepository attachmentLinkRepository;
    private final FilleManager fileManager;

    static final TreeMap<Long, AttachmentLink> attachmentLinks = new TreeMap<>();

    public AttachmentManager(
            AttachmentLinkRepository attachmentLinkRepository,
            FilleManager fileManager
    ) {
        this.attachmentLinkRepository = attachmentLinkRepository;
        this.fileManager = fileManager;
    }


    public void loadFromDatabase() {
        attachmentLinks.clear();

        List<AttachmentLink> loadedLinks = attachmentLinkRepository.findAll();

        for (AttachmentLink link : loadedLinks) {
            attachmentLinks.put(link.getId(), link);
        }
    }

    public AttachmentLink linkFile(
            long fileId,
            AttachmentTargetType targetType,
            long targetId,
            long ownerId
    ) {
        FileMeta file = fileManager.getFileById(fileId);

        if (file.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("Ошибка: у вас нет прав на изменение этого файла");
        }

        AttachmentLink link = new AttachmentLink();

        link.setFileId(fileId);
        link.setTargetType(targetType);
        link.setTargetId(targetId);
        link.setOwnerId(ownerId);
        link.setCreatedAt(Instant.now());

        AttachmentLink savedLink = attachmentLinkRepository.save(link);

        attachmentLinks.put(savedLink.getId(), savedLink);
        System.out.println("LINK OWNER ID = " + link.getOwnerId());

        return savedLink;
    }

    public void unlinkFile(
            long fileId,
            AttachmentTargetType targetType,
            long targetId,
            long currentUserId
    ) {
        AttachmentLink foundLink = null;

        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getFileId() == fileId
                    && link.getTargetType() == targetType
                    && link.getTargetId() == targetId) {
                foundLink = link;
                break;
            }
        }

        if (foundLink == null) {
            throw new IllegalArgumentException("Ошибка: связь не найдена");
        }

        if (foundLink.getOwnerId() != currentUserId) {
            throw new IllegalArgumentException("Ошибка: у вас нет прав на удаление этой связи");
        }

        attachmentLinkRepository.delete(fileId, targetType, targetId);

        attachmentLinks.remove(foundLink.getId());
    }
    public void addAttachmentLinks(AttachmentLink link) {
        if (link == null) {
            throw new IllegalArgumentException("Ошибка: связь не может быть null");
        }

        if (link.getId() == 0) {
            throw new IllegalArgumentException("Ошибка: id связи не может быть null");
        }

        attachmentLinks.put(link.getId(), link);
    }

    public List<AttachmentLink> getLinksByFileId(long fileId) {
        List<AttachmentLink> result = new ArrayList<>();

        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getFileId() == fileId) {
                result.add(link);
            }
        }

        return result;
    }

    public List<AttachmentLink> getAllLinksForSave() {
        return new ArrayList<>(attachmentLinks.values());
    }
    public void setStatusDel(long fileId) {
        List<AttachmentLink> linksToDelete = new ArrayList<>();

        for (AttachmentLink link : attachmentLinks.values()) {
            if (link.getFileId() == fileId) {
                linksToDelete.add(link);
            }
        }

        for (AttachmentLink link : linksToDelete) {
            attachmentLinkRepository.delete(
                    link.getFileId(),
                    link.getTargetType(),
                    link.getTargetId()
            );

            attachmentLinks.remove(link.getId());
        }
    }
}
/*
package service;


import domain.AttachmentLink;
import domain.AttachmentTargetType;
import domain.FileMeta;
import domain.FileStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import repository.AttachmentLinkRepository;
import static service.FilleManager.files;


public class AttachmentManager {
    AttachmentLinkRepository attachmentLinkRepository;
    FilleManager filleManager;
    public static final TreeMap<Long, AttachmentLink> attachmentLinks = new TreeMap<>();

    public AttachmentManager(AttachmentLinkRepository attachmentLinkRepository, FilleManager filleManager) {
        this.attachmentLinkRepository = attachmentLinkRepository;
        this.filleManager =filleManager;
    }

    public AttachmentManager() {
    }

    public void loadFromDatabase() {
        attachmentLinks.clear();
        for (AttachmentLink link : attachmentLinkRepository.findAll()) {
            attachmentLinks.put(link.getId(), link);
        }
    }
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

    public void unlinkFile(long fileId, AttachmentTargetType targetType, long targetId, long currentUserId) {
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
    public AttachmentLink linkFile(long fileId, AttachmentTargetType targetType, long targetId, long ownerId) {
        if (fileId < 0) {
            throw new IllegalArgumentException("Ошибка: file_id должен быть больше 0");
        }

        if (targetType == null) {
            throw new IllegalArgumentException("Ошибка: тип объекта не может быть пустым");
        }

        if (targetId < 0) {
            throw new IllegalArgumentException("Ошибка: ID объекта должен быть больше 0");
        }

        AttachmentLink link = new AttachmentLink(
                Generator.getAttNextId(),
                fileId,
                targetType,
                targetId,
                "SYSTEM",
                Instant.now(),
                SessionService.getCurrentUserId()
        );

        attachmentLinks.put(link.getId(), link);

        return link;
    }
}

 */
