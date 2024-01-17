package main;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.client.Client;
import network.server.Server;
import utils.input.In;

public class Main {
    private static final Logger LOGGER = Logger.getLogger("Main");
    static boolean console = false; //Make true only for jar builds

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        if(console){
            openConsole();
        }
        else{
            startPerudo();
        }
    }

    private static void openConsole() throws IOException, InterruptedException, URISyntaxException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(getConsoleCommand(filename));
        }
        else{
            startPerudo();
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
    }

    private static void startPerudo(){
        try {
            while(true) {
                System.out.println("[---START MENU---]");
                System.out.println();
                System.out.println("1. Start Server");
                System.out.println("2. Start Client");
                System.out.println("3. Exit");

                String choice = In.nextLine();

                if (choice.equals("1")) {
                    System.out.println("Enter the port on which to open the server: ");
                    int port = In.nextInt();

                    (new Thread(new Server(port))).start();
                    break;
                }
                else if (choice.equals("2")) {

                    System.out.println("Enter the IP of the server: ");
                    String ip = In.nextLine();

                    System.out.println("Enter the port of the server: ");
                    int port = In.nextInt();

                    (new Thread(new Client(ip, port))).start();
                    break;
                }
                else if (!choice.equals("3")) {
                    break;
                }
            }
        }
        catch (Exception var4) {
            LOGGER.log(Level.SEVERE, "IOException encountered. Not the correct input.");
        }
    }

    private static String[] getConsoleCommand(String filename){
        String os = System.getProperty("os.name");

        if(os.contains("Windows")){
            return new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""};
        }
        else{
            return new String[]{"sh","-c","xdg-open","sh","--init-file","java -jar \"" + filename + "\""};
        }
    }
}
