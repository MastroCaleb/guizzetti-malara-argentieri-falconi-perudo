package network.server.lobbies;

import network.game.manager.GameManager;
import network.game.player.Player;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import network.server.Server;
import network.server.lobbies.settings.LobbySettings;
import network.server.service.PlayerConnectionHandler;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

/**
 * This class manages a Thread of a Lobby.
 */
public class Lobby implements Runnable {

    /**
     * This lobby's settings.
     */
    private final LobbySettings lobbySettings;
    private final Logger LOGGER;

    /**
     * The players inside this lobby.
     */
    private final LinkedList<Player> players = new LinkedList<>();

    /**
     * The players that disconnected from this lobby.
     */
    private final LinkedList<Player> disconnectedPlayers = new LinkedList<>();

    /**
     * If the lobby asked to start the game.
     */
    private volatile boolean startSent = false;

    /**
     * If the game has started.
     */
    private volatile boolean hasStarted = false;

    /**
     * The host of the lobby, the player that created it.
     */
    private Player host;

    /**
     * If null the game hasn't started. Otherwise, it contains the current game's instance.
     */
    private GameManager gameManager;

    /**
     * The constructor of the class. Also sets the Logger's ID.
     * @param host The player that created the lobby.
     * @param lobbySettings This lobby's settings.
     */
    public Lobby(Player host, LobbySettings lobbySettings) {
        this.host = host;
        this.lobbySettings = lobbySettings;
        this.LOGGER = new Logger("Lobby(" + lobbySettings.getLobbyCode() + ")");
    }

    /**
     * Manages the lobby. Starts games and ends games. If lobby has no players, this closes it.
     */
    @Override
    public void run() {
        try {
            while(true) {
                if(this.isFull()){
                    LOGGER.log(LoggerLevel.INFO, "This lobby is full.");
                }

                //If the game has started and game manager is null we start a new game.
                if (this.hasStarted && this.gameManager == null) {
                    //We set up every player.
                    for(Player player : players){
                        player.removeAllDices();
                        player.setupPlayer(this);
                    }

                    clearAllPlayers();

                    LOGGER.log(LoggerLevel.INFO, "A Game has started.");
                    this.gameManager = new GameManager(this);

                    (new Thread(this.gameManager)).start(); //Start new game.
                }
                //If the game has started, the manager isn't null and the game ended we make gameManager null and reset the booleans.
                else if (this.hasStarted && this.gameManager != null && this.gameManager.hasFinished()) {
                    LOGGER.log(LoggerLevel.INFO, "A Game has ended.");
                    this.hasStarted = false;
                    this.startSent = false;
                    this.gameManager = null;

                    //We ask to restart the game.
                    host.ask("StartGame");
                    this.startSent = true;
                }

                //If there's no players left in the lobby we close it.
                if(this.players.isEmpty()){
                    LOGGER.log(LoggerLevel.WARNING, "This lobby was found empty. Closing the lobby.");
                    Server.lobbies.remove(this);
                    break;
                }
            }
        }
        catch (Exception var3) {
            LOGGER.log(LoggerLevel.ERROR, "This lobby is shutting down after an Exception.");
            Server.lobbies.remove(this);
        }
    }

    /**
     * Makes a player join this lobby.
     * @param player The player that has to join.
     */
    public void joinLobby(Player player) throws IOException {
        player.clean();
        this.players.add(player);
        player.sendToThis("[--PLAYER LIST--]");
        player.sendToThis(this.playerList());
        player.sendToThis("[--LOBBY SETTINGS--]");
        player.sendToThis(lobbySettings.toString());
        PlayerConnectionHandler playerConnectionHandler = new PlayerConnectionHandler(this, player);
        (new Thread(playerConnectionHandler)).start();
        this.sendToAll(player.getName() + " joined the lobby.");
        player.setupPlayer(this);

        if (!this.isFull() && !hasStarted()) {
            this.sendToAll("Waiting for players (" + this.getNumberOfPlayers() + "/" + this.lobbySettings.getMaxPlayers() + ")");
        }

        if(this.canStart() && !hasStarted){
            host.sendToThis("Start the game? Y/N");
        }

        if (this.canStart() && !this.startSent && !this.hasStarted) {
            this.host.ask("StartGame");
            this.startSent = true;
        }
    }

    /**
     * If the player disconnected we make him reconnect with this method.
     * @param player The player that needs to reconnect.
     */
    public void reJoinLobby(Player player) throws IOException {
        player.clean();
        this.players.add(player);
        this.removeDisconnected(player);
        player.sendToThis("[--PLAYER LIST--]");
        player.sendToThis(this.playerList());
        player.sendToThis("[--LOBBY SETTINGS--]");
        player.sendToThis(lobbySettings.toString());
        PlayerConnectionHandler playerConnectionHandler = new PlayerConnectionHandler(this, player);
        (new Thread(playerConnectionHandler)).start();
        this.sendToAll(player.getName() + " re-joined the lobby.");
        player.reSetupPlayer(this, getPlayerWithLeastDices());

        if(this.hasStarted){
            player.sendToThis("");
            player.sendToThis("You will re-join the match the next cycle of turns.");
            player.sendToThis("You will join with the same number of dices as the player with less dices (" + player.getDices().size() + ").");
            player.sendToThis("");
        }

        if (!this.isFull() && !hasStarted()) {
            this.sendToAll("Waiting for players (" + this.getNumberOfPlayers() + "/" + this.lobbySettings.getMaxPlayers() + ")");
        }

        if (this.canStart() && !this.startSent && !this.hasStarted) {
            this.host.ask("StartGame");
            this.startSent = true;
        }

        if(startSent && !hasStarted){
            host.sendToThis("Start the game? Y/N");
        }
    }

    /**
     * Makes a player leave this lobby.
     * @param player The player that has to leave.
     */
    public void leaveLobby(Player player) {
        try{
            player.getClient().close();
            this.players.remove(player);
            this.disconnectedPlayers.add(player);
            if (player.equals(this.host) && !this.players.isEmpty()) {
                this.host = this.players.getFirst();
                this.startSent = false;
            }

            Server.players.remove(player);
            this.sendToAll(player.getName() + " left the lobby.");
            if (this.gameManager != null) {
                this.gameManager.stopWaiting();
            }

            if (!this.isFull() && !hasStarted()) {
                this.sendToAll("Waiting for players (" + this.getNumberOfPlayers() + "/" + this.lobbySettings.getMaxPlayers() + ")");
            }

            if(startSent && !hasStarted){
                host.sendToThis("Start the game? Y/N");
            }
        }
        catch(IOException ignored){}
    }

    /**
     * @return Gets the least number of dices in a player that is still playing.
     */
    public int getPlayerWithLeastDices(){
        int min = players.get(0).getDices().size();
        for(Player player : players){
            if(min > player.getDices().size() && !player.getDices().isEmpty()){
                min = player.getDices().size();
            }
        }

        return min;
    }

    /**
     * @return The total number of players connected.
     */
    public int getNumberOfPlayers() {
        return this.players.size();
    }

    /**
     * @return If the lobby is full or not.
     */
    public boolean isFull() {
        return this.lobbySettings.getMaxPlayers() <= this.players.size();
    }

    /**
     * @return If the lobby is ready to start the game.
     */
    public boolean canStart() {
        return this.getNumberOfPlayers() >= this.lobbySettings.getMinPlayers();
    }

    /**
     * @return If the game already started.
     */
    public boolean hasStarted() {
        return this.hasStarted;
    }

    /**
     * @param player The player we need to check.
     * @return If this player was disconnected from the lobby.
     */
    public boolean wasDisconnected(Player player) {
        for(Player p : disconnectedPlayers){
            if(p.getName().equals(player.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * @return The list of players in a String.
     */
    public String playerList() {
        StringBuilder playerList = new StringBuilder();
        playerList.append("Player List (").append(this.getNumberOfPlayers()).append(") of Lobby with code ").append(lobbySettings.getLobbyCode()).append(": \n");
        playerList.append("\n");

        for(Player player : players) {
            if (player.equals(this.host)) {
                playerList.append("- ").append(player.getName()).append(" (Host)\n");
            }
            else {
                playerList.append("- ").append(player.getName()).append("\n");
            }
        }

        if (this.isFull()) {
            playerList.append("This Lobby is full. \n");
        }

        return playerList.toString();
    }

    /**
     * @return The list of players currently connected.
     */
    public LinkedList<Player> getPlayers() {
        return this.players;
    }

    /**
     * @return The list of players that disconnected.
     */
    public LinkedList<Player> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
    public void setStartSent(boolean startSent) {
        this.startSent = startSent;
    }

    /**
     * @return The game manager of the lobby.
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }

    /**
     * Clears all player's OS's console.
     */
    public void clearAllPlayers() throws IOException {
        for (Player player : players){
            player.clean();
        }
    }

    /**
     * Sends a message to all players.
     * @param message The contents of the message.
     */
    public void sendToAll(String message) {
        if (!this.players.isEmpty()) {
            for(Player p : players) {
                try{
                    DataOutputStream outputStream = new DataOutputStream(p.getClient().getOutputStream());
                    outputStream.writeUTF(message);
                }
                catch(IOException e){
                    this.leaveLobby(p);
                }
            }
        }
    }

    /**
     * Removes from the disconnected list a player that reconnected.
     * @param player The player that reconnected.
     */
    public void removeDisconnected(Player player){
        Player toRemove = null;
        if(!disconnectedPlayers.isEmpty()){
            for(Player p : disconnectedPlayers){
                if(p.getName().equals(player.getName())){
                    toRemove = p;
                }
            }
        }

        if(toRemove != null){
            disconnectedPlayers.remove(toRemove);
        }
    }

    /**
     * @return The total count of players but in String form.
     */
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

