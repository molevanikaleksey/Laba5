package repository;

import db.DatabaseConnectionFactory;
import domain.AttachmentLink;
import domain.AttachmentTargetType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresAttachmentLinkRepository implements AttachmentLinkRepository {
    private final DatabaseConnectionFactory connectionFactory;

    public PostgresAttachmentLinkRepository(DatabaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public AttachmentLink save(AttachmentLink link) {
        String sql = """
                INSERT INTO attachment_link(
                    file_id,
                    target_type,
                    target_id,
                    owner_id,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, link.getFileId());
            statement.setString(2, link.getTargetType().name());
            statement.setLong(3, link.getTargetId());
            statement.setLong(4, link.getOwnerId());
            statement.setTimestamp(5, Timestamp.from(link.getCreatedAt()));

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                link.setId(resultSet.getLong("id"));
                return link;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при создании связи: " + e.getMessage());
        }
    }

    @Override
    public Optional<AttachmentLink> findById(long id) {
        String sql = """
                SELECT *
                FROM attachment_link
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
            throw new IllegalStateException("Ошибка БД при поиске связи: " + e.getMessage());
        }
    }

    @Override
    public List<AttachmentLink> findAll() {
        String sql = """
                SELECT *
                FROM attachment_link
                ORDER BY id
                """;

        List<AttachmentLink> links = new ArrayList<>();

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                links.add(mapRow(resultSet));
            }

            return links;

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при загрузке связей: " + e.getMessage());
        }
    }

    @Override
    public List<AttachmentLink> findByFileId(long fileId) {
        String sql = """
                SELECT *
                FROM attachment_link
                WHERE file_id = ?
                ORDER BY id
                """;

        List<AttachmentLink> links = new ArrayList<>();

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, fileId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    links.add(mapRow(resultSet));
                }
            }

            return links;

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при поиске связей файла: " + e.getMessage());
        }
    }

    @Override
    public List<AttachmentLink> findByTarget(AttachmentTargetType targetType, long targetId) {
        String sql = """
                SELECT *
                FROM attachment_link
                WHERE target_type = ? AND target_id = ?
                ORDER BY id
                """;

        List<AttachmentLink> links = new ArrayList<>();

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, targetType.name());
            statement.setLong(2, targetId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    links.add(mapRow(resultSet));
                }
            }

            return links;

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при поиске файлов объекта: " + e.getMessage());
        }
    }

    @Override
    public void delete(long fileId, AttachmentTargetType targetType, long targetId) {
        String sql = """
                DELETE FROM attachment_link
                WHERE file_id = ? AND target_type = ? AND target_id = ?
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, fileId);
            statement.setString(2, targetType.name());
            statement.setLong(3, targetId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при удалении связи: " + e.getMessage());
        }
    }

    private AttachmentLink mapRow(ResultSet resultSet) throws SQLException {
        return new AttachmentLink(
                resultSet.getLong("id"),
                resultSet.getLong("file_id"),
                AttachmentTargetType.valueOf(resultSet.getString("target_type")),
                resultSet.getLong("target_id"),
                resultSet.getLong("owner_id"),
                resultSet.getTimestamp("created_at").toInstant()
        );
    }
}