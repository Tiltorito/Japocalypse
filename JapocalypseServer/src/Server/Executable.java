package Server;

/**
 * Created by mpampis on 7/2/2017.
 */

@FunctionalInterface
public interface Executable {
    public boolean execute(String... args);
}
