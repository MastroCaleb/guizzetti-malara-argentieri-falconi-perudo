package network.server.lobbies;

import game.manager.GameManager;
import game.player.Player;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.server.Server;
import network.server.lobbies.settings.LobbySettings;
import network.server.service.PlayerConnectionHandler;

public class Lobby implements Runnable {
    private LobbySettings lobbySettings;
    private Logger LOGGER = Logger.getLogger("Lobby");
    private LinkedList<Player> players = new LinkedList();
    private volatile boolean startSent = false;
    private volatile boolean hasStarted = false;
    private volatile boolean isWaiting = true;
    private Player host;
    private GameManager gameManager;

    public Lobby(Player host, LobbySettings lobbySettings) {
        this.host = host;
        this.lobbySettings = lobbySettings;
    }

    @Override
    public void run() {
        System.out.println("A Lobby was created successfully.");
        try {
            while(true){
                if (this.isWaiting && !this.isFull()) {
                    this.sendToAll("Waiting for players (" + this.getNumberOfPlayers() + "/" + this.lobbySettings.getMaxPlayers() + ")");
                    this.isWaiting = false;
                }

                if (this.canStart() && !this.startSent && !this.hasStarted) {
                    this.host.sendToThis("askStartGame");
                    this.startSent = true;
                }

                if (this.hasStarted && this.gameManager == null) {
                    for(Player player : players){
                        player.rollAll();
                    }

                    this.LOGGER.log(Level.INFO, "Game has started.");
                    this.gameManager = new GameManager(this);

                    (new Thread(this.gameManager)).start();
                }
                else if (this.hasStarted && this.gameManager != null && this.gameManager.hasFinished()) {
                    this.LOGGER.log(Level.INFO, "Game has ended.");
                    this.hasStarted = false;
                    this.startSent = false;
                    this.gameManager = null;
                }

                if(this.players.isEmpty()){
                    this.LOGGER.log(Level.WARNING, "Lobby was found empty. Shutting down.");
                    Server.lobbies.remove(this);
                    break;
                }
            }
        }
        catch (Exception var3) {
            this.LOGGER.log(Level.SEVERE, "Lobby encountered an exception. Shutting down.");
            Server.lobbies.remove(this);
        }
    }

    public void joinLobby(Player player) {
        this.players.add(player);
        PlayerConnectionHandler playerConnectionHandler = new PlayerConnectionHandler(this, player);
        (new Thread(playerConnectionHandler)).start();
        this.sendToAll(player.getName() + " joined the lobby.");
        player.setupPlayer(this);
        this.isWaiting = true;
    }

    public void leaveLobby(Player player) throws IOException {
        player.getClient().close();
        this.players.remove(player);
        if (player.equals(this.host) && !this.players.isEmpty()) {
            this.host = (Player)this.players.getFirst();
            this.startSent = false;
        }

        Server.players.remove(player);
        this.sendToAll(player.getName() + " left the lobby.");
        if (this.gameManager != null) {
            this.gameManager.stopWaiting();
        }

        this.isWaiting = true;
    }

    public int getNumberOfPlayers() {
        return this.players.size();
    }

    public boolean isFull() {
        return this.lobbySettings.getMaxPlayers() <= this.players.size();
    }

    public boolean canStart() {
        return this.getNumberOfPlayers() >= this.lobbySettings.getMinPlayers();
    }

    public boolean hasStarted() {
        return this.hasStarted;
    }

    public void playerList() {
        System.out.println("Player List (" + this.getNumberOfPlayers() + ") : ");
        System.out.println();

        for(Player player : players) {
            if (player.equals(this.host)) {
                System.out.println("- " + player.getName() + " (Host)");
            } else {
                System.out.println("- " + player.getName());
            }
        }

        System.out.println();
        if (this.isFull()) {
            System.out.println("This Lobby is full.");
            System.out.println();
        }
    }

    public LinkedList<Player> getPlayers() {
        return this.players;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void setStartSent(boolean startSent) {
        this.startSent = startSent;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public void sendToAll(String message) {
        if (!this.players.isEmpty()) {
            try {
                for(Player player : players) {
                    DataOutputStream outputStream = new DataOutputStream(player.getClient().getOutputStream());
                    outputStream.writeUTF(message);
                }
            }
            catch (IOException e) {
            }
        }
    }

    public String playerCount() {
        return "(" + this.players.size() + "/" + this.lobbySettings.getMaxPlayers() + ")";
    }

    public String getCode() {
        return this.lobbySettings.getLobbyCode();
    }

    public boolean isPublic() {
        return this.lobbySettings.isPublic();
    }

    public LobbySettings getSettings() {
        return this.lobbySettings;
    }
}

