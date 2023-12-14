import client.Client;
import game.bet.BetPacket;
import game.dices.DiceValue;
import server.Server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        BetPacket test = new BetPacket(3, DiceValue.TWO);
        String testString = test.toString();

        BetPacket test2 = new BetPacket();
        test2.fromString(testString);



        Scanner scanner = new Scanner(System.in);

        System.out.println("Menu");
        System.out.println("1. Start Server");
        System.out.println("2. Start Client");

        String choice = scanner.nextLine();

        if(choice.equals("1")){
            System.out.println("Port to connect to: ");
            int port = scanner.nextInt();

            startServer(port);
        }
        else if (choice.equals("2")) {
            System.out.println("Ip to connect to: ");
            String ip = scanner.nextLine();

            System.out.println("Port to connect to: ");
            int port = scanner.nextInt();

            startClient(ip, port);
        }

    }
    
    static void startClient(String ip, int port){
        new Thread(new Client(ip, port)).start();
    }

    static void startServer(int port){
        new Thread(new Server(port)).start();
    }

}