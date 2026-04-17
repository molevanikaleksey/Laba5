import commands.CLI;
import service.FilleManager;
import service.AttachmentManager;

public class Main {
    public static void main(String[] args) {
        AttachmentManager attachmentManager = new AttachmentManager();
        FilleManager filleManager = new FilleManager();
        CLI cli = new CLI(filleManager, attachmentManager);
        cli.start();

    }
}
