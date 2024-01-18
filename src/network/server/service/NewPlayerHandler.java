package network.server.service;

import network.game.player.Player;
import java.io.DataInputStream;
import java.io.IOException;
import network.server.Server;
import network.server.lobbies.Lobby;
import network.server.lobbies.settings.LobbySettings;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

public class NewPlayerHandler implements Runnable {
    private final Logger LOGGER = new Logger("NewPlayerHandler");
    private Player player;

    public NewPlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            while(true) {
                DataInputStream inputStream = new DataInputStream(this.player.getClient().getInputStream());

                while(this.player.getName().isEmpty()) {
                    player.ask("Nickname");

                    String name = inputStream.readUTF();

                    if (!Server.nickIsUsed(name)) {
                        player = new Player(name, player.getClient());
                        break;
                    }
                    else {
                        player.sendToThis("Nickname not available.");
                    }
                }

                Lobby disconnectedLobby = wasDisconnectedFromLobby(player);

                if(disconnectedLobby != null){
                    this.LOGGER.log(LoggerLevel.INFO, "Found reconnection of " + this.player.getName() + ".");

                    player.clean();

                    player.sendToThis("[--RECONNECT TO LOBBY--]");
                    player.sendToThis("");
                    player.sendToThis("1. Reconnect");
                    player.sendToThis("2. No");

                    this.player.ask("Action");

                    String action = inputStream.readUTF().replace("action:", "");

                    if(action.equals("1")){
                        this.player.sendToThis("Reconnecting to this lobby...");
                        disconnectedLobby.reJoinLobby(player);
                        break;
                    }
                }

                player.clean();

                this.LOGGER.log(LoggerLevel.INFO, player.getName() + " has connected to the server.");

                Server.players.add(player);

                this.player.sendToThis("");
                this.player.sendToThis(Server.getLobbyList());
                this.player.sendToThis("");

                this.player.ask("CreateOrJoinLobby");

                String createOrJoin = inputStream.readUTF();

                if (createOrJoin.equals("createLobby")) {
                    this.LOGGER.log(LoggerLevel.INFO, "Creating a new Lobby.");

                    player.clean();

                    this.player.ask("LobbySettings");

                    String settings = inputStream.readUTF();

                    String code = Server.getRandomCode();

                    LobbySettings lobbySettings = new LobbySettings();
                    lobbySettings.read("lobbyCode:"+code+";"+settings);

                    Lobby lobby = new Lobby(this.player, lobbySettings);

                    player.sendToThis("The code for your lobby is: " + code);

                    lobby.joinLobby(this.player);

                    Server.lobbies.add(lobby);
                    (new Thread(lobby)).start();
                    this.LOGGER.log(LoggerLevel.INFO, "New Lobby created.");
                    break;
                }
                else if (createOrJoin.contains("joinLobby:")){

                    String code = createOrJoin.replace("joinLobby:", "");
                    Lobby lobby = Server.getLobbyFromCode(code);

                    if (lobby == null) {
                        player.sendToThis("No Lobby with this code was found.");
                    }
                    else if(lobby.isPublic()){
                        if(!lobby.hasStarted()){
                            this.player.sendToThis("Connecting to this lobby...");
                            lobby.joinLobby(this.player);
                        }
                        else if (lobby.wasDisconnected(player)) {
                            this.player.sendToThis("Reconnecting to this lobby...");
                            lobby.reJoinLobby(this.player);
                        }

                        break;
                    }
                    else if(!lobby.isPublic()){
                        while(true) {
                            this.player.ask("ForPassword");

                            String password = inputStream.readUTF();

                            if (lobby.getSettings().getPassword().equals(password)) {

                                if(!lobby.hasStarted()){
                                    this.player.sendToThis("Correct password.");
                                    this.player.sendToThis("Connecting to this lobby...");
                                    lobby.joinLobby(this.player);
                                }
                                else if (lobby.wasDisconnected(player)) {
                                    this.player.sendToThis("Correct password.");
                                    this.player.sendToThis("Reconnecting to this lobby...");
                                    lobby.reJoinLobby(this.player);
                                }

                                break;
                            }
                            else {
                                this.player.sendToThis("Wrong password.");
                            }
                        }
                        break;
                    }
                }
                else if(createOrJoin.equals("updateLobby")){
                    player.sendToThis("Updating lobby list.");
                    player.sendToThis("");
                }
            }
        }
        catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            Server.players.remove(player);
            LOGGER.log(LoggerLevel.WARNING, player.getName() + " has disconnected from the server.");
        }
    }

    public Lobby wasDisconnectedFromLobby(Player player){
        for (Lobby lobby : Server.lobbies){
            if(lobby.wasDisconnected(player)){
                return lobby;
            }
        }
        return null;
    }

}

