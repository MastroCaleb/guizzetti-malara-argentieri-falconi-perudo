package network.client.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import network.client.Client;

public class ClientMessageThread implements Runnable {
    private final Socket client;

    public ClientMessageThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while(this.client.isConnected()) {
                DataInputStream inputStream = new DataInputStream(this.client.getInputStream());
                String message = inputStream.readUTF();

                switch (message) {
                    case "askNickName" -> {
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
                    case "askToReconnect" -> {
                        Client.canReconnect = true;
                    }
                    default -> {System.out.println(message);}
                }
            }
        }
        catch (IOException var5) {
            System.out.println("Problem getting messages.");
        }
    }

    public void startWaiting() throws InterruptedException {
        synchronized(this) {
            this.wait();
        }
    }

    public void stopWaiting() {
        synchronized(this) {
            this.notify();
        }
    }
}
