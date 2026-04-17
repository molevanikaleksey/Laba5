package commands;
import service.FilleManager;
import service.AttachmentManager;

import java.util.Scanner;
public class CLI {

    private final Scanner scanner;
    private final  FilleManager fileManager;
    private final  AttachmentManager attachmentManager;
    private final CommandRegistry registry;

    public CLI(FilleManager filleManager, AttachmentManager attachmentManager) {
        this.fileManager = filleManager;
        this.attachmentManager = attachmentManager;
        this.scanner = new Scanner(System.in);
        this.registry = new CommandRegistry();
    }


    public void start() {
        System.out.println("Введите команду. help - список команд, exit - выход.");

        boolean running = true;

        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            try {
                String[] args = line.split(" ");
                String commandName = args[0];

                Command command = registry.getCommand(commandName);

                try {
                    command.execute(args);
                }catch (Exception e){
                    System.out.println("Возможно вы неправильно написали имя команды");
                }

                if (command instanceof ExitCommand){
                    running = false;
                }
/*
                switch (commandName) {
                    case "help":
                        helpCommand.execute(registry);
                        break;

                    case "exit":
                        running = exitCommand.execute();
                        break;

                    case "file_add":
                        fileAddCommand.execute(args, manager, scanner);
                        break;

                    case "file_list":
                        fileListCommand.execute(args, manager);
                        break;

                    case "file_show":
                        fileShowCommand.execute(args, manager);
                        break;

                    case "file_link":
                        fileLinkCommand.execute(args, manager, scanner);
                        break;

                    case "file_links":
                        fileLinksCommand.execute(args, manager);
                        break;

                    case "obj_files":
                        objFilesCommand.execute(args, manager);
                        break;

                    case "file_update":
                        fileUpdateCommand.execute(args, manager);
                        break;

                    case "file_delete":
                        fileDeleteCommand.execute(args, manager);
                        break;

                    case "file_unlink":
                        fileUnlinkCommand.execute(args, manager);
                        break;

                    case "file_export_meta":
                        fileExportMetaCommand.execute(args, manager);
                        break;

                    default:
                        System.out.println("Ошибка: неизвестная команда");
                        break;
                        }


 */


            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}