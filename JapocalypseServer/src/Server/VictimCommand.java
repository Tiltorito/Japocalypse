package Server;

public class VictimCommand implements VictimExecutable {
    private String command;
    private VictimExecutable logic;

    public VictimCommand(String c) {
        command = c;
    }

    public void addLogic(VictimExecutable logic) {
        this.logic = logic;
    }

    @Override
    public boolean execute(Victim v, String... args) {
        return logic.execute(v, args);
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
