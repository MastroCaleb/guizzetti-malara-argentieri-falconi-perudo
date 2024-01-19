package network.client.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import main.Main;
import network.client.Client;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

/**
 * This class manages the Server's packets that arrive to the client.
 */
public class ClientMessageThread implements Runnable {
    private final Logger LOGGER = new Logger("Client");
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

                //This switch statement checks for commands from the server, if no command was found then we just simply print out the message.
                switch (message) {
                    case "askNickname" -> Client.canSendNick = true;
                    case "askCreateOrJoinLobby" -> Client.canCreateOrJoin = true;
                    case "askLobbySettings" -> Client.canSendLobbySettings = true;
                    case "askForPassword" -> Client.canSendPassword = true;
                    case "askStartGame" -> {
                        System.out.println("Minimum number of players reached to start the game.");
                        Client.canStartGame = true;
                    }
                    case "askStartBet" -> Client.canStartBet = true;
                    case "askAction" -> Client.canSendAction = true;
                    case "askNewBet"  -> Client.canSendNewBet = true;
                    case "askForSockIt"  -> Client.canSendSockIt = true;
                    case "askClean" -> {
                        if(Main.isConsole()){
                            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); //Cleans the OS's terminal
                        }
                    }
                    default -> System.out.println(message);
                }
            }
        }
        catch (IOException | InterruptedException var5) {
            //We handle the disconnection from the server after it shut down.
            LOGGER.log(LoggerLevel.WARNING, "The server shut down.");
            try {
                client.close();
                clientThread.stop();
                Main.main(new String[0]);
            }
            catch (IOException | InterruptedException e) {
                LOGGER.log(LoggerLevel.ERROR, "An error occurred, closing this thread.");
            }
        }
    }
}
