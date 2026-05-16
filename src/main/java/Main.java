
import commands.CLI;
import db.DatabaseConfig;
import db.DatabaseConnectionFactory;
import repository.AttachmentLinkRepository;
import repository.FileMetaRepository;
import repository.PostgresAttachmentLinkRepository;
import repository.PostgresFileMetaRepository;
import repository.PostgresUserRepository;
import repository.UserRepository;
import security.PasswordHash;
import service.*;

public class Main {
    public static void main(String[] args) {
        DatabaseConfig config = new DatabaseConfig("db.properties");
        DatabaseConnectionFactory connectionFactory =
                new DatabaseConnectionFactory(config);

        UserRepository userRepository =
                new PostgresUserRepository(connectionFactory);

        FileMetaRepository fileMetaRepository =
                new PostgresFileMetaRepository(connectionFactory);

        AttachmentLinkRepository attachmentLinkRepository =
                new PostgresAttachmentLinkRepository(connectionFactory);

        FilleManager filleManager = new FilleManager(fileMetaRepository);

        AttachmentManager attachmentManager =
                new AttachmentManager(attachmentLinkRepository, filleManager);

        filleManager.loadFromDatabase();
        attachmentManager.loadFromDatabase();

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
        System.out.println("Метод хранения: бд");
        cli.start();

    }
}