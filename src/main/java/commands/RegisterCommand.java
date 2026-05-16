package commands;

import service.AuthService;

import java.util.Scanner;

public class RegisterCommand implements Command{
    private final AuthService authService;
    private final Scanner scanner;
    public RegisterCommand(AuthService authService, Scanner scanner){
        this.authService = authService;
        this.scanner = scanner;
    }
    @Override
    public void execute(String[] args){
        System.out.println("Логин: ");
        String login = scanner.nextLine();
        System.out.println("Пароль: ");
        String password = scanner.nextLine();

        authService.register(login, password);
        System.out.println("OK user registered");
    }

    @Override
    public String description() {
        return "Регистрирует новых пользователей";
    }

    @Override
    public String name() {
        return "register";
    }
}
