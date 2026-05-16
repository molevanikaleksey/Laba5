package domain;

import java.time.Instant;

public final class AttachmentLink {
    // Уникальный номер связи "файл прикреплён". Программа назначает сама.
    private long id;
    // Какой файл прикрепляем (id файла).
    // Должен ссылаться на реально существующий FileMeta.
    private long fileId;
    // Тип объекта, к которому прикрепили SAMPLE/MEASUREMENT/REPORT).
    private AttachmentTargetType targetType;
    // ID объекта (sampleId / measurementId / reportId).
    private long targetId;
    // Кто сделал прикрепление (логин). На ранних этапах можно"SYSTEM".
    private String ownerUsername;
    // Когда прикрепили. Программа ставит автоматически.
    private Instant createdAt;
    private Long ownerId;

    public AttachmentLink(long id, long fileId, AttachmentTargetType targetType, long targetId, String ownerUsername, Instant createdAt, Long userId) {
        this.setId(id);
        this.setFileId(fileId);
        this.setTargetType(targetType);
        this.setTargetId(targetId);
        this.setOwnerUsername(ownerUsername);
        this.setOwnerUserId(userId);
        this.setCreatedAt(createdAt);
    }

    public AttachmentLink() {
    }

    public long getId() {
        return id;
    }

    public long getFileId() {
        return fileId;
    }

    public AttachmentTargetType getTargetType() {
        return targetType;
    }

    public long getTargetId() {
        return targetId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public void setTargetType(AttachmentTargetType targetType) {
        this.targetType = targetType;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void setOwnerUsername(String ownerUsername) {
        if (ownerUsername == null){
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        this.ownerUsername = ownerUsername;
    }

    public void setOwnerUserId(Long ownerUsername) {
        this.ownerId = ownerId;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}