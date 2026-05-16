import commands.CLI;
import commands.CommandRegistry;
import domain.User;
import persistence.JsonUserRepository;
import security.PasswordHash;
import service.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AttachmentManager attachmentManager = new AttachmentManager();
        FilleManager filleManager = new FilleManager();

        UserRepository userRepository =
                new JsonUserRepository(Path.of("users.json"));

        PasswordHash passwordHash = new PasswordHash();
        SessionService sessionService = new SessionService();

        AuthService authService = new AuthService(
                userRepository,
                passwordHash,
                sessionService
        );

        CLI cli = new CLI(
                filleManager,
                attachmentManager,
                authService,
                sessionService
        );

        cli.start();

    }
}
