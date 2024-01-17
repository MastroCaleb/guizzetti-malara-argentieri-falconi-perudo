package main;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.util.logging.Logger;
import network.client.Client;
import network.server.Server;
import utils.input.In;

public class Main{
    private static final Logger LOGGER = Logger.getLogger("Main");
    public static String filename = "";
    public static void main(String[] args) throws IOException, InterruptedException {
        filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
        if(isConsole()){
            openConsole();
        }
        else{
            startPerudo();
        }
    }

    private static void openConsole() throws IOException, InterruptedException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            Runtime.getRuntime().exec(getConsoleCommand(filename));
        }
        else{
            startPerudo();
            System.out.println("Program has ended, please type 'exit' to close the console");
            new ProcessBuilder("cmd", "/c", "exit").inheritIO().start().waitFor();
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
                else if (choice.equals("3")) {
                    break;
                }
            }
        }
        catch (Exception ignored) {}
    }

    private static String[] getConsoleCommand(String filename){
        String os = System.getProperty("os.name");

        if(os.contains("Windows")){
            return new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""};
        }
        else{
            return new String[]{"sh","-c","xdg-open","sh","--init-file","java -jar \"" + filename + "\""}; //don't really know if this works for Linux
        }
    }

    public static boolean isConsole(){
        return filename.contains(".jar");
    }
}
