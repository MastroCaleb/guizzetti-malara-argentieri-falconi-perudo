package network.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.client.service.ClientMessageThread;
import network.packets.settings.LobbySettingsPacket;
import utils.In;

public class Client implements Runnable {
    public static volatile boolean canSendNick = false;
    public static volatile boolean canCreateOrJoin = false;
    public static volatile boolean canSendLobbySettings = false;
    public static volatile boolean canSendPassword = false;
    public static volatile boolean canStartGame = false;
    public static volatile boolean canStartBet = false;
    public static volatile boolean canSendAction = false;
    public static volatile boolean canSendNewBet = false;
    private static Logger LOGGER = Logger.getLogger("Client");
    private String ip;
    private int port;
    private Socket client;

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.client = new Socket(ip, port);
    }

    @Override
    public void run() {
        try {
            DataOutputStream outputStream = new DataOutputStream(this.client.getOutputStream());
            ClientMessageThread clientMessageThread = new ClientMessageThread(this.client);

            (new Thread(clientMessageThread)).start();

            while(true) {
                if(canSendNick){
                    canSendNick = false;
                    System.out.println("[--PROFILE--]");
                    System.out.println("Insert your nickname: ");
                    String name = In.nextLine();

                    clientMessageThread.stopWaiting();

                    outputStream.writeUTF(name);
                }
                else if (canCreateOrJoin) {
                    canCreateOrJoin = false;
                    System.out.println("[--CREATE OR JOIN--]");
                    System.out.println();
                    System.out.println("1. Create New Lobby");
                    System.out.println("2. Join Lobby");
                    System.out.println("3. Update Lobby List");

                    String choice = In.nextLine();

                    clientMessageThread.stopWaiting();

                    if (choice.equals("1")) {

                        outputStream.writeUTF("createLobby");
                    }
                    else if (choice.equals("2")) {
                        System.out.println();
                        System.out.println("Type the code of the lobby you want to join: ");

                        String code = In.nextLine();

                        outputStream.writeUTF("joinLobby:" + code);
                    }
                    else if (choice.equals("3")){
                        outputStream.writeUTF("updateLobby");
                    }
                }
                else if (canSendLobbySettings) {
                    canSendLobbySettings = false;

                    boolean isPublic;
                    String password = "";
                    int maxPlayers = 1;
                    int minPlayers = 0;
                    int maxDices = 0;
                    boolean jollies;

                    System.out.println("[--LOBBY SETTINGS--]");
                    System.out.println();

                    while(true) {
                        System.out.println("Lobby type:");
                        System.out.println("1. Public");
                        System.out.println("2. Private");

                        String choice = In.nextLine();

                        if (choice.equals("1")) {
                            isPublic = true;
                            break;
                        }
                        else if (choice.equals("2")) {
                            isPublic = false;
                            System.out.println("Type in the password for the lobby:");
                            password = In.nextLine();
                            break;
                        }

                        System.out.println();
                        System.out.println("Not a choice.");
                        System.out.println();
                    }


                    while(maxPlayers <= 1) {
                        System.out.println("Set MAX number of Players in the lobby: (Default is 6, minimum is 2)");
                        maxPlayers = In.nextInt();
                    }

                    while(minPlayers <= 1) {
                        System.out.println("Set MIN number of Players in the lobby: (Default is 2, minimum is 2)");
                        minPlayers = In.nextInt();
                    }

                    while(maxDices <= 0) {
                        System.out.println("Set MAX number of dices for each Player in the lobby: (Default is 5, minimum is 1)");
                        maxDices = In.nextInt();
                    }

                    while(true) {
                        System.out.println("Allow Jollies?");
                        System.out.println("1. Yes");
                        System.out.println("2. No");

                        String choice = In.nextLine();

                        if (choice.equals("1")) {
                            jollies = true;
                            break;
                        }
                        else if (choice.equals("2")) {
                            jollies = false;
                            break;
                        }
                        else{
                            System.out.println();
                            System.out.println("Not a choice.");
                            System.out.println();
                        }
                    }

                    LobbySettingsPacket lobbySettingsPacket = new LobbySettingsPacket(isPublic, password, maxPlayers, minPlayers, maxDices, jollies);

                    clientMessageThread.stopWaiting();

                    System.out.println(lobbySettingsPacket.write());
                    outputStream.writeUTF(lobbySettingsPacket.write());

                }
                else if (canSendPassword) {
                    canSendPassword = false;

                    System.out.println("Enter the password: ");
                    String password = In.nextLine();

                    clientMessageThread.stopWaiting();

                    outputStream.writeUTF(password);
                }
                else if (canStartGame) {
                    canStartGame = false;

                    System.out.println("Start the game? Y/N");
                    String choice = In.nextLine();

                    clientMessageThread.stopWaiting();

                    if (!choice.equals("Y") && !choice.equals("y")) {
                        outputStream.writeUTF("cantStart");
                    }
                    else {
                        outputStream.writeUTF("canStart");
                    }
                }
                else {

                    if (canStartBet) {
                        canStartBet = false;

                        System.out.println("Insert the dice value of the bet: ");
                        int diceValue = In.nextInt();
                        System.out.println("Insert the dice number of the bet: ");
                        int diceNumber = In.nextInt();

                        clientMessageThread.stopWaiting();

                        outputStream.writeUTF("startBet:" + "diceValue:" + diceValue + ";" + "diceNumber:" + diceNumber);
                    }
                    else if (canSendAction) {
                        canSendAction = false;

                        System.out.println("[--DOUBT OR BET--]");
                        System.out.println();
                        System.out.println("1. Doubt");
                        System.out.println("2. Bet Again");

                        String choice = In.nextLine();

                        clientMessageThread.stopWaiting();

                        if (choice.equals("1") || choice.equals("2")) {
                            outputStream.writeUTF("action:" + choice);
                        }
                        else {
                            outputStream.writeUTF("action:error");
                        }
                    } else if (canSendNewBet) {
                        canSendNewBet = false;

                        String choice = In.nextLine();
                        if (choice.equals("1")) {
                            System.out.println("Change dice value of the bet: ");
                            int diceNumber = In.nextInt();

                            clientMessageThread.stopWaiting();

                            outputStream.writeUTF("diceValue:" + diceNumber);
                        }
                        else if (choice.equals("2")) {
                            System.out.println("Change dice number of the bet: ");
                            int diceNumber = In.nextInt();

                            clientMessageThread.stopWaiting();

                            outputStream.writeUTF("diceNumber:" + diceNumber);
                        }
                    }
                }
            }
        }
        catch (Exception var10) {
            LOGGER.log(Level.SEVERE, "Client Exception, closing client.");
        }
    }
}
