package commands;
import service.AuthService;
import service.FilleManager;
import service.AttachmentManager;
import service.SessionService;

import java.util.Scanner;
public class CLI {

    private final Scanner scanner;
    private final  FilleManager fileManager;
    private final  AttachmentManager attachmentManager;
    String args;
    private final CommandRegistry registry;
    AuthService authService;
    SessionService sessionService;

    public CLI(FilleManager filleManager, AttachmentManager attachmentManager, AuthService authService, SessionService sessionService) {
        this.fileManager = filleManager;
        this.attachmentManager = attachmentManager;
        this.scanner = new Scanner(System.in);
        this.sessionService = sessionService;
        this.authService = authService;
        this.registry = new CommandRegistry(filleManager, scanner, attachmentManager, args, authService, sessionService);
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


                if (command == null) {
                    System.out.println("Ошибка: неизвестная команда '" + commandName + "'");
                    start();
                }
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