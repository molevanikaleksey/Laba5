package commands;

import service.AuthService;

import java.util.Scanner;

public class LoginCommand implements Command {
    private final AuthService authService;
    private final Scanner scanner;

    public LoginCommand(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        System.out.print("Логин: ");
        String login = scanner.nextLine();

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        authService.login(login, password);

        System.out.println("OK logged in as " + login);
    }

    @Override
    public String description() {
        return "Пользователь входит в аккаунт";
    }

    @Override
    public String name() {
        return "login";
    }
}
