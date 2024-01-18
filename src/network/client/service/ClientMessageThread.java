package network.client.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import main.Main;
import network.client.Client;

public class ClientMessageThread implements Runnable {
    private final Socket client;
    private final Client clientThread;
    public ClientMessageThread(Socket client, Client clientThread) {
        this.client = client;
        this.clientThread = clientThread;
    }

    @Override
    public void run() {
        try {
            while(this.client.isConnected()) {
                DataInputStream inputStream = new DataInputStream(this.client.getInputStream());
                String message = inputStream.readUTF();

                switch (message) {
                    case "askNickname" -> {
                        Client.canSendNick = true;
                    }
                    case "askCreateOrJoinLobby" -> {
                        Client.canCreateOrJoin = true;
                    }
                    case "askLobbySettings" -> {
                        Client.canSendLobbySettings = true;
                    }
                    case "askForPassword" -> {
                        Client.canSendPassword = true;
                    }
                    case "askStartGame" -> {
                        System.out.println("Minimum number of players reached.");
                        Client.canStartGame = true;
                    }
                    case "askStartBet" -> {
                        Client.canStartBet = true;
                    }
                    case "askAction" -> {
                        Client.canSendAction = true;
                    }
                    case "askNewBet"  -> {
                        Client.canSendNewBet = true;
                    }
                    case "askForSockIt"  -> {
                        Client.canSendSockIt = true;
                    }
                    case "askClean" -> {
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    }
                    default -> {System.out.println(message);}
                }
            }
        }
        catch (IOException | InterruptedException var5) {
            System.out.println("The server encountered problems and shut down.");
            try {
                client.close();
                clientThread.stop();
                Main.main(new String[0]);
            }
            catch (IOException | InterruptedException e) {
                System.out.println("Error has occurred.");
            }
        }
    }
}
