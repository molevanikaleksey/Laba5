package repository;

import domain.FileMeta;

import java.util.List;
import java.util.Optional;

public interface FileMetaRepository {
    FileMeta save(FileMeta fileMeta);

    void update(FileMeta fileMeta);

    Optional<FileMeta> findById(long id);

    List<FileMeta> findAll();

    void deleteById(long id);
}