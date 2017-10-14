package Server;

import java.util.List;

/**
 * Created by mpampis on 10/14/2017.
 */
public class Utilities {
    private Utilities() {

    }

    /**
     * Takes a list and an object and returns the index of the object inside the list.
     * @param list the list
     * @param object the object
     * @return the index of the object inside the list
     */
    public static int indexOf(List<?> list, Object object) {
        for(int i = 0; i < list.size();i++) {
            if(list.get(i).equals(object)) {
                return i;
            }
        }

        return -1;
    }
}
