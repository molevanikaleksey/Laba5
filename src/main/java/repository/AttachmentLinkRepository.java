package repository;

import domain.AttachmentLink;
import domain.AttachmentTargetType;

import java.util.List;
import java.util.Optional;

public interface AttachmentLinkRepository {
    AttachmentLink save(AttachmentLink link);

    Optional<AttachmentLink> findById(long id);

    List<AttachmentLink> findAll();

    List<AttachmentLink> findByFileId(long fileId);

    List<AttachmentLink> findByTarget(AttachmentTargetType targetType, long targetId);

    void delete(long fileId, AttachmentTargetType targetType, long targetId);
}