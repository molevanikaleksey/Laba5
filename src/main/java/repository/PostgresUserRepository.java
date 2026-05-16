package repository;

import db.DatabaseConnectionFactory;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresUserRepository implements UserRepository {
    private final DatabaseConnectionFactory connectionFactory;

    public PostgresUserRepository(DatabaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users(login, password_hash, created_at)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setTimestamp(3, Timestamp.from(user.getCreatedAt()));

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                user.setUserId(resultSet.getLong("id"));
                return user;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при сохранении пользователя: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = """
                SELECT id, login, password_hash, created_at
                FROM users
                WHERE login = ?
                """;

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, login);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при поиске пользователя: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = """
                SELECT id, login, password_hash, created_at
                FROM users
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
            throw new IllegalStateException("Ошибка БД при поиске пользователя: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        String sql = """
                SELECT id, login, password_hash, created_at
                FROM users
                ORDER BY id
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = connectionFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }

            return users;

        } catch (SQLException e) {
            throw new IllegalStateException("Ошибка БД при загрузке пользователей: " + e.getMessage());
        }
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("login"),
                resultSet.getString("password_hash"),
                resultSet.getLong("id"),
                resultSet.getTimestamp("created_at").toInstant()
        );
    }
}