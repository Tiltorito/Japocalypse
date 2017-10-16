package Server;

@FunctionalInterface
public interface VictimExecutable {
    boolean execute(Victim v, String... args);
}
