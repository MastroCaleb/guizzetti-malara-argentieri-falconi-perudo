package network.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import network.client.service.ClientMessageThread;
import network.packets.action.ActionPacket;
import network.packets.bet.BetPacket;
import network.packets.settings.LobbySettingsPacket;
import utils.input.In;

/**
 * This is the main class of the Client. We manage the Player's actions here.
 */
public class Client implements Runnable {

    //COMMANDS
    public static volatile boolean canSendNick = false;
    public static volatile boolean canCreateOrJoin = false;
    public static volatile boolean canSendLobbySettings = false;
    public static volatile boolean canSendPassword = false;
    public static volatile boolean canStartGame = false;
    public static volatile boolean canStartBet = false;
    public static volatile boolean canSendAction = false;
    public static volatile boolean canSendNewBet = false;
    public static volatile boolean canSendSockIt = false;

    //OTHER
    public volatile boolean keepGoing = true;
    private final Socket client;
    private DataOutputStream outputStream;

    public Client(String ip, int port) throws IOException {
        this.client = new Socket(ip, port);
    }

    @Override
    public void run() {
        try {
            outputStream = new DataOutputStream(this.client.getOutputStream());
            ClientMessageThread clientMessageThread = new ClientMessageThread(this.client, this);

            (new Thread(clientMessageThread)).start();

            //This executes the commands given by the server.
            while(keepGoing) {
                if(canSendNick){
                    canSendNick();
                }
                else if (canCreateOrJoin) {
                    canCreateOrJoin();
                }
                else if (canSendLobbySettings) {
                    canSendLobbySettings();
                }
                else if (canSendPassword) {
                    canSendPassword();
                }
                else if (canStartGame) {
                    canStartGame();
                }
                else if (canStartBet) {
                    canStartBet();
                }
                else if (canSendAction) {
                    canSendAction();
                }
                else if (canSendNewBet) {
                    canSendNewBet();
                }
                else if(canSendSockIt){
                    canSendSockIt();
                }
            }
        }
        catch (Exception ignored) {}
    }

    public void canSendNick() throws IOException {
        canSendNick = false;
        System.out.println("[--PROFILE--]");
        System.out.println("Insert your nickname: ");
        String name = In.nextLine();

        outputStream.writeUTF(name);
    }

    public void canCreateOrJoin() throws IOException {
        canCreateOrJoin = false;
        System.out.println("[--CREATE OR JOIN--]");
        System.out.println();
        System.out.println("1. Create New Lobby");
        System.out.println("2. Join Lobby");
        System.out.println("3. Update Lobby List");

        String choice = In.nextLine();

        if(!keepGoing){
            return;
        }

        switch (choice) {
            case "1" -> outputStream.writeUTF("createLobby");
            case "2" -> {
                System.out.println();
                System.out.println("Type the code of the lobby you want to join: ");

                String code = In.nextLine();

                outputStream.writeUTF("joinLobby:" + code);
            }
            case "3" -> outputStream.writeUTF("updateLobby");
        }
    }

    public void canSendLobbySettings() throws IOException {
        canSendLobbySettings = false;

        boolean isPublic;
        String password = "";
        int maxPlayers = 1;
        int minPlayers = 0;
        int maxDices = 0;
        boolean jollies;
        boolean sockIt;

        System.out.println("[--LOBBY SETTINGS--]");
        System.out.println();

        while(true) {
            System.out.println("Lobby type:");
            System.out.println("1. Public");
            System.out.println("2. Private");

            String choice = In.nextLine();

            if(!keepGoing){
                return;
            }

            if (choice.equals("1")) {
                isPublic = true;
                break;
            }
            else if (choice.equals("2")) {
                isPublic = false;
                System.out.println("Type in the password for the lobby:");
                password = In.nextLine();

                if(!keepGoing){
                    return;
                }

                break;
            }
            else{
                System.out.println();
                System.out.println("Not a choice.");
                System.out.println();
            }
        }


        while(maxPlayers <= 1) {
            System.out.println("Set MAX number of Players in the lobby: (Default is 6, minimum is 2)");
            maxPlayers = In.nextInt();

            if(!keepGoing){
                return;
            }
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

        while(true) {
            System.out.println("Allow to declare Sock It?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            String choice = In.nextLine();

            if (choice.equals("1")) {
                sockIt = true;
                break;
            }
            else if (choice.equals("2")) {
                sockIt = false;
                break;
            }
            else{
                System.out.println();
                System.out.println("Not a choice.");
                System.out.println();
            }
        }

        LobbySettingsPacket lobbySettingsPacket = new LobbySettingsPacket(isPublic, password, maxPlayers, minPlayers, maxDices, jollies, sockIt);

        outputStream.writeUTF(lobbySettingsPacket.write());
    }

    public void canSendPassword() throws IOException {
        canSendPassword = false;

        System.out.println("Enter the password: ");
        String password = In.nextLine();

        outputStream.writeUTF(password);
    }

    public void canStartGame() throws IOException {
        canStartGame = false;

        String choice = In.nextLine();

        if (!choice.equals("Y") && !choice.equals("y")) {
            outputStream.writeUTF("cantStart");
        }
        else {
            outputStream.writeUTF("canStart");
        }
    }

    public void canStartBet() throws IOException {
        canStartBet = false;

        System.out.println("Insert the dice value of the bet: ");
        int diceValue = In.nextInt();
        System.out.println("Insert the dice number of the bet: ");
        int diceNumber = In.nextInt();

        BetPacket betPacket = new BetPacket(diceValue, diceNumber);

        outputStream.writeUTF("startBet:" + betPacket.write());
    }

    public void canSendAction() throws IOException {
        canSendAction = false;

        String choice = In.nextLine();

        if (choice.equals("1") || choice.equals("2")) {
            outputStream.writeUTF(new ActionPacket(choice).getChoice());
        }
        else {
            outputStream.writeUTF(new ActionPacket("error").getChoice());
        }
    }

    public void canSendNewBet() throws IOException {
        canSendNewBet = false;

        String choice = In.nextLine();
        if (choice.equals("1")) {
            System.out.println("Change dice value of the bet: ");
            String diceValue = In.nextLine();

            if(diceValue.equals("J")){
                diceValue = "1";
            }

            outputStream.writeUTF("diceValue:" + diceValue);
        }
        else if (choice.equals("2")) {
            System.out.println("Change dice number of the bet: ");
            int diceNumber = In.nextInt();

            outputStream.writeUTF("diceNumber:" + diceNumber);
        }
    }

    public void canSendSockIt() throws IOException {
        canSendSockIt = false;

        System.out.println();
        System.out.println("[--CALL SOCK IT--]");
        System.out.println("Calling Sock It checks if the last bet is PERFECT.");
        System.out.println("- If it is you get a dice (WIN).");
        System.out.println("- If it's not you lose a dice (LOSE).");
        System.out.println();
        System.out.println("1. Sock It");
        System.out.println("2. No");
        System.out.println();

        String choice = In.nextLine();

        if (choice.equals("1")) {
            outputStream.writeUTF("sockIt:Y");
        }
        else {
            outputStream.writeUTF("sockIt:N");
        }
    }

    public void stop() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        keepGoing = false;
    }
}
