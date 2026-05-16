package commands;

import service.SessionService;

public class LogoutCommand implements Command {
    SessionService sessionService;

    public LogoutCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void execute(String[] args) {
        if (!sessionService.isAuthorized()) {
            throw new IllegalStateException("Ошибка: вы не вошли в аккаунт");
        }

        String username = sessionService.getCurrentUsername();
        sessionService.logout();

        System.out.println("OK logout from " + username);
    }

    @Override
    public String description() {
        return "Выйти из аккаунта";
    }

    @Override
    public String name() {
        return "logout";
    }
}