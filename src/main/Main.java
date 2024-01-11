package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import network.client.Client;
import network.server.Server;
import utils.In;

public class Main {
    private static Logger LOGGER = Logger.getLogger("Main");

    public Main() {
    }

    public static void main(String[] args) {
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
}
