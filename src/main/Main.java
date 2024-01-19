package main;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import network.client.Client;
import network.server.Server;
import utils.input.In;

public class Main{
    public static String filename = ""; //The file position of this application's ".jar"
    public static void main(String[] args) throws IOException, InterruptedException {
        filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6); //Obtain the ".jar" position.
        if(isConsole()){
            openConsole();
        }
        else{
            startPerudo();
        }
    }

    /**
     * This opens the OS's terminal and runs this program on it instead of the IDE's terminal.
     * This means that we can actually build an Executable Jar.
     */
    private static void openConsole() throws IOException, InterruptedException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            Runtime.getRuntime().exec(getConsoleCommand(filename));
        }
        else{
            startPerudo();
            new ProcessBuilder("cmd", "/c", "exit").inheritIO().start().waitFor();
        }
    }

    /**
     * Starts the program.
     */
    private static void startPerudo(){
        try {
            label:
            while(true) {
                System.out.println("[---START MENU---]");
                System.out.println();
                System.out.println("1. Start Server");
                System.out.println("2. Start Client");
                System.out.println("3. Exit");

                String choice = In.nextLine();

                switch (choice) {
                    case "1": {
                        System.out.println("Enter the port on which to open the server: ");
                        int port = In.nextInt();

                        (new Thread(new Server(port))).start();
                        break label;
                    }
                    case "2": {

                        System.out.println("Enter the IP of the server: ");
                        String ip = In.nextLine();

                        System.out.println("Enter the port of the server: ");
                        int port = In.nextInt();

                        (new Thread(new Client(ip, port))).start();
                        break label;
                    }
                    case "3":
                        break label;
                }
            }
        }
        catch (Exception ignored) {}
    }

    /**
     * @return The array of commands to use based on which Operating System this is being run on.
     */
    private static String[] getConsoleCommand(String filename){
        String os = System.getProperty("os.name");

        if(os.contains("Windows")){
            return new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""};
        }
        else{
            return new String[]{"sh","-c","xdg-open","sh","--init-file","java -jar \"" + filename + "\""}; //don't really know if this works for Linux
        }
    }

    /**
     * @return True if the ".jar" was found, we can open a terminal. False if this program is run on an IDE's terminal.
     */
    public static boolean isConsole(){
        return filename.contains(".jar");
    }
}
