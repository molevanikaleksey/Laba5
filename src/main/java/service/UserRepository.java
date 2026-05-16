package service;

import domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByLogin(String login);
    List<User> findAll();
}
