package network.server.service;

import game.player.Player;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import network.server.lobbies.Lobby;

public class PlayerConnectionHandler implements Runnable {
    private Lobby lobby;
    private Player player;

    public PlayerConnectionHandler(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            while (this.player.getClient().isConnected()) {
                try {
                    DataInputStream inputStream = new DataInputStream(this.player.getClient().getInputStream());
                    String message = inputStream.readUTF();

                    if (message.equals("canStart")) {
                        this.lobby.setHasStarted(true);
                    }
                    else if (message.equals("cantStart")) {
                        this.lobby.setStartSent(false);
                    }

                    if (message.contains("startBet:")) {
                        this.player.setPlayerInteraction(message.replace("startBet:", ""));
                        this.lobby.getGameManager().stopWaiting();
                    }
                    else {
                        if (message.contains("diceValue:")) {
                            this.player.setPlayerInteraction(message);
                            this.lobby.getGameManager().stopWaiting();
                        }

                        if (message.contains("diceNumber:")) {
                            this.player.setPlayerInteraction(message);
                            this.lobby.getGameManager().stopWaiting();
                        }
                    }

                    if (message.contains("action:")) {
                        this.player.setPlayerInteraction(message.replace("action:", ""));
                        this.lobby.getGameManager().stopWaiting();
                    }
                }
                catch (SocketException e) {
                    break;
                }
            }

            this.lobby.leaveLobby(this.player);
        }
        catch (IOException e) {
            System.out.println("Error");
        }
    }
}
