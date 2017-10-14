package Server;

import java.io.*;
import java.net.Socket;

/**
 * Created by mpampis on 7/2/2017.
 */
public class Victim {
    private static int count = 0; // this counts the victims, to give them a unique id

    private int id; // id of the victim
    private Socket socket; // the victim socket
    public PrintWriter out; // this stream will be used to write to the victim
    public InputStreamReader in; // input stream
    public BufferedReader bin; // buffer input stream

    public Victim(Socket socket) throws IOException {
        id = count++; // giving unique id to the victim
        this.socket = socket;
        /**
         * init the streams
         */
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

    /**
     * Two users they are equal, if and only if the have the same IDs
     * @param o the user
     * @return true if the users are equal
     */
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
