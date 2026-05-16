package repository;

import domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByLogin(String login);

    Optional<User> findById(long id);

    List<User> findAll();
}
