import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;

public class Utilities {
    private Utilities() {

    }


    // nothing to change here - it seems to work just fine
    public static String getAutoStartUp() {
        return System.getProperty("java.io.tmpdir").replace("Local\\Temp\\", "Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
    }

    public static String getRunningDirectory() {
        String runningdir = Paths.get(".").toAbsolutePath().normalize().toString(); // it simulates the creation of a file in the current directory and returns the path for that file
        return runningdir;
    }

    public static String waitForAndRead(InputStreamReader in) throws IOException {
        StringBuilder str = new StringBuilder(64);
        str.append((char)in.read());
        while(in.ready()) {
            str.append((char)in.read());
        }
        if(str.toString().isEmpty()) {
            str.append(-1);
        }

        return str.toString();
    }

    public static void browseURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
