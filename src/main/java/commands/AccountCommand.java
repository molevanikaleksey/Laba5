package commands;

import domain.User;
import service.SessionService;

public class AccountCommand implements Command {
    SessionService sessionService;

    public AccountCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void execute(String[] args) {
        if (!sessionService.isAuthorized()) {
            System.out.println("Вы не авторизованы");
            return;
        }

        User user = sessionService.getCurrentUser();

        System.out.println("Current account:");
        System.out.println("id: " + user.getUserId());
        System.out.println("login: " + user.getLogin());
        System.out.println("createdAt: " + user.getCreatedAt());
    }

    @Override
    public String description() {
        return "посмотреть Данные аккаунта";
    }

    @Override
    public String name() {
        return "account";
    }
}