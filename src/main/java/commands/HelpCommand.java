package commands;

//import service.CollectionManager;
import java.util.Map;

import static commands.CommandRegistry.commands;

public class HelpCommand implements Command {

    public HelpCommand() {
    }

    @Override
    public void execute(String[] args) {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            String key = entry.getValue().name();
            String value = entry.getValue().description();

            System.out.println(key + " - " + value);
        }
    }

    @Override
    public String description() {
        return "Вывод информации о командах";
    }

    @Override
    public String name() {
        return "help";
    }
}



