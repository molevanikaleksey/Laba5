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
