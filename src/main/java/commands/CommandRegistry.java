package commands;

import java.util.HashMap;
import java.util.Scanner;

import service.*;
public class CommandRegistry {
    FilleManager filleManager;
    AttachmentManager attachmentManager;
    Scanner scanner;
    String args;
    public CommandRegistry(FilleManager filleManager, Scanner scanner ,AttachmentManager attachmentManager, String args){
        this.filleManager = filleManager;
        this.attachmentManager = attachmentManager;


        this.args = args;
    }
    static final HashMap<String, Command> commands = new HashMap<>();

    public CommandRegistry(){
        commands.put("help", new HelpCommand());
        commands.put("exit", new ExitCommand());
        commands.put("file_add", new FileAddCommand(filleManager, scanner));
        commands.put("file_list", new FileListCommand(filleManager));
        commands.put("file_show", new FileShowCommand(filleManager));
        commands.put("file_link", new FileLinkCommand(attachmentManager, scanner, filleManager));
        commands.put("file_links", new FileLinksCommand(attachmentManager, filleManager));
        commands.put("obj_files", new ObjFilesCommand(filleManager));
        commands.put("file_delete", new FileDeleteCommand(attachmentManager, filleManager));
        commands.put("file_unlink", new FileUnlinkCommand(attachmentManager));
        commands.put("file_export_meta", new FileExportMetaCommand(filleManager));
        commands.put("file_update", new FileUpdateCommand(filleManager, scanner));


    }

//    public void register(String commandName, String description) {
//        commands.put(commandName, description);
//    }

    public Command getCommand(String command){
        return commands.get(command);
    }
//    public TreeMap<String, String> getCommands() {
//        return commands;
//    }

//    public void printHelp() {
//        System.out.println("Доступные команды:");
//        for (Map.Entry<String, String> entry : commands.entrySet()) {
//            System.out.println(entry.getKey() + " - " + entry.getValue());
//        }
//    }
}
