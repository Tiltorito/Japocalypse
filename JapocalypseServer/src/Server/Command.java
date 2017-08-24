package Server;

/**
 * Created by mpampis on 7/2/2017.
 */
public class Command implements Executable {
    private String command;
    private Executable logic;

    public Command(String c) {
        command = c;
    }

    public boolean execute(String... args) {
        if(logic != null)
            return logic.execute(args);
        return false;
    }

    public void addLogic(Executable logic) {
        this.logic = logic;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof String) {
            String c = (String)o;
            return command.equals(c.split(" ")[0].trim());
        }
        else if(o instanceof Command) {
            return equals(o.toString());
        }

        return false;
    }

    @Override
    public String toString() {
        return command;
    }
}
