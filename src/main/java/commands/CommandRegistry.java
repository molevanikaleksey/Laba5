package commands;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import domain.User;
import persistence.FileStorage;
import security.PasswordHash;
import service.*;
public class CommandRegistry {
    FilleManager filleManager;
    AttachmentManager attachmentManager;
    Scanner scanner;
    String args;
    FileStorage storage = new FileStorage();

    public CommandRegistry(FilleManager filleManager, Scanner scanner ,AttachmentManager attachmentManager, String args, AuthService authService, SessionService sessionService){
        this.filleManager = filleManager;
        this.attachmentManager = attachmentManager;
        this.args = args;
        commands.put("help", new HelpCommand());
        commands.put("exit", new ExitCommand());
        commands.put("file_add", new FileAddCommand(filleManager, scanner, sessionService));
        commands.put("file_list", new FileListCommand(filleManager));
        commands.put("file_show", new FileShowCommand(filleManager));
        commands.put("file_link", new FileLinkCommand(attachmentManager, scanner, filleManager, sessionService));
        commands.put("file_links", new FileLinksCommand(attachmentManager, filleManager));
        commands.put("obj_files", new ObjFilesCommand(filleManager));
        commands.put("file_delete", new FileDeleteCommand(attachmentManager, filleManager, sessionService));
        commands.put("file_unlink", new FileUnlinkCommand(attachmentManager, sessionService));
        commands.put("file_export_meta", new FileExportMetaCommand(filleManager));
        commands.put("file_update", new FileUpdateCommand(filleManager, scanner, sessionService));
        commands.put("save", new SaveCommand(filleManager, attachmentManager, storage));
        commands.put("load", new LoadCommand(filleManager, attachmentManager, storage));
        commands.put("register", new RegisterCommand(authService, scanner));
        commands.put("login", new LoginCommand(authService, scanner));
        commands.put("logout", new LogoutCommand(sessionService));
        commands.put("account", new AccountCommand(sessionService));
    }
    static final HashMap<String, Command> commands = new HashMap<>();


    public Command getCommand(String command){
        return commands.get(command);
    }

}
