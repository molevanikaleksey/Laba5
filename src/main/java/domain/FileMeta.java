package domain;

import java.time.Instant;
import java.util.Objects;

public final class FileMeta {
    // Уникальный номер файла. Программа назначает сама.
    private long id;
    // Имя файла (например "photo1.png"). Нельзя пустое. До128 символов.
    private String fileName;
    // MIME тип (например "image/png"). Можно пусто. До 64символов.
    private String mimeType;
    // Размер в байтах. Не должен быть отрицательным.
    private long sizeBytes;
    // Описание файла (для человека). Можно пусто. До 256символов.
    private String description;
    // Статус файла: ACTIVE или DELETED.
    private FileStatus status;
    // Кто загрузил/добавил (логин). На ранних этапах можно "SYSTEM".
    private String ownerUsername;
    // Когда добавили. Программа ставит автоматически.
    private Instant createdAt;
    // Когда обновляли. Программа обновляет автоматически.
    private Instant updatedAt;
    private String path;
    private String OwnerName;
    private Long OwnerId;

    public FileMeta(long id, String path,String fileName, String mimeType, long sizeBytes, String description, FileStatus status, String ownerUsername, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.description = description;
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.createdAt = Instant.parse(createdAt.toString());
        this.updatedAt = Instant.parse(updatedAt.toString());
        this.path = path;
    }

    public FileMeta() {}

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public Long getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(Long ownerId) {
        OwnerId = ownerId;
    }

    public String getPath(){
        return path;
    }

    public String getDescription() {
        return description;
    }

    public FileStatus getStatus() {
        return status;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setFileName(String fileName) {
        if (fileName != null && fileName.length() <= 128){
            this.fileName = fileName;
        }else{
            throw new IllegalArgumentException("Неправильное имя файла");
        }
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMimeType(String mimeType) {
        if (mimeType.length() <=64){
            this.mimeType = mimeType;
        }else{
            throw new IllegalArgumentException("Неправильное имя файла");
        }
    }

    public void setSizeBytes(long sizeBytes) {
        if (sizeBytes > 0){
            this.sizeBytes = sizeBytes;
        }else{
            throw new IllegalArgumentException("Неверный формат, размер файла не должен быть меньше 0");
        }
    }

    public void setDescription(String description) {
        if ( description.length() < 256){
            this.description = description;
        }else{
            throw new IllegalArgumentException("Слишком длинное описание, должно быть меньше символов");
        }

    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = Instant.parse(createdAt.toString());
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = Instant.parse(updatedAt.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileMeta fileMeta = (FileMeta) o;
        return id == fileMeta.id && sizeBytes == fileMeta.sizeBytes && Objects.equals(fileName, fileMeta.fileName) && Objects.equals(mimeType, fileMeta.mimeType) && Objects.equals(description, fileMeta.description) && status == fileMeta.status && Objects.equals(ownerUsername, fileMeta.ownerUsername) && Objects.equals(createdAt, fileMeta.createdAt) && Objects.equals(updatedAt, fileMeta.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, mimeType, sizeBytes, description, status, ownerUsername, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", sizeBytes=" + sizeBytes +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}