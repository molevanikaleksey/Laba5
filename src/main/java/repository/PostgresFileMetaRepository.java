package repository;

import db.DatabaseConnectionFactory;
import domain.FileMeta;
import domain.FileStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresFileMetaRepository implements FileMetaRepository {
    private final DatabaseConnectionFactory connectionFactory;

    public PostgresFileMetaRepository(DatabaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public FileMeta save(FileMeta fileMeta) {
        String sql = """
                INSERT INTO file_meta(
                    file_name,
                    mime_type,
                    size_bytes,
                    description,
                    status,
                    owner_id,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, fileMeta.getFileName());
            statement.setString(2, fileMeta.getMimeType());
            statement.setLong(3, fileMeta.getSizeBytes());
            statement.setString(4, fileMeta.getDescription());
            statement.setString(5, fileMeta.getStatus().name());
            statement.setLong(6, fileMeta.getOwnerId());
            statement.setTimestamp(7, Timestamp.from(fileMeta.getCreatedAt()));
            statement.setTimestamp(8, Timestamp.from(fileMeta.getUpdatedAt()));

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                fileMeta.setId(resultSet.getLong("id"));
                return fileMeta;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при сохранении файла: " + e.getMessage());
        }
    }

    @Override
    public void update(FileMeta fileMeta) {
        String sql = """
                UPDATE file_meta
                SET file_name = ?,
                    mime_type = ?,
                    size_bytes = ?,
                    description = ?,
                    status = ?,
                    owner_id = ?,
                    updated_at = ?
                WHERE id = ?
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, fileMeta.getFileName());
            statement.setString(2, fileMeta.getMimeType());
            statement.setLong(3, fileMeta.getSizeBytes());
            statement.setString(4, fileMeta.getDescription());
            statement.setString(5, fileMeta.getStatus().name());
            statement.setLong(6, fileMeta.getOwnerId());
            statement.setTimestamp(7, Timestamp.from(fileMeta.getUpdatedAt()));
            statement.setLong(8, fileMeta.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalArgumentException("Ошибка: файл с id=" + fileMeta.getId() + " не найден");
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при обновлении файла: " + e.getMessage());
        }
    }

    @Override
    public Optional<FileMeta> findById(long id) {
        String sql = """
                SELECT *
                FROM file_meta
                WHERE id = ?
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при поиске файла: " + e.getMessage());
        }
    }

    @Override
    public List<FileMeta> findAll() {
        String sql = """
            SELECT *
            FROM file_meta
            ORDER BY id
            """;

        List<FileMeta> files = new ArrayList<>();

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                files.add(mapRow(resultSet));
            }

            System.out.println("FIND ALL FROM DB = " + files.size());

            return files;

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при загрузке файлов: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = """
                DELETE FROM file_meta
                WHERE id = ?
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при удалении файла: " + e.getMessage());
        }
    }

    private FileMeta mapRow(ResultSet resultSet) throws SQLException {
        FileMeta fileMeta = new FileMeta();

        fileMeta.setId(resultSet.getLong("id"));
        fileMeta.setFileName(resultSet.getString("file_name"));
        fileMeta.setMimeType(resultSet.getString("mime_type"));
        fileMeta.setSizeBytes(resultSet.getLong("size_bytes"));
        fileMeta.setDescription(resultSet.getString("description"));
        fileMeta.setStatus(FileStatus.valueOf(resultSet.getString("status")));
        fileMeta.setOwnerId(resultSet.getLong("owner_id"));
        fileMeta.setCreatedAt(resultSet.getTimestamp("created_at").toInstant());
        fileMeta.setUpdatedAt(resultSet.getTimestamp("updated_at").toInstant());

        return fileMeta;
    }
}