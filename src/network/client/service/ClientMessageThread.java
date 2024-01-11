package network.client.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import network.client.Client;

public class ClientMessageThread implements Runnable {
    private Socket client;

    public ClientMessageThread(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            while(this.client.isConnected()) {

                DataInputStream inputStream = new DataInputStream(this.client.getInputStream());
                String message = inputStream.readUTF();

                switch (message) {
                    case "askNickName" -> {
                        Client.canSendNick = true;
                        this.stopWaiting();
                    }
                    case "askCreateOrJoinLobby" -> {
                        Client.canCreateOrJoin = true;
                        this.startWaiting();
                    }
                    case "askLobbySettings" -> {
                        Client.canSendLobbySettings = true;
                        this.startWaiting();
                    }
                    case "askForPassword" -> {
                           Client.canSendPassword = true;
                           this.startWaiting();
                    }
                    case "askStartGame" -> {
                        System.out.println("Minimum number of players reached.");
                        Client.canStartGame = true;
                        this.startWaiting();
                    }
                    case "askStartBet" -> {
                        Client.canStartBet = true;
                        this.startWaiting();
                    }
                    case "askAction" -> {
                        Client.canSendAction = true;
                        this.startWaiting();
                    }
                    case "askNewBet"  -> {
                        Client.canSendNewBet = true;
                        this.startWaiting();
                    }
                    default -> {System.out.println(message);}
                }
            }

        }
        catch (IOException var5) {
            System.out.println("Problem getting messages.");
        }
        catch (InterruptedException var6) {
            System.out.println("Thread problem.");
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
