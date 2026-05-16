import commands.CLI;
import persistence.JsonUserRepository;
import repository.AttachmentLinkRepository;
import repository.FileMetaRepository;
import repository.UserRepository;
import security.PasswordHash;
import service.*;

import java.nio.file.Path;

public class MainJson {

    public static void main(String[] args) {
        FileMetaRepository fileMetaRepository;
        AttachmentLinkRepository attachmentLinkRepository;
        FilleManager filleManager;
        //AttachmentManager attachmentManager = new AttachmentManager(attachmentLinkRepository, filleManager);
        //FilleManager filleManager = new FilleManager(fileMetaRepository);

        UserRepository userRepository =
                new JsonUserRepository(Path.of("users.json"));

        PasswordHash passwordHash = new PasswordHash();
        SessionService sessionService = new SessionService();

        AuthService authService = new AuthService(
                userRepository,
                passwordHash,
                sessionService
        );
        /*
        CLI cli = new CLI(
                filleManager,
                attachmentManager,
                authService,
                sessionService
        );

         */

        System.out.println("Метод хранения: JSON");

        //cli.start();
    }
}