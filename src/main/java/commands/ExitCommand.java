package commands;


public class ExitCommand implements Command{

    public ExitCommand() {
    }


    @Override
    public void execute(String[] args) {
        System.out.println("Выход из программы.");
    }
    public String description(){
        return "выход из программы";
    }
    public String name() {
        return "exit";
    }
}
