package Server;

import java.io.*;
import java.net.Socket;

/**
 * Created by mpampis on 7/2/2017.
 */
public class Victim {
    private static int count = 0;
    private int id;
    private Socket socket;
    public PrintWriter out;
    public InputStreamReader in;
    public BufferedReader bin;

    public Victim(Socket socket) throws IOException {
        id = count++;
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream());
        in = new InputStreamReader(socket.getInputStream());
        bin = new BufferedReader(in);
    }

    public void executeCommand(String command) {

    }

    public void closeStreams() {
        out.close();
        try {
            in.close();
        }
        catch(IOException ex) {

        }
    }

    public int getID() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Victim) {
            Victim v = (Victim)o;
            if(id == v.id)
                return true;
        }
        else if(o instanceof Integer) {
            return id == (Integer)o;
        }

        return false;
    }

    public String waitForAndRead() throws IOException {
        StringBuilder str = new StringBuilder(64);
        str.append((char)in.read());
        while (in.ready()) {
            str.append((char) in.read());
        }

        return str.toString();
    }

    @Override
    public String toString() {
        return id + " " + socket.getInetAddress().getHostAddress();
    }
}
