package persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.User;
import repository.UserRepository;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import com.google.gson.GsonBuilder;


import java.time.Instant;


public class JsonUserRepository implements UserRepository {
    private final Path path;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .setPrettyPrinting()
            .create();

    private final List<User> users = new ArrayList<>();

    public JsonUserRepository(Path path) {
        this.path = path;
        load();
    }

    @Override
    public User save(User user) {
        long newId = users.stream()
                .mapToLong(User::getUserId)
                .max()
                .orElse(0) + 1;

        User userWithId = new User(
                user.getLogin(),
                user.getPassword(),
                newId,
                user.getCreatedAt()
        );

        users.add(userWithId);
        saveToFile();
        return userWithId;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    private void load() {
        try {
            if (!Files.exists(path)) {
                return;
            }

            Type type = new TypeToken<List<User>>() {}.getType();

            try (FileReader reader = new FileReader(path.toFile())) {
                List<User> loadedUsers = gson.fromJson(reader, type);

                if (loadedUsers != null) {
                    users.clear();
                    users.addAll(loadedUsers);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Ошибка загрузки пользователей: " + e.getMessage()
            );
        }
    }

    private void saveToFile() {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            try (FileWriter writer = new FileWriter(path.toFile())) {
                gson.toJson(users, writer);
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Ошибка сохранения пользователей: " + e.getMessage()
            );
        }
    }
}