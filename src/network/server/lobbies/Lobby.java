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

public class Lobby implements Runnable {
    private final LobbySettings lobbySettings;
    private final Logger LOGGER;
    private final LinkedList<Player> players = new LinkedList<>();
    private final LinkedList<Player> disconnectedPlayers = new LinkedList<>();
    private volatile boolean startSent = false;
    private volatile boolean hasStarted = false;
    private Player host;
    private GameManager gameManager;

    public Lobby(Player host, LobbySettings lobbySettings) {
        this.host = host;
        this.lobbySettings = lobbySettings;
        this.LOGGER = new Logger("Lobby(" + lobbySettings.getLobbyCode() + ")");
    }

    @Override
    public void run() {
        try {
            while(true) {
                if(this.isFull()){
                    LOGGER.log(LoggerLevel.INFO, "This lobby is full.");
                }

                if (this.hasStarted && this.gameManager == null) {
                    for(Player player : players){
                        player.removeAllDices();
                        player.setupPlayer(this);
                    }
                    clearAllPlayers();
                    LOGGER.log(LoggerLevel.INFO, "A Game has started.");
                    this.gameManager = new GameManager(this);

                    (new Thread(this.gameManager)).start();
                }
                else if (this.hasStarted && this.gameManager != null && this.gameManager.hasFinished()) {
                    LOGGER.log(LoggerLevel.INFO, "A Game has ended.");
                    this.hasStarted = false;
                    this.startSent = false;
                    this.gameManager = null;
                }

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

    public int getPlayerWithLeastDices(){
        int min = players.get(0).getDices().size();
        for(Player player : players){
            if(min > player.getDices().size() && !player.getDices().isEmpty()){
                min = player.getDices().size();
            }
        }

        return min;
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
    @SuppressWarnings("all")
    public boolean hasStarted() {
        return this.hasStarted;
    }
    public boolean wasDisconnected(Player player) {
        for(Player p : disconnectedPlayers){
            if(p.getName().equals(player.getName())){
                return true;
            }
        }
        return false;
    }
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
    public LinkedList<Player> getPlayers() {
        return this.players;
    }
    public LinkedList<Player> getDisconnectedPlayers() {
        return disconnectedPlayers;
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
    public void clearAllPlayers() throws IOException {
        for (Player player : players){
            player.ask("Clean");
        }
    }
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
    public void sendToAllExcept(String message, Player except) {
        if (!this.players.isEmpty()) {
            for(Player p : players) {
                if(!p.equals(except)){
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
    }
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

