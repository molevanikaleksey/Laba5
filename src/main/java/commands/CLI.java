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

                

                command.execute(args);


                if (command instanceof ExitCommand){
                    running = false;
                }



            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}