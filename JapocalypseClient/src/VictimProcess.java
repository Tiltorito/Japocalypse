import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class VictimProcess {
    private Process p;;
    private PrintWriter writeToProcess;
    private InputStreamReader readFromProcess;

    public VictimProcess(Process p) {
        this.p = p;
        writeToProcess = new PrintWriter(p.getOutputStream());
        readFromProcess = new InputStreamReader(p.getInputStream());
    }


    public String executeCommand(String command, long milliseconds) {
        writeToProcess.println(command);
        writeToProcess.flush();
        String result = "";
        try {
            Thread.sleep(100);
            result = TaskWithTimeOut.executeTask(() -> {
                return waitForAndRead();
            }, milliseconds, TimeUnit.MILLISECONDS);
        }
        catch(InterruptedException e) {

        }

        return result;
    }

    public String readRemainingData() {
        StringBuilder str = new StringBuilder(64);
        try {
            while(readFromProcess.ready()) {
                str.append((char)readFromProcess.read());
            }
        }
        catch(IOException e) {
            return "ERROR\n";
        }

        return str.toString();
    }

    public String waitForAndRead() {
        String result;
        try {
            result = Utilities.waitForAndRead(readFromProcess);
        }
        catch(IOException e) {
            result = null;
        }

        return result;
    }

    public void kill() {
        p.destroy();
    }
}
