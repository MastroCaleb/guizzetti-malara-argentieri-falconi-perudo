import client.Client;
import server.Server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        while(true){
            Scanner scanner = new Scanner(System.in);

            System.out.println("Start Menu");
            System.out.println("1. Start Server");
            System.out.println("2. Start Client");
            System.out.println("3. Exit");
            System.out.println();

            String choice = scanner.nextLine();

            if(choice.equals("1")){
                System.out.println("Port to connect to: ");
                int port = scanner.nextInt();

                startServer(port);
                break;
            }
            else if (choice.equals("2")) {
                System.out.println("Ip to connect to: ");
                String ip = scanner.nextLine();

                System.out.println("Port to connect to: ");
                int port = scanner.nextInt();

                startClient(ip, port);
                break;
            }
            else if(choice.equals("3")){
                break;
            }
        }
    }
    
    static void startClient(String ip, int port){
        new Thread(new Client(ip, port)).start();
    }

    static void startServer(int port){
        new Thread(new Server(port)).start();
    }

}