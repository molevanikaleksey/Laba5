package service;

import domain.User;
import repository.UserRepository;
import security.PasswordHash;

import java.time.Instant;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHash passwordHash;
    private final SessionService sessionService;

    public AuthService(UserRepository userRepository, PasswordHash passwordHash, SessionService sessionService) {
        this.userRepository = userRepository;
        this.passwordHash = passwordHash;
        this.sessionService = sessionService;
    }


    public void register(String login, String password){
        validateLogin(login);
        validatePassword(password);

        if (userRepository.findByLogin(login).isPresent()){
            throw new IllegalArgumentException("Ошибка: Логин уже занят");
        }
        String hash = PasswordHash.hashPassword(password);
        User user = new User(login, hash, Generator.nextUserId(), Instant.now());
        userRepository.save(user);
        sessionService.login(user);

    }
    public void login(String login, String password){
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (optionalUser.isEmpty()){
            throw new IllegalArgumentException("Ошибка: неверный логин");
        }
        User user = optionalUser.get();
        String inputHash = passwordHash.hashPassword(password);
        if (!user.getPassword().equals(inputHash)){
            throw new IllegalArgumentException("Ошибка: неверный пароль");

        }
        sessionService.login(user);
    }
    public void validateLogin(String login){
        if (login == null || login.isBlank()){
            throw new IllegalArgumentException("Ошибка: Логин не может быть пустым");
        }
        if (login.length() > 64){
            throw new IllegalArgumentException("Ошибка: Логин слишком длинный");
        }
    }
    public void validatePassword(String password){
        if (password == null || password.isBlank()){
            throw new IllegalArgumentException("Ошибка: пароль не может быть пустым");
        }
        if (password.length() < 4){
            throw new IllegalArgumentException("Ошибка: Слишком короткий пароль");
        }
    }
}
