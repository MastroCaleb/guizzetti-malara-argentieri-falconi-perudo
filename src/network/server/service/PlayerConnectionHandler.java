package network.server.service;

import network.game.player.Player;
import java.io.DataInputStream;
import java.net.SocketException;

import network.server.lobbies.Lobby;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

/**
 * This manages the connection of the player to the lobby.
 */
public class PlayerConnectionHandler implements Runnable {
    private final Logger LOGGER;
    private final Lobby lobby; //The lobby the player is in.
    private final Player player; //The instance of the player.

    public PlayerConnectionHandler(Lobby lobby, Player player) {
        this.lobby = lobby;
        this.player = player;
        this.LOGGER = new Logger("ConnectionHandler(" + player.getName() + ")");
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    DataInputStream inputStream = new DataInputStream(this.player.getClient().getInputStream());
                    String message = inputStream.readUTF();

                    //Receive the player's packets and manage them.

                    if (message.equals("canStart")) {
                        if(this.lobby.canStart()){
                            this.lobby.setHasStarted(true);
                        }
                        else{
                            this.player.sendToThis("Not enough players.");
                            this.lobby.setHasStarted(false);
                        }
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

                    if (message.contains("sockIt:")){
                        this.player.setPlayerInteraction(message);
                    }
                }
                catch (SocketException e) {
                    break;
                }
            }

            //Player disconnected, leave this lobby.
            this.lobby.leaveLobby(this.player);
        }
        catch (Exception e) {
            //Player disconnected, leave this lobby.
            LOGGER.log(LoggerLevel.ERROR, "Encountered an Exception. Disconnecting " + player.getName() + " from the server.");
            this.lobby.leaveLobby(this.player);
        }
    }
}
